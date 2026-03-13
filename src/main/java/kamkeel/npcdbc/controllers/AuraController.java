package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.aura.IAuraHandler;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.data.aura.Aura;
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

public class AuraController implements IAuraHandler {
    public static AuraController Instance = new AuraController();
    public HashMap<Integer, Aura> customAurasSync = new HashMap();
    public HashMap<Integer, Aura> customAuras;
    private HashMap<Integer, String> bootOrder;
    private int lastUsedID = 0;

    public CategoryManager categoryManager = new CategoryManager();

    public AuraController() {
        Instance = this;
        customAuras = new HashMap<>();
        bootOrder = new HashMap<>();
    }

    public void load() {
        customAuras = new HashMap<>();
        bootOrder = new HashMap<>();
        lastUsedID = 0;
        LogWriter.info("Loading custom auras...");
        readCustomAuraMap();
        loadAuras();
        LogWriter.info("Done loading custom auras.");
    }

    public IAura createAura(String name) {
        if (hasName(name))
            return get(name);
        else {
            Aura aura = new Aura();
            aura.name = name;
            if (aura.id == -1) {
                aura.id = getUnusedId();
            }
            int setID = aura.id;
            while (bootOrder.containsKey(setID) || customAuras.containsKey(setID)) {
                if (bootOrder.containsKey(setID))
                    if (bootOrder.get(setID).equals(aura.name))
                        break;

                setID++;
            }
            customAuras.put(aura.id, aura);
            aura.save();
            return aura;
        }
    }

    public IAura saveAura(IAura customAura) {
        if (customAura.getID() < 0) {
            customAura.setID(getUnusedId());
            while (hasName(customAura.getName()))
                customAura.setName(customAura.getName() + "_");
        } else {
            Aura existing = customAuras.get(customAura.getID());
            if (existing != null && !existing.name.equals(customAura.getName()))
                while (hasName(customAura.getName()))
                    customAura.setName(customAura.getName() + "_");
        }

        TagController.validateTagUUIDs(((Aura) customAura).tagUUIDs);
        customAuras.remove(customAura.getID());
        customAuras.put(customAura.getID(), (Aura) customAura);

        saveAuraLoadMap();

        File dir = categoryManager.getItemDir(customAura.getID());
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, customAura.getName() + ".json_new");
        File file2 = new File(dir, customAura.getName() + ".json");

        try {
            NBTTagCompound nbtTagCompound = ((Aura) customAura).writeToNBT();
            NBTJsonUtil.SaveFile(file, nbtTagCompound);
            if (file2.exists())
                file2.delete();
            file.renameTo(file2);
            DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.AURA, EnumSyncAction.UPDATE, -1, nbtTagCompound));
        } catch (Exception e) {
            LogWriter.except(e);
        }
        return customAuras.get(customAura.getID());
    }

    public Aura cloneAura(int originalId) {
        Aura original = customAuras.get(originalId);
        if (original == null) return null;

        int originalCatId = categoryManager.getItemCategory(originalId);

        Aura clone = new Aura();
        clone.readFromNBT(original.writeToNBT());
        clone.id = getUnusedId();

        String name = clone.name;
        while (hasName(name)) name += "_";
        clone.name = name;

        if (originalCatId > CategoryManager.UNCATEGORIZED_ID) {
            categoryManager.registerItem(clone.id, originalCatId);
        }

        saveAura(clone);
        return clone;
    }

    public void deleteAuraFile(String name) {
        categoryManager.deleteFile(name + ".json");
    }

    private void loadAuras() {
        customAuras.clear();

        File dir = getDir();
        if (!dir.exists()) {
            dir.mkdir();
        }

        categoryManager.loadCategories(dir);

        // Load uncategorized auras (root level)
        loadAurasFromDir(dir, CategoryManager.UNCATEGORIZED_ID);

        // Load categorized auras (subdirectories)
        for (Map.Entry<Integer, Category> entry : categoryManager.getCategories().entrySet()) {
            File catDir = categoryManager.getCategoryDir(entry.getKey());
            loadAurasFromDir(catDir, entry.getKey());
        }

        saveAuraLoadMap();
    }

    private void loadAurasFromDir(File dir, int catId) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".json"))
                continue;
            try {
                Aura aura = new Aura();
                aura.readFromNBT(NBTJsonUtil.LoadFile(file));
                aura.name = file.getName().substring(0, file.getName().length() - 5);

                if (aura.id == -1) {
                    aura.id = getUnusedId();
                }

                int originalID = aura.id;
                int setID = aura.id;
                while (bootOrder.containsKey(setID) || customAuras.containsKey(setID)) {
                    if (bootOrder.containsKey(setID))
                        if (bootOrder.get(setID).equals(aura.name))
                            break;

                    setID++;
                }

                aura.id = setID;
                if (originalID != setID) {
                    LogWriter.info("Found Custom Aura ID Mismatch: " + aura.name + ", New ID: " + setID);
                    aura.save();
                }

                customAuras.put(aura.id, aura);
                categoryManager.registerItem(aura.id, catId);
            } catch (Exception e) {
                LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            }
        }
    }

    public boolean hasName(String newName) {
        if (newName.trim().isEmpty())
            return true;
        for (Aura aura : customAuras.values())
            if (aura.name.equals(newName))
                return true;
        return false;
    }

    public IAura get(String name) {
        return getAuraFromName(name);
    }

    public IAura get(int id) {
        return this.customAuras.get(id);
    }

    public boolean has(String name) {
        return getAuraFromName(name) != null;
    }

    public boolean has(int id) {
        return get(id) != null;
    }

    public void delete(int id) {
        if (!this.customAuras.containsKey(id))
            return;

        Aura foundAura = this.customAuras.remove(id);
        if (foundAura != null && foundAura.name != null) {
            File dir = categoryManager.getItemDir(id);
            File file = new File(dir, foundAura.name + ".json");
            if (file.exists()) {
                file.delete();
            }
            categoryManager.removeItem(id);
            DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.AURA, EnumSyncAction.REMOVE, foundAura.getID(), new NBTTagCompound()));
            saveAuraLoadMap();
        }
    }

    public void delete(String name) {
        Aura delete = getAuraFromName(name);
        if (delete != null) {
            delete(delete.id);
        }
    }

    private File getDir() {
        return new File(CustomNpcs.getWorldSaveDirectory(), "customauras");
    }

    public int getUnusedId() {
        for (int catid : customAuras.keySet()) {
            if (catid > lastUsedID)
                lastUsedID = catid;
        }
        lastUsedID++;
        return lastUsedID;
    }

    public String[] getNames() {
        String[] names = new String[customAuras.size()];
        int i = 0;
        for (Aura aura : customAuras.values()) {
            names[i] = aura.name.toLowerCase();
            i++;
        }
        return names;
    }

    public IAura[] getAuras() {
        ArrayList<IAura> auras = new ArrayList<>(this.customAuras.values());
        return auras.toArray(new IAura[0]);
    }

    public Aura getAuraFromName(String auraName) {
        for (Map.Entry<Integer, Aura> entryAura : customAuras.entrySet()) {
            if (entryAura.getValue().name.equalsIgnoreCase(auraName)) {
                return entryAura.getValue();
            }
        }
        return null;
    }

    public File getMapDir() {
        File dir = CustomNpcs.getWorldSaveDirectory();
        if (!dir.exists())
            dir.mkdir();
        return dir;
    }

    ////////////////////////////////////////////////////////
    // AURA MAP
    ////////////////////////////////////////////////////////

    public void readCustomAuraMap() {
        bootOrder.clear();

        try {
            File file = new File(getMapDir(), "customauras.dat");
            if (file.exists()) {
                loadCustomAuraMap(file);
            }
        } catch (Exception e) {
            try {
                File file = new File(getMapDir(), "customauras.dat_old");
                if (file.exists()) {
                    loadCustomAuraMap(file);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void readCustomAuraMap(DataInputStream stream) throws IOException {
        NBTTagCompound nbtCompound = CompressedStreamTools.read(stream);
        this.readMapNBT(nbtCompound);
    }

    private void loadCustomAuraMap(File file) throws IOException {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
        readCustomAuraMap(var1);
        var1.close();
    }

    public void readMapNBT(NBTTagCompound compound) {
        lastUsedID = compound.getInteger("lastID");
        NBTTagList list = compound.getTagList("CustomAuras", 10);
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
                String auraName = nbttagcompound.getString("Name");
                Integer key = nbttagcompound.getInteger("ID");
                bootOrder.put(key, auraName);
            }
        }
    }

    public NBTTagCompound writeMapNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList auraList = new NBTTagList();
        for (Integer key : customAuras.keySet()) {
            Aura aura = customAuras.get(key);
            if (!aura.getName().isEmpty()) {
                NBTTagCompound auraCompound = new NBTTagCompound();
                auraCompound.setString("Name", aura.getName());
                auraCompound.setInteger("ID", key);

                auraList.appendTag(auraCompound);
            }
        }
        nbt.setTag("CustomAuras", auraList);
        nbt.setInteger("lastID", lastUsedID);
        return nbt;
    }

    public void saveAuraLoadMap() {
        try {
            File saveDir = getMapDir();
            File file = new File(saveDir, "customauras.dat_new");
            File file1 = new File(saveDir, "customauras.dat_old");
            File file2 = new File(saveDir, "customauras.dat");
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

    public static AuraController getInstance() {
        return Instance;
    }

    ////////////////////////////////////////////////////////
    // CATEGORY HELPERS
    ////////////////////////////////////////////////////////

    public Map<String, Integer> getCategoryScrollData() {
        return categoryManager.getCategoryScrollData();
    }

    public Map<String, Integer> getItemsByCategoryScrollData(int catId) {
        Map<String, Integer> map = new HashMap<>();
        List<Integer> itemIds = categoryManager.getItemsInCategory(catId, customAuras.keySet());
        for (int itemId : itemIds) {
            Aura aura = customAuras.get(itemId);
            if (aura != null) {
                map.put(aura.name, aura.id);
            }
        }
        return map;
    }

    public HashMap<String, HashSet<UUID>> getItemTagMapForCategory(int catId) {
        HashMap<String, HashSet<UUID>> tagMap = new HashMap<>();
        List<Integer> itemIds = categoryManager.getItemsInCategory(catId, customAuras.keySet());
        for (int itemId : itemIds) {
            Aura aura = customAuras.get(itemId);
            if (aura != null && !aura.tagUUIDs.isEmpty()) {
                tagMap.put(aura.name, aura.tagUUIDs);
            }
        }
        return tagMap;
    }

    public void moveItemToCategory(int itemId, int catId) {
        Aura aura = customAuras.get(itemId);
        if (aura == null) return;
        categoryManager.moveItem(itemId, aura.name + ".json", catId);
        saveAuraLoadMap();
    }

    public Category createCategory(String name) {
        return categoryManager.createCategory(name);
    }

    public void saveCategory(Category cat) {
        categoryManager.saveCategory(cat);
    }

    public boolean removeCategory(int catId) {
        return categoryManager.removeCategory(catId, customAuras.keySet());
    }

    public Category getCategory(int catId) {
        return categoryManager.getCategory(catId);
    }
}
