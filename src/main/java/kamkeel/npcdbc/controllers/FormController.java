package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.ICustomForm;
import kamkeel.npcdbc.api.IFormHandler;
import kamkeel.npcdbc.data.CustomForm;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.SyncType;
import noppes.npcs.util.NBTJsonUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class FormController implements IFormHandler {
    public static FormController Instance = new FormController();
    public HashMap<Integer, CustomForm> customFormsSync = new HashMap();
    public HashMap<Integer, CustomForm> customForms;
    private HashMap<Integer, String> bootOrder;
    private int lastUsedID = 0;

    public FormController() {
        Instance = this;
        customForms = new HashMap<>();
        bootOrder = new HashMap<>();
    }

    public void load() {
        LogWriter.info("Loading custom forms...");
        readCustomFormMap();
        loadForms();
        LogWriter.info("Done loading custom forms.");
    }

    public ICustomForm createForm(String name) {
        if (hasName(name))
            return get(name);
        else {
            CustomForm form = new CustomForm();
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
        } else {
            for (File file : dir.listFiles()) {
                if (!file.isFile() || !file.getName().endsWith(".json"))
                    continue;
                try {
                    CustomForm form = new CustomForm();
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
                } catch (Exception e) {
                    LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
                }
            }
        }
        verifyFormTree();
        saveFormLoadMap();
    }

    private void verifyFormTree() {
        for (CustomForm form : customForms.values()) {
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

    public ICustomForm saveForm(ICustomForm customForm) {
        if (customForm.getID() < 0) {
            customForm.setID(getUnusedId());
            while (hasName(customForm.getName()))
                customForm.setName(customForm.getName() + "_");
        } else {
            CustomForm existing = customForms.get(customForm.getID());
            if (existing != null && !existing.name.equals(customForm.getName()))
                while (hasName(customForm.getName()))
                    customForm.setName(customForm.getName() + "_");
        }

        customForms.remove(customForm.getID());
        customForms.put(customForm.getID(), (CustomForm) customForm);

        saveFormLoadMap();

        // Save CustomForm File
        File dir = this.getDir();
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, customForm.getName() + ".json_new");
        File file2 = new File(dir, customForm.getName() + ".json");

        try {
            NBTTagCompound nbtTagCompound = ((CustomForm) customForm).writeToNBT();
            NBTJsonUtil.SaveFile(file, nbtTagCompound);
            if (file2.exists())
                file2.delete();
            file.renameTo(file2);
            Server.sendToAll(EnumPacketClient.SYNC_UPDATE, SyncType.CUSTOM_FORM, nbtTagCompound, customForm.getID());
        } catch (Exception e) {
            LogWriter.except(e);
        }
        return customForms.get(customForm.getID());
    }

    public boolean hasName(String newName) {
        if (newName.trim().isEmpty())
            return true;
        for (CustomForm form : customForms.values())
            if (form.name.equals(newName))
                return true;
        return false;
    }

    public void delete(String name) {
        CustomForm delete = getFormFromName(name);
        if (delete != null) {
            CustomForm foundForm = this.customForms.remove(delete.getID());
            if (foundForm != null && foundForm.name != null) {
                File dir = this.getDir();
                for (File file : dir.listFiles()) {
                    if (!file.isFile() || !file.getName().endsWith(".json"))
                        continue;
                    if (file.getName().equals(foundForm.name + ".json")) {
                        file.delete();
                        Server.sendToAll(EnumPacketClient.SYNC_REMOVE, SyncType.CUSTOM_FORM, foundForm);
                        break;
                    }
                }
                saveFormLoadMap();
            }
        }
    }

    public void delete(int id) {
        if (!this.customForms.containsKey(id))
            return;

        CustomForm foundForm = this.customForms.remove(id);
        if (foundForm != null && foundForm.name != null) {
            File dir = this.getDir();
            for (File file : dir.listFiles()) {
                if (!file.isFile() || !file.getName().endsWith(".json"))
                    continue;
                if (file.getName().equals(foundForm.name + ".json")) {
                    file.delete();
                    Server.sendToAll(EnumPacketClient.SYNC_REMOVE, SyncType.CUSTOM_FORM, foundForm);
                    break;
                }
            }
            saveFormLoadMap();
        }
    }

    public boolean has(String name) {
        return getFormFromName(name) != null;
    }

    public boolean has(int id) {
        return get(id) != null;
    }

    public ICustomForm get(String name) {
        return getFormFromName(name);
    }

    public ICustomForm get(int id) {
        return this.customForms.get(id);
    }

    public ICustomForm[] getForms() {
        ArrayList<ICustomForm> customForms = new ArrayList<>(this.customForms.values());
        return customForms.toArray(new ICustomForm[0]);
    }

    public CustomForm getFormFromName(String formName) {
        for (Map.Entry<Integer, CustomForm> entryForm : FormController.getInstance().customForms.entrySet()) {
            if (entryForm.getValue().name.equalsIgnoreCase(formName)) {
                return entryForm.getValue();
            }
        }
        return null;
    }

    public String[] getNames() {
        String[] names = new String[customForms.size()];
        int i = 0;
        for (CustomForm customForm : customForms.values()) {
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
    ////////////////////////////////////////////////////////
    // CUSTOM FORM MAP
    // Used to keep load order of Forms

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
            CustomForm customForm = customForms.get(key);
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

    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////
}
