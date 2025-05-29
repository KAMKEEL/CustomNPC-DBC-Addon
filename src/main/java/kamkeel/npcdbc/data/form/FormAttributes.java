package kamkeel.npcdbc.data.form;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FormAttributes {
    private static final String TAG_FORM_ATTRIBUTES = "formAttributes";
    private static final String TAG_FORM_MAGIC_ATTRIBUTES = "formMagicAttributes";

    private final Form parent;

    // non‑magic attrs: key → value
    private final Map<String, Float> attrs = new HashMap<>();

    // magic attrs: magicTag (e.g. "magic_damage") → (magicId → value)
    private final Map<String, Map<Integer, Float>> magic = new HashMap<>();

    public FormAttributes(Form parent) {
        this.parent = parent;
    }

    // ─── Non‑magic attribute API ────────────────────────────────────────────────

    public void setAttribute(String key, float value) {
        attrs.put(key, value);
    }

    public void removeAttribute(String key) {
        attrs.remove(key);
    }

    public Map<String, Float> getAllAttributes() {
        return Collections.unmodifiableMap(attrs);
    }

    // ─── Magic attribute API ───────────────────────────────────────────────────

    /**
     * Stores a magic attribute under the given tag.
     * Example tags: CustomAttributes.MAGIC_DAMAGE_KEY, MAGIC_BOOST_KEY, etc.
     */
    public void applyMagicAttribute(String attributeTag, int magicId, float value) {
        Map<Integer, Float> map = magic.get(attributeTag);
        if (map == null) {
            map = new HashMap<Integer, Float>();
            magic.put(attributeTag, map);
        }
        map.put(magicId, value);
    }

    public void removeMagicAttribute(String attributeTag, int magicId) {
        Map<Integer, Float> map = magic.get(attributeTag);
        if (map != null) {
            map.remove(magicId);
            if (map.isEmpty()) {
                magic.remove(attributeTag);
            }
        }
    }

    /**
     * Returns an unmodifiable map of magicId → value for the given tag,
     * or empty map if none.
     */
    public Map<Integer, Float> getMagicMap(String attributeTag) {
        Map<Integer, Float> map = magic.get(attributeTag);
        if (map == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(map);
    }

    /** All magic tags → (magicId → value) */
    public Map<String, Map<Integer, Float>> getAllMagic() {
        return Collections.unmodifiableMap(magic);
    }

    // ─── NBT I/O ────────────────────────────────────────────────────────────────

    /** Call in Form.readFromNBT(...) */
    public void readFromNBT(NBTTagCompound compound) {
        // --- attributes ---
        attrs.clear();
        if (compound.hasKey(TAG_FORM_ATTRIBUTES)) {
            NBTTagCompound at = compound.getCompoundTag(TAG_FORM_ATTRIBUTES);
            Set<String> keys = at.func_150296_c();
            for (String k : keys) {
                attrs.put(k, at.getFloat(k));
            }
        }

        // --- magic ---
        magic.clear();
        if (compound.hasKey(TAG_FORM_MAGIC_ATTRIBUTES)) {
            NBTTagCompound mg = compound.getCompoundTag(TAG_FORM_MAGIC_ATTRIBUTES);
            Set<String> tags = mg.func_150296_c();
            for (String tagName : tags) {
                NBTTagCompound mapTag = mg.getCompoundTag(tagName);
                Set<String> ids = mapTag.func_150296_c();
                Map<Integer, Float> map = new HashMap<Integer, Float>();
                for (String idStr : ids) {
                    try {
                        int id = Integer.parseInt(idStr);
                        map.put(id, mapTag.getFloat(idStr));
                    } catch (NumberFormatException e) {
                        // skip bad key
                    }
                }
                if (!map.isEmpty()) {
                    magic.put(tagName, map);
                }
            }
        }
    }

    /** Call in Form.writeToNBT(...) */
    public void writeToNBT(NBTTagCompound compound) {
        // --- attributes ---
        NBTTagCompound at = new NBTTagCompound();
        for (Map.Entry<String, Float> e : attrs.entrySet()) {
            at.setFloat(e.getKey(), e.getValue());
        }
        compound.setTag(TAG_FORM_ATTRIBUTES, at);

        // --- magic ---
        NBTTagCompound mg = new NBTTagCompound();
        for (Map.Entry<String, Map<Integer, Float>> tagEntry : magic.entrySet()) {
            String tagName = tagEntry.getKey();
            Map<Integer, Float> map = tagEntry.getValue();
            NBTTagCompound mapTag = new NBTTagCompound();
            for (Map.Entry<Integer, Float> m : map.entrySet()) {
                mapTag.setFloat(String.valueOf(m.getKey()), m.getValue());
            }
            mg.setTag(tagName, mapTag);
        }
        compound.setTag(TAG_FORM_MAGIC_ATTRIBUTES, mg);
    }
}
