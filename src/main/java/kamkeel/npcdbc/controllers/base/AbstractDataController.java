package kamkeel.npcdbc.controllers.base;

import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcs.controllers.SyncController;
import kamkeel.npcs.network.enums.SyncType;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.util.NBTJsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDataController<T extends IControllerSerializable> {

    public final Map<String, T> customData = new HashMap<>();

    // ═══════════════════════════════════════════════════════
    //  TEMPLATE METHODS — Impls MUST override
    // ═══════════════════════════════════════════════════════

    protected abstract T createNew();

    protected abstract T createNew(String displayName);

    protected abstract SyncType getSyncType();

    protected abstract String getSaveDirectoryName();

    protected abstract String getLabel();

    // ═══════════════════════════════════════════════════════
    //  OPTIONAL HOOKS — Impls MAY override
    // ═══════════════════════════════════════════════════════

    protected void onPostLoad(T item) {}

    protected void onPostSave(T item) {}

    protected void onPostDelete(String key, T removed) {}

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Server load/save lifecycle
    // ═══════════════════════════════════════════════════════

    public void load() {
        customData.clear();
        LogWriter.info("Loading " + getLabel() + "s...");
        loadFromDir();
        LogWriter.info("Done loading " + getLabel() + "s. "
                + customData.size() + " loaded.");
    }

    private void loadFromDir() {
        File dir = getDir();
        if (!dir.exists()) {
            dir.mkdirs();
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".json"))
                continue;
            try {
                T item = createNew();
                item.deserialize(DataCompound.ofNbt(NBTJsonUtil.LoadFile(file)));

                if (!item.hasKey()) {
                    LogWriter.error(getLabel() + " file '"
                            + file.getName() + "' has no key, skipping.");
                    continue;
                }
                if (customData.containsKey(item.getKey())) {
                    LogWriter.error("Duplicate " + getLabel()
                            + " key '" + item.getKey() + "', skipping.");
                    continue;
                }

                onPostLoad(item);
                customData.put(item.getKey(), item);
            } catch (Exception e) {
                LogWriter.error("Error loading " + getLabel()
                        + ": " + file.getAbsolutePath(), e);
            }
        }
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — CRUD operations (server-side)
    // ═══════════════════════════════════════════════════════

    public T save(T item) {
        if (item == null) return null;

        T existingByKey = customData.get(item.getKey());
        if (existingByKey == null
                || !existingByKey.getDisplayName().equals(item.getDisplayName())) {
            while (hasName(item.getDisplayName(), item.getKey())) {
                item.setDisplayName(item.getDisplayName() + "_");
            }
        }

        item.setDisplayName(item.getDisplayName());
        customData.put(item.getKey(), item);

        File dir = getDir();
        if (!dir.exists()) dir.mkdirs();

        String safeFileName = toFileSystemSafe(item.getDisplayName());
        File file = new File(dir, safeFileName + ".json_new");
        File file2 = new File(dir, safeFileName + ".json");

        try {
            NBTTagCompound nbt = item.serialize(DataCompound.create()).toNbt();
            NBTJsonUtil.SaveFile(file, nbt);
            if (file2.exists()) file2.delete();
            file.renameTo(file2);

            onPostSave(item);
            SyncController.syncUpdate(getSyncType(), nbt);
        } catch (Exception e) {
            LogWriter.except(e);
        }

        return item;
    }

    public T clone(String originalKey) {
        T original = customData.get(originalKey);
        if (original == null) return null;

        String name = original.getDisplayName();
        while (hasName(name)) name += "_";

        T cloneObj = createNew("");
        cloneObj.deserialize(DataCompound.ofNbt(original.serialize(DataCompound.create()).toNbt()));
        cloneObj.setDisplayName(name);

        return save(cloneObj);
    }

    public void delete(String key) {
        T removed = customData.remove(key);
        if (removed == null) return;

        File dir = getDir();
        String safeFileName = toFileSystemSafe(removed.getDisplayName());
        File file = new File(dir, safeFileName + ".json");
        if (file.exists()) file.delete();

        onPostDelete(key, removed);
        SyncController.syncRemove(getSyncType(), key);
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Lookup APIs
    // ═══════════════════════════════════════════════════════

    public boolean hasKey(String key) {
        return key != null && customData.containsKey(key);
    }

    public boolean hasName(String name, String excludeKey) {
        if (name == null || name.trim().isEmpty()) return true;
        for (T item : customData.values()) {
            if (item.getDisplayName().equals(name)
                    && !item.getKey().equals(excludeKey))
                return true;
        }
        return false;
    }

    public boolean hasName(String name) {
        return hasName(name, null);
    }

    public T get(String key) {
        return key == null ? null : customData.get(key);
    }

    public T getByName(String name) {
        if (name == null) return null;
        for (T item : customData.values()) {
            if (name.equals(item.getDisplayName())) return item;
        }
        return null;
    }

    public List<T> getSorted() {
        List<T> sorted = new ArrayList<>(customData.values());
        sorted.sort((a, b) -> {
            int cmp = a.getDisplayName().compareToIgnoreCase(b.getDisplayName());
            return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
        });
        return sorted;
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Client-side helpers
    // ═══════════════════════════════════════════════════════

    public void setAllData(Map<String, T> newData) {
        customData.clear();
        customData.putAll(newData);
    }

    public void put(T item) {
        if (item == null || !item.hasKey()) return;
        customData.put(item.getKey(), item);
    }

    public void remove(String key) {
        if (key != null) customData.remove(key);
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Utility
    // ═══════════════════════════════════════════════════════

    protected File getDir() {
        return new File(CustomNpcs.getWorldSaveDirectory(), getSaveDirectoryName());
    }

    protected static String toFileSystemSafe(String name) {
        if (name == null) return "unnamed";
        return name.replace(':', '_').replace('/', '_');
    }
}
