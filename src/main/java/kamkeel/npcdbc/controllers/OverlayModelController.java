package kamkeel.npcdbc.controllers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.IOverlayModel;
import kamkeel.npcdbc.controllers.sync.DBCSyncType;
import kamkeel.npcdbc.data.overlay.OverlayModelScript;
import kamkeel.npcdbc.data.overlay.ScriptOverlayModel;
import kamkeel.npcs.controllers.SyncController;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.util.NBTJsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Single source of truth for overlay-model lookup.
 * <p>
 * Manages two separate maps:
 * <ul>
 *   <li>{@link #customModels} — synced script-defined models (server-persisted, keyed by immutable string key)</li>
 *   <li>{@link #builtInModels} — client-only hardcoded IOverlayModel implementations (e.g. BioAndroid parts)</li>
 * </ul>
 * <p>
 * Custom models are persisted under a dedicated save folder keyed by a
 * filesystem-safe encoding of the stable key.
 */
public class OverlayModelController {
    public static OverlayModelController Instance = new OverlayModelController();

    /** Server-side + synced custom script-defined overlay models. Keyed by immutable canonical key. */
    public final Map<String, ScriptOverlayModel> customModels = new HashMap<>();

    /**
     * Client-only built-in overlay model implementations.
     * Not persisted, not synced. Keyed by stable built-in keys like {@code npcdbc:bio_crest}.
     */
    @SideOnly(Side.CLIENT)
    public static Map<String, IOverlayModel> builtInModels;

    public OverlayModelController() {
        Instance = this;
    }

    // ═══════════════════════════════════════════════════════
    //  Server load / save lifecycle
    // ═══════════════════════════════════════════════════════

    /**
     * Loads all custom overlay models from the save directory.
     * Called server-side during {@code FMLServerAboutToStartEvent}.
     */
    public void load() {
        customModels.clear();
        LogWriter.info("Loading custom overlay models...");
        loadModelsFromDir();
        LogWriter.info("Done loading custom overlay models. Loaded " + customModels.size() + " model(s).");
    }

    private void loadModelsFromDir() {
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
                ScriptOverlayModel model = new ScriptOverlayModel();
                model.readFromNBT(NBTJsonUtil.LoadFile(file));

                if (!model.hasKey()) {
                    LogWriter.error("Overlay model file '" + file.getName() + "' has no key, skipping.");
                    continue;
                }

                if (customModels.containsKey(model.key)) {
                    LogWriter.error(
                            "Duplicate overlay model key '" + model.key + "' in file '" + file.getName() + "', skipping.");
                    continue;
                }

                customModels.put(model.key, model);
            } catch (Exception e) {
                LogWriter.error("Error loading overlay model: " + file.getAbsolutePath(), e);
            }
        }
    }

    // ═══════════════════════════════════════════════════════
    //  CRUD operations (server-side)
    // ═══════════════════════════════════════════════════════

    /**
     * Saves (creates or updates) a custom overlay model.
     * <p>
     * Enforces name uniqueness among custom models. If a model with the
     * same name exists under a different key, the name is suffixed.
     *
     * @return the saved model
     */
    public ScriptOverlayModel save(ScriptOverlayModel model) {
        if (model == null) return null;

        // Enforce unique display name among custom models
        ScriptOverlayModel existingByKey = customModels.get(model.key);
        if (existingByKey != null && existingByKey.name.equals(model.name)) {
            // Same key, same name — no conflict
        } else {
            while (hasName(model.name, model.key)) {
                model.name = model.name + "_";
            }
        }

        model.key = ScriptOverlayModel.deriveKey(model.name);

        customModels.put(model.key, model);

        // Persist to file
        File dir = getDir();
        if (!dir.exists()) dir.mkdirs();

        String safeFileName = toFileSystemSafe(model.name);
        File file = new File(dir, safeFileName + ".json_new");
        File file2 = new File(dir, safeFileName + ".json");

        try {
            NBTTagCompound nbt = model.writeToNBT(new NBTTagCompound());
            NBTJsonUtil.SaveFile(file, nbt);
            if (file2.exists()) file2.delete();
            file.renameTo(file2);
            SyncController.syncUpdate(DBCSyncType.OVERLAY_MODEL, nbt);
        } catch (Exception e) {
            LogWriter.except(e);
        }


        return model;
    }

    /**
     * Clones a custom model by key.
     * <p>
     * Creates a unique clone display name by appending suffixes, then derives
     * a fresh key from that clone name.
     *
     * @return the cloned model, or null if the original was not found
     */
    public ScriptOverlayModel clone(String originalKey) {
        ScriptOverlayModel original = customModels.get(originalKey);
        if (original == null) return null;

        String name = original.name;
        while (hasName(name)) name += "_";

        ScriptOverlayModel clone = new ScriptOverlayModel("");
        clone.readFromNBT(original.writeToNBT(new NBTTagCompound()));
        clone.setName(name);

        return save(clone);
    }

    /**
     * Deletes a custom model by key.
     * Removes from memory and deletes the persisted file.
     */
    public void delete(String key) {
        ScriptOverlayModel removed = customModels.remove(key);
        if (removed == null) return;

        File dir = getDir();
        String safeFileName = toFileSystemSafe(removed.name);
        File file = new File(dir, safeFileName + ".json");
        if (file.exists()) {
            file.delete();
        }
        SyncController.syncRemove(DBCSyncType.OVERLAY_MODEL, key);
    }

    // ═══════════════════════════════════════════════════════
    //  Lookup APIs
    // ═══════════════════════════════════════════════════════

    /** Check if a key exists in custom models. */
    public boolean hasKey(String key) {
        return key != null && customModels.containsKey(key);
    }

    /**
     * Check if a display name is used by any custom model.
     * Optionally excludes a specific key from the check.
     */
    public boolean hasName(String name, String excludeKey) {
        if (name == null || name.trim().isEmpty()) return true;
        for (ScriptOverlayModel m : customModels.values()) {
            if (m.name.equals(name) && !m.key.equals(excludeKey))
                return true;
        }
        return false;
    }

    /** Check if a display name is used by any custom model. */
    public boolean hasName(String name) {
        return hasName(name, null);
    }

    /** Get a custom model by key. */
    public ScriptOverlayModel getCustom(String key) {
        return key == null ? null : customModels.get(key);
    }

    /**
     * Get a built-in overlay model by key. Client-only.
     */
    @SideOnly(Side.CLIENT)
    public IOverlayModel getBuiltIn(String key) {
        return key == null || builtInModels == null ? null : builtInModels.get(key);
    }

    /**
     * Resolves a key to an {@link IOverlayModel}, checking built-in models.
     * <p>
     * For custom scripted models, callers should use {@link #getCustom(String)} and
     * resolve the compiled script instance at the call site (e.g. in ModelDBC).
     *
     * @return the built-in IOverlayModel, or null if no built-in exists for this key
     */
    @SideOnly(Side.CLIENT)
    public IOverlayModel get(String key) {
        if (key == null) return null;
        return getBuiltIn(key);
    }

    /**
     * Resolves a key to an IOverlayModel, checking custom script models first
     * (via the compiled Functions instance), then falling back to built-ins.
     * <p>
     * Client-only. The custom model's script must be compiled for this to
     * return the scripted model; otherwise falls back to built-in.
     */
    @SideOnly(Side.CLIENT)
    public IOverlayModel resolve(String key) {
        if (key == null) return null;

        ScriptOverlayModel custom = customModels.get(key);
        if (custom != null && custom.getScriptHandler().hasScript()) {
            OverlayModelScript script = custom.getScriptHandler().getScript();
            if (script != null && custom.getScriptHandler().getEnabled()) {
                IOverlayModel result = script.call(functions -> (IOverlayModel) functions);
                if (result != null) return result;
            }
        }

        return getBuiltIn(key);
    }

    // ═══════════════════════════════════════════════════════
    //  Client reload helpers
    // ═══════════════════════════════════════════════════════

    /**
     * Client-side: atomically replace all custom model data after a full RELOAD sync.
     * Does NOT touch built-in models.
     */
    public void setCustomModelData(Map<String, ScriptOverlayModel> newModels) {
        customModels.clear();
        customModels.putAll(newModels);
    }

    public void put(ScriptOverlayModel model) {
        if (model == null || !model.hasKey()) return;
        customModels.put(model.key, model);
    }

    public void remove(String key) {
        if (key != null) customModels.remove(key);
    }

    // ═══════════════════════════════════════════════════════
    //  Built-in registration (client-only)
    // ═══════════════════════════════════════════════════════

    /**
     * Registers a built-in (hardcoded) overlay model implementation.
     * <p>
     * Client-only. Called during client init to register implementations like
     * BioAndroid crest/wings/tail models. Built-ins are never persisted or synced.
     *
     * @param key   the stable built-in key (e.g. {@code npcdbc:bio_crest})
     * @param model the overlay model implementation
     */
    @SideOnly(Side.CLIENT)
    public void register(String key, IOverlayModel model) {
        if (key == null || model == null) return;
        if (builtInModels == null) builtInModels = new HashMap<>();
        builtInModels.put(key, model);
    }
    

    // ═══════════════════════════════════════════════════════
    //  Utility
    // ═══════════════════════════════════════════════════════

    /**
     * Gets all custom model keys and names as a sorted list of pairs.
     * Sorted alphabetically by display name, then key as tie-breaker.
     */
    public List<ScriptOverlayModel> getSortedCustomModels() {
        List<ScriptOverlayModel> sorted = new ArrayList<>(customModels.values());
        sorted.sort((a, b) -> {
            int cmp = a.name.compareToIgnoreCase(b.name);
            return cmp != 0 ? cmp : a.key.compareTo(b.key);
        });
        return sorted;
    }

    public ScriptOverlayModel getCustomByName(String name) {
        if (name == null) return null;
        for (ScriptOverlayModel model : customModels.values()) {
            if (name.equals(model.name)) return model;
        }
        return null;
    }

    private File getDir() {
        return new File(CustomNpcs.getWorldSaveDirectory(), "customoverlaymodels");
    }

    /**
     * Converts a key string to a filesystem-safe filename.
     * Replaces colons and slashes with underscores.
     */
    static String toFileSystemSafe(String key) {
        if (key == null) return "unnamed";
        return key.replace(':', '_').replace('/', '_');
    }

    public static OverlayModelController getInstance() {
        return Instance;
    }
}
