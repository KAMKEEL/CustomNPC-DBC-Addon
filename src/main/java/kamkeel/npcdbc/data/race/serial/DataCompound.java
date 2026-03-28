package kamkeel.npcdbc.data.race.serial;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DataCompound {

    private final JsonObject     json;
    private final NBTTagCompound nbt;

    private DataCompound(boolean hasJson) {
        this.json = hasJson ? new JsonObject() : null;
        this.nbt  = new NBTTagCompound();
    }

    private DataCompound(JsonObject json, NBTTagCompound nbt) {
        this.json = json;
        this.nbt  = nbt != null ? nbt : new NBTTagCompound();
    }

    public static DataCompound create()              { return new DataCompound(false); }
    public static DataCompound create(boolean hasJson) { return new DataCompound(hasJson); }

    public static DataCompound ofJson(JsonObject json) { return new DataCompound(json, new NBTTagCompound()); }
    public static DataCompound ofNbt(NBTTagCompound nbt) { return new DataCompound(null, nbt); }

    public JsonObject     toJson() { return json; }
    public NBTTagCompound toNbt()  { return nbt;  }
    public boolean        hasJson() { return json != null; }

    public DataCompound child() {
        return new DataCompound(json != null);
    }

    public DataCompound putChild(String key, DataCompound child) {
        if (json != null) json.add(key, child.json != null ? child.json : new JsonObject());
        nbt.setTag(key, child.nbt);
        return this;
    }

    public DataCompound getChild(String key) {
        JsonObject     j = (json != null && json.has(key) && json.get(key).isJsonObject()) ? json.getAsJsonObject(key) : null;
        NBTTagCompound n = nbt.hasKey(key) ? nbt.getCompoundTag(key) : new NBTTagCompound();
        return new DataCompound(j, n);
    }

    public boolean has(String key) {
        return (json != null && json.has(key)) || nbt.hasKey(key);
    }

    public DataCompound comment(String text) {
        if (json != null) json.addProperty("_comment", text);
        return this;
    }

    public DataCompound putString(String key, String value) {
        if (json != null) json.addProperty(key, value);
        nbt.setString(key, value);
        return this;
    }

    public DataCompound putInt(String key, int value) {
        if (json != null) json.addProperty(key, value);
        nbt.setInteger(key, value);
        return this;
    }

    public DataCompound putFloat(String key, float value) {
        if (json != null) json.addProperty(key, value);
        nbt.setFloat(key, value);
        return this;
    }

    public DataCompound putDouble(String key, double value) {
        if (json != null) json.addProperty(key, value);
        nbt.setDouble(key, value);
        return this;
    }

    public DataCompound putBoolean(String key, boolean value) {
        if (json != null) json.addProperty(key, value);
        nbt.setBoolean(key, value);
        return this;
    }

    public DataCompound putIntArray(String key, int[] values) {
        if (json != null) {
            JsonArray arr = new JsonArray();
            for (int v : values) arr.add(new JsonPrimitive(v));
            json.add(key, arr);
        }
        nbt.setIntArray(key, values);
        return this;
    }

    public DataCompound putFloatList(String key, List<Float> values) {
        NBTTagList list = new NBTTagList();
        JsonArray arr = json != null ? new JsonArray() : null;
        for (Float f : values) {
            if (arr != null) arr.add(new JsonPrimitive(f));
            list.appendTag(new NBTTagString(String.valueOf(f)));
        }
        if (json != null) json.add(key, arr);
        nbt.setTag(key, list);
        return this;
    }

    public DataCompound putStringList(String key, List<String> values) {
        NBTTagList list = new NBTTagList();
        JsonArray arr = json != null ? new JsonArray() : null;
        for (String s : values) {
            if (arr != null) arr.add(new JsonPrimitive(s));
            list.appendTag(new NBTTagString(s));
        }
        if (json != null) json.add(key, arr);
        nbt.setTag(key, list);
        return this;
    }

    public String getString(String key, String def) {
        if (json != null && json.has(key)) return json.get(key).getAsString();
        if (nbt.hasKey(key)) return nbt.getString(key);
        return def;
    }

    public int getInt(String key, int def) {
        if (json != null && json.has(key)) return json.get(key).getAsInt();
        if (nbt.hasKey(key)) return nbt.getInteger(key);
        return def;
    }

    public float getFloat(String key, float def) {
        if (json != null && json.has(key)) return json.get(key).getAsFloat();
        if (nbt.hasKey(key)) return nbt.getFloat(key);
        return def;
    }

    public double getDouble(String key, double def) {
        if (json != null && json.has(key)) return json.get(key).getAsDouble();
        if (nbt.hasKey(key)) return nbt.getDouble(key);
        return def;
    }

    public boolean getBoolean(String key, boolean def) {
        if (json != null && json.has(key)) return json.get(key).getAsBoolean();
        if (nbt.hasKey(key)) return nbt.getBoolean(key);
        return def;
    }

    public int[] getIntArray(String key, int[] def) {
        if (json != null && json.has(key) && json.get(key).isJsonArray()) {
            JsonArray arr = json.getAsJsonArray(key);
            int[] result = new int[arr.size()];
            for (int i = 0; i < arr.size(); i++) result[i] = arr.get(i).getAsInt();
            return result;
        }
        if (nbt.hasKey(key)) return nbt.getIntArray(key);
        return def;
    }

    public List<Float> getFloatList(String key) {
        if (json != null && json.has(key) && json.get(key).isJsonArray()) {
            List<Float> result = new ArrayList<Float>();
            for (com.google.gson.JsonElement e : json.getAsJsonArray(key))
                result.add(e.getAsFloat());
            return result;
        }
        if (nbt.hasKey(key)) {
            NBTTagList list = nbt.getTagList(key, 8);
            List<Float> result = new ArrayList<Float>();
            for (int i = 0; i < list.tagCount(); i++) {
                try { result.add(Float.parseFloat(list.getStringTagAt(i))); } catch (NumberFormatException ignored) {}
            }
            return result;
        }
        return Collections.emptyList();
    }

    public List<String> getStringList(String key) {
        if (json != null && json.has(key) && json.get(key).isJsonArray()) {
            List<String> result = new ArrayList<String>();
            for (com.google.gson.JsonElement e : json.getAsJsonArray(key))
                result.add(e.getAsString());
            return result;
        }
        if (nbt.hasKey(key)) {
            NBTTagList list = nbt.getTagList(key, 8);
            List<String> result = new ArrayList<String>();
            for (int i = 0; i < list.tagCount(); i++) result.add(list.getStringTagAt(i));
            return result;
        }
        return Collections.emptyList();
    }

    // ── Keys ───────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public Set<String> getKeys() {
        if (json != null) return json.entrySet().isEmpty() ? Collections.<String>emptySet() : getJsonKeys();
        return (Set<String>) nbt.func_150296_c();
    }

    private Set<String> getJsonKeys() {
        Set<String> keys = new java.util.LinkedHashSet<String>();
        for (Map.Entry<String, JsonElement> e : json.entrySet()) keys.add(e.getKey());
        return keys;
    }

    // ── Map helpers ────────────────────────────────────────────────────────

    public DataCompound putStringMap(String key, Map<String, String> map) {
        DataCompound child = child();
        for (Map.Entry<String, String> e : map.entrySet()) {
            child.putString(e.getKey(), e.getValue());
        }
        putChild(key, child);
        return this;
    }

    public Map<String, String> getStringMap(String key) {
        if (!has(key)) return new LinkedHashMap<String, String>();
        DataCompound child = getChild(key);
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String k : child.getKeys()) {
            result.put(k, child.getString(k, ""));
        }
        return result;
    }

    public DataCompound putIntMap(String key, Map<String, Integer> map) {
        DataCompound child = child();
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            child.putInt(e.getKey(), e.getValue());
        }
        putChild(key, child);
        return this;
    }

    public Map<String, Integer> getIntMap(String key) {
        if (!has(key)) return new LinkedHashMap<String, Integer>();
        DataCompound child = getChild(key);
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        for (String k : child.getKeys()) {
            result.put(k, child.getInt(k, 0));
        }
        return result;
    }

    public DataCompound putDoubleMap(String key, Map<String, Double> map) {
        DataCompound child = child();
        for (Map.Entry<String, Double> e : map.entrySet()) {
            // Store as string in NBT for precision, native double in JSON
            if (json != null) child.json.addProperty(e.getKey(), e.getValue());
            child.nbt.setTag(e.getKey(), new NBTTagString(String.valueOf(e.getValue())));
        }
        putChild(key, child);
        return this;
    }

    public Map<String, Double> getDoubleMap(String key) {
        if (!has(key)) return new LinkedHashMap<String, Double>();
        DataCompound child = getChild(key);
        Map<String, Double> result = new LinkedHashMap<String, Double>();
        for (String k : child.getKeys()) {
            if (child.json != null && child.json.has(k)) {
                result.put(k, child.json.get(k).getAsDouble());
            } else {
                // NBT stored as string
                try { result.put(k, Double.parseDouble(child.nbt.getString(k))); } catch (NumberFormatException ignored) {}
            }
        }
        return result;
    }

    public DataCompound putFloatMap(String key, Map<String, Float> map) {
        DataCompound child = child();
        for (Map.Entry<String, Float> e : map.entrySet()) {
            if (json != null) child.json.addProperty(e.getKey(), e.getValue());
            child.nbt.setTag(e.getKey(), new NBTTagString(String.valueOf(e.getValue())));
        }
        putChild(key, child);
        return this;
    }

    public Map<String, Float> getFloatMap(String key) {
        if (!has(key)) return new LinkedHashMap<String, Float>();
        DataCompound child = getChild(key);
        Map<String, Float> result = new LinkedHashMap<String, Float>();
        for (String k : child.getKeys()) {
            if (child.json != null && child.json.has(k)) {
                result.put(k, child.json.get(k).getAsFloat());
            } else {
                try { result.put(k, Float.parseFloat(child.nbt.getString(k))); } catch (NumberFormatException ignored) {}
            }
        }
        return result;
    }
}
