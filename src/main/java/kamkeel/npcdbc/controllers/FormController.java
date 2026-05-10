package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.form.IFormHandler;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormScript;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.DBCInfoSyncPacket;
import kamkeel.npcs.network.enums.EnumSyncAction;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.controllers.CategoryManager;
import noppes.npcs.controllers.TagController;
import noppes.npcs.controllers.data.Category;
import noppes.npcs.util.NBTJsonUtil;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

public class FormController implements IFormHandler {
    public static FormController Instance = new FormController();
    public HashMap<Integer, Form> customFormsSync = new HashMap();
    public HashMap<Integer, Form> customForms;
    public HashMap<Integer, FormScript> customFormsScripts;
    private HashMap<Integer, String> bootOrder;
    private int lastUsedID = 0;

    public CategoryManager categoryManager = new CategoryManager();

    public FormController() {
        Instance = this;
        customForms = new HashMap<>();
        bootOrder = new HashMap<>();
    }

    public void load() {
        customForms = new HashMap<>();
        bootOrder = new HashMap<>();
        lastUsedID = 0;
        LogWriter.info("Loading custom forms...");
        readCustomFormMap();
        loadForms();
        LogWriter.info("Done loading custom forms.");
    }

    public IForm createForm(String name) {
        if (hasName(name))
            return get(name);
        else {
            Form form = new Form();
            form.name = name;
            if (form.id == -1) {
                form.id = getUnusedId();
            }
            int setID = form.id;
            while (bootOrder.containsKey(setID) || customForms.containsKey(setID)) {
                if (bootOrder.containsKey(setID))
                    if (bootOrder.get(setID).equals(form.name))
                        break;

                setID++;
            }
            form.id = setID;
            customForms.put(form.id, form);
            form.save();
            return form;
        }
    }

    private void loadForms() {
        customForms.clear();

        File dir = getDir();
        if (!dir.exists()) {
            dir.mkdir();
        }

        categoryManager.loadCategories(dir);

        // Load uncategorized forms (root level .json files)
        loadFormsFromDir(dir, CategoryManager.UNCATEGORIZED_ID);

        // Load categorized forms (subdirectories)
        for (Map.Entry<Integer, Category> entry : categoryManager.getCategories().entrySet()) {
            File catDir = categoryManager.getCategoryDir(entry.getKey());
            loadFormsFromDir(catDir, entry.getKey());
        }

        verifyLinkedForms();
        saveFormLoadMap();
    }

    private void loadFormsFromDir(File dir, int catId) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".json"))
                continue;
            try {
                Form form = new Form();
                form.readFromNBT(NBTJsonUtil.LoadFile(file));
                form.name = file.getName().substring(0, file.getName().length() - 5);

                if (form.id == -1) {
                    form.id = getUnusedId();
                }

                int originalID = form.id;
                int setID = form.id;
                while (bootOrder.containsKey(setID) || customForms.containsKey(setID)) {
                    if (bootOrder.containsKey(setID))
                        if (bootOrder.get(setID).equals(form.name))
                            break;

                    setID++;
                }

                form.id = setID;
                if (originalID != setID) {
                    LogWriter.info("Found Custom Form ID Mismatch: " + form.name + ", New ID: " + setID);
                    form.save();
                }

                customForms.put(form.id, form);
                categoryManager.registerItem(form.id, catId);
            } catch (Exception e) {
                LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            }
        }
    }

    private void verifyLinkedForms() {
        for (Form form : customForms.values()) {
            if (!has(form.childID))
                form.childID = -1;
            if (!has(form.parentID))
                form.parentID = -1;
        }
    }

    private File getDir() {
        return new File(CustomNpcs.getWorldSaveDirectory(), "customforms");
    }

    public int getUnusedId() {
        for (int catid : customForms.keySet()) {
            if (catid > lastUsedID)
                lastUsedID = catid;
        }
        lastUsedID++;
        return lastUsedID;
    }

    public IForm saveForm(IForm customForm) {
        if (customForm.getID() < 0) {
            customForm.setID(getUnusedId());
            while (hasName(customForm.getName()))
                customForm.setName(customForm.getName() + "_");
        } else {
            Form existing = customForms.get(customForm.getID());
            if (existing != null && !existing.name.equals(customForm.getName()))
                while (hasName(customForm.getName()))
                    customForm.setName(customForm.getName() + "_");
        }

        TagController.validateTagUUIDs(((Form) customForm).tagUUIDs);
        customForms.remove(customForm.getID());
        customForms.put(customForm.getID(), (Form) customForm);

        saveFormLoadMap();

        File dir = categoryManager.getItemDir(customForm.getID());
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, customForm.getName() + ".json_new");
        File file2 = new File(dir, customForm.getName() + ".json");

        try {
            NBTTagCompound nbtTagCompound = ((Form) customForm).writeToNBT(true);
            NBTJsonUtil.SaveFile(file, nbtTagCompound);
            if (file2.exists())
                file2.delete();
            file.renameTo(file2);
            DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.FORM, EnumSyncAction.UPDATE, -1, nbtTagCompound));
        } catch (Exception e) {
            LogWriter.except(e);
        }
        return customForms.get(customForm.getID());
    }

    public Form cloneForm(int originalId) {
        Form original = customForms.get(originalId);
        if (original == null) return null;

        int originalCatId = categoryManager.getItemCategory(originalId);

        Form clone = new Form();
        clone.readFromNBT(original.writeToNBT(true));
        clone.id = getUnusedId();
        clone.parentID = -1;
        clone.childID = -1;

        String name = clone.name;
        while (hasName(name)) name += "_";
        clone.name = name;

        if (originalCatId > CategoryManager.UNCATEGORIZED_ID) {
            categoryManager.registerItem(clone.id, originalCatId);
        }

        saveForm(clone);
        return clone;
    }

    public boolean hasName(String newName) {
        if (newName.trim().isEmpty())
            return true;
        for (Form form : customForms.values())
            if (form.name.equals(newName))
                return true;
        return false;
    }

    public void delete(String name) {
        Form delete = getFormFromName(name);
        if (delete != null) {
            delete(delete.id);
        }
    }

    public void delete(int id) {
        if (!this.customForms.containsKey(id))
            return;

        Form foundForm = this.customForms.remove(id);
        if (foundForm != null && foundForm.name != null) {
            customFormsScripts.remove(foundForm.getID());
            File dir = categoryManager.getItemDir(id);
            File file = new File(dir, foundForm.name + ".json");
            if (file.exists()) {
                file.delete();
            }
            categoryManager.removeItem(id);
            DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.FORM, EnumSyncAction.REMOVE, foundForm.getID(), new NBTTagCompound()));
            saveFormLoadMap();
        }
    }

    public boolean has(String name) {
        return getFormFromName(name) != null;
    }

    public boolean has(int id) {
        return get(id) != null;
    }

    public IForm get(String name) {
        return getFormFromName(name);
    }

    public IForm get(int id) {
        if (id == -1)
            return null;
        return this.customForms.get(id);
    }

    public IForm[] getForms() {
        ArrayList<IForm> customForms = new ArrayList<>(this.customForms.values());
        return customForms.toArray(new IForm[0]);
    }

    public Form getFormFromName(String formName) {
        for (Map.Entry<Integer, Form> entryForm : FormController.getInstance().customForms.entrySet()) {
            if (entryForm.getValue().name.equalsIgnoreCase(formName)) {
                return entryForm.getValue();
            }
        }
        return null;
    }

    public String[] getNames() {
        String[] names = new String[customForms.size()];
        int i = 0;
        for (Form customForm : customForms.values()) {
            names[i] = customForm.name.toLowerCase();
            i++;
        }
        return names;
    }

    public File getMapDir() {
        File dir = CustomNpcs.getWorldSaveDirectory();
        if (!dir.exists())
            dir.mkdir();
        return dir;
    }

    ////////////////////////////////////////////////////////
    // CUSTOM FORM MAP
    ////////////////////////////////////////////////////////

    public void readCustomFormMap() {
        bootOrder.clear();

        try {
            File file = new File(getMapDir(), "customforms.dat");
            if (file.exists()) {
                loadCustomFormMap(file);
            }
        } catch (Exception e) {
            try {
                File file = new File(getMapDir(), "customforms.dat_old");
                if (file.exists()) {
                    loadCustomFormMap(file);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public NBTTagCompound writeMapNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList formList = new NBTTagList();
        for (Integer key : customForms.keySet()) {
            Form customForm = customForms.get(key);
            if (!customForm.getName().isEmpty()) {
                NBTTagCompound formCompound = new NBTTagCompound();
                formCompound.setString("Name", customForm.getName());
                formCompound.setInteger("ID", key);

                formList.appendTag(formCompound);
            }
        }
        nbt.setTag("CustomForms", formList);
        nbt.setInteger("lastID", lastUsedID);
        return nbt;
    }

    public void readMapNBT(NBTTagCompound compound) {
        lastUsedID = compound.getInteger("lastID");
        NBTTagList list = compound.getTagList("CustomForms", 10);
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
                String formName = nbttagcompound.getString("Name");
                Integer key = nbttagcompound.getInteger("ID");
                bootOrder.put(key, formName);
            }
        }
    }

    private void loadCustomFormMap(File file) throws IOException {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
        readCustomFormMap(var1);
        var1.close();
    }

    public void readCustomFormMap(DataInputStream stream) throws IOException {
        NBTTagCompound nbtCompound = CompressedStreamTools.read(stream);
        this.readMapNBT(nbtCompound);
    }

    public void saveFormLoadMap() {
        try {
            File saveDir = getMapDir();
            File file = new File(saveDir, "customforms.dat_new");
            File file1 = new File(saveDir, "customforms.dat_old");
            File file2 = new File(saveDir, "customforms.dat");
            CompressedStreamTools.writeCompressed(this.writeMapNBT(), new FileOutputStream(file));
            if (file1.exists()) {
                file1.delete();
            }
            file2.renameTo(file1);
            if (file2.exists()) {
                file2.delete();
            }
            file.renameTo(file2);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            LogWriter.except(e);
        }
    }

    public static FormController getInstance() {
        return Instance;
    }

    public void deleteFormFile(String name) {
        categoryManager.deleteFile(name + ".json");
    }

    ////////////////////////////////////////////////////////
    // CATEGORY HELPERS
    ////////////////////////////////////////////////////////

    public Map<String, Integer> getCategoryScrollData() {
        return categoryManager.getCategoryScrollData();
    }

    public Map<String, Integer> getItemsByCategoryScrollData(int catId) {
        Map<String, Integer> map = new HashMap<>();
        List<Integer> itemIds = categoryManager.getItemsInCategory(catId, customForms.keySet());
        for (int itemId : itemIds) {
            Form form = customForms.get(itemId);
            if (form != null) {
                map.put(form.name, form.id);
            }
        }
        return map;
    }

    public HashMap<String, HashSet<UUID>> getItemTagMapForCategory(int catId) {
        HashMap<String, HashSet<UUID>> tagMap = new HashMap<>();
        List<Integer> itemIds = categoryManager.getItemsInCategory(catId, customForms.keySet());
        for (int itemId : itemIds) {
            Form form = customForms.get(itemId);
            if (form != null && !form.tagUUIDs.isEmpty()) {
                tagMap.put(form.name, form.tagUUIDs);
            }
        }
        return tagMap;
    }

    public void moveItemToCategory(int itemId, int catId) {
        Form form = customForms.get(itemId);
        if (form == null) return;
        categoryManager.moveItem(itemId, form.name + ".json", catId);
        saveFormLoadMap();
    }

    public Category createCategory(String name) {
        return categoryManager.createCategory(name);
    }

    public void saveCategory(Category cat) {
        categoryManager.saveCategory(cat);
    }

    public boolean removeCategory(int catId) {
        return categoryManager.removeCategory(catId, customForms.keySet());
    }

    public Category getCategory(int catId) {
        return categoryManager.getCategory(catId);
    }
}
