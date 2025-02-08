package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.form.IFormHandler;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.get.DBCInfoSync;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.util.NBTJsonUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class FormController implements IFormHandler {
    public static FormController Instance = new FormController();
    public HashMap<Integer, Form> customFormsSync = new HashMap();
    public HashMap<Integer, Form> customForms;
    private HashMap<Integer, String> bootOrder;
    private int lastUsedID = 0;

    public FormController() {
        Instance = this;
        customForms = new HashMap<>();
        bootOrder = new HashMap<>();
    }

    public void load() {
        customForms = new HashMap<>();
        bootOrder = new HashMap<>();
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
                } catch (Exception e) {
                    LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
                }
            }
        }
        verifyLinkedForms();
        saveFormLoadMap();
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

        customForms.remove(customForm.getID());
        customForms.put(customForm.getID(), (Form) customForm);

        saveFormLoadMap();

        // Save CustomForm File
        File dir = this.getDir();
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, customForm.getName() + ".json_new");
        File file2 = new File(dir, customForm.getName() + ".json");

        try {
            NBTTagCompound nbtTagCompound = ((Form) customForm).writeToNBT();
            NBTJsonUtil.SaveFile(file, nbtTagCompound);
            if (file2.exists())
                file2.delete();
            file.renameTo(file2);
            PacketHandler.Instance.sendToAll(new DBCInfoSync(DBCSyncType.FORM, EnumPacketClient.SYNC_UPDATE, nbtTagCompound, -1));
        } catch (Exception e) {
            LogWriter.except(e);
        }
        return customForms.get(customForm.getID());
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
            Form foundForm = this.customForms.remove(delete.getID());
            if (foundForm != null && foundForm.name != null) {
                File dir = this.getDir();
                for (File file : dir.listFiles()) {
                    if (!file.isFile() || !file.getName().endsWith(".json"))
                        continue;
                    if (file.getName().equals(foundForm.name + ".json")) {
                        file.delete();
                        PacketHandler.Instance.sendToAll(new DBCInfoSync(DBCSyncType.FORM, EnumPacketClient.SYNC_REMOVE, new NBTTagCompound(), foundForm.getID()));
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

        Form foundForm = this.customForms.remove(id);
        if (foundForm != null && foundForm.name != null) {
            File dir = this.getDir();
            for (File file : dir.listFiles()) {
                if (!file.isFile() || !file.getName().endsWith(".json"))
                    continue;
                if (file.getName().equals(foundForm.name + ".json")) {
                    file.delete();
                    PacketHandler.Instance.sendToAll(new DBCInfoSync(DBCSyncType.FORM, EnumPacketClient.SYNC_REMOVE, new NBTTagCompound(), foundForm.getID()));
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
        File dir = this.getDir();
        if (!dir.exists())
            dir.mkdirs();
        File file2 = new File(dir, name + ".json");
        if (file2.exists())
            file2.delete();
    }

    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////
}
