package kamkeel.npcdbc.data.race.serial;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DataCompound {

    private final Map<String, Object> yaml;
    private final NBTTagCompound nbt;

    private DataCompound(boolean hasYaml) {
        this.yaml = hasYaml ? new LinkedHashMap<>() : null;
        this.nbt  = new NBTTagCompound();
    }

    private DataCompound(Map<String, Object> yaml, NBTTagCompound nbt) {
        this.yaml = yaml;
        this.nbt  = nbt != null ? nbt : new NBTTagCompound();
    }

    public static DataCompound create()                    { return new DataCompound(false); }
    public static DataCompound create(boolean hasYaml)     { return new DataCompound(hasYaml); }

    public static DataCompound ofYaml(Map<String, Object> map) {
        LinkedHashMap<String, Object> copy = new LinkedHashMap<>();
        if (map != null) copy.putAll(map);
        return new DataCompound(copy, new NBTTagCompound());
    }

    public static DataCompound ofNbt(NBTTagCompound nbt) { return new DataCompound(null, nbt); }

    public Map<String, Object> toYaml() { return yaml; }
    public NBTTagCompound toNbt()       { return nbt;  }
    public boolean        hasYaml()     { return yaml != null; }

    // ── YAML string serialization ─────────────────────────────────────────────

    public String toYamlString() {
        if (yaml == null) return "";
        StringBuilder sb = new StringBuilder();
        appendYaml(sb, yaml, 0);
        return sb.toString();
    }

    
    private static void appendYaml(StringBuilder sb, Map<String, Object> map, int indent) {
        String pad = repeat(indent);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();

            // Blank-line sentinel
            if (key.startsWith("~")) {
                sb.append('\n');
                continue;
            }
            // Comment sentinel
            if (key.startsWith("#")) {
                sb.append(pad).append("# ").append(val).append('\n');
                continue;
            }

            if (val instanceof Map) {
                sb.append(pad).append(key).append(":\n");
                appendYaml(sb, (Map<String, Object>) val, indent + 2);
            } else if (val instanceof List) {
                sb.append(pad).append(key).append(":\n");
                appendYamlList(sb, (List<Object>) val, indent + 2);
            } else if (val instanceof int[]) {
                int[] arr = (int[]) val;
                sb.append(pad).append(key).append(":\n");
                for (int v : arr) {
                    sb.append(pad).append("  - ").append(v).append('\n');
                }
            } else if (val instanceof String) {
                sb.append(pad).append(key).append(": \"").append(escapeYamlString((String) val)).append("\"\n");
            } else if (val instanceof Boolean) {
                sb.append(pad).append(key).append(": ").append(val).append('\n');
            } else {
                // Integer, Float, Double, etc.
                sb.append(pad).append(key).append(": ").append(val).append('\n');
            }
        }
    }

    
    private static void appendYamlList(StringBuilder sb, List<Object> list, int indent) {
        String pad = repeat(indent);
        for (Object item : list) {
            if (item instanceof Map) {
                sb.append(pad).append("-\n");
                appendYaml(sb, (Map<String, Object>) item, indent + 2);
            } else if (item instanceof String) {
                sb.append(pad).append("- \"").append(escapeYamlString((String) item)).append("\"\n");
            } else {
                sb.append(pad).append("- ").append(item).append('\n');
            }
        }
    }

    private static String escapeYamlString(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static String repeat(int spaces) {
        if (spaces <= 0) return "";
        char[] chars = new char[spaces];
        java.util.Arrays.fill(chars, ' ');
        return new String(chars);
    }

    // ── Child / structure ─────────────────────────────────────────────────────

    public DataCompound child() {
        return new DataCompound(yaml != null);
    }

    
    public DataCompound put(String key, DataCompound child) {
        if (yaml != null) {
            yaml.put(key, child.yaml != null ? child.yaml : new LinkedHashMap<String, Object>());
        }
        nbt.setTag(key, child.nbt);
        return this;
    }

    
    public DataCompound get(String key) {
        Map<String, Object> y = null;
        if (yaml != null && yaml.containsKey(key)) {
            Object val = yaml.get(key);
            if (val instanceof Map) {
                y = new LinkedHashMap<>((Map<String, Object>) val);
            }
        }
        NBTTagCompound n = nbt.hasKey(key) ? nbt.getCompoundTag(key) : new NBTTagCompound();
        return new DataCompound(y, n);
    }

    public boolean has(String key) {
        return (yaml != null && yaml.containsKey(key)) || nbt.hasKey(key);
    }

    // ── Comment / spacing ─────────────────────────────────────────────────────

    private int sentinelCounter = 0;

    public DataCompound comment(String text) {
        if (yaml != null) {
            yaml.put("#" + (sentinelCounter++), text);
        }
        return this;
    }

    public DataCompound spacing() {
        if (yaml != null) {
            yaml.put("~" + (sentinelCounter++), "\n");
        }
        return this;
    }

    // ── Put methods ───────────────────────────────────────────────────────────

    public DataCompound putString(String key, String value) {
        if(value == null) return this;
        if (yaml != null) yaml.put(key, value);
        nbt.setString(key, value);
        return this;
    }

    public DataCompound putInt(String key, int value) {
        if (yaml != null) yaml.put(key, value);
        nbt.setInteger(key, value);
        return this;
    }

    public DataCompound putFloat(String key, float value) {
        if (yaml != null) yaml.put(key, value);
        nbt.setFloat(key, value);
        return this;
    }

    public DataCompound putDouble(String key, double value) {
        if (yaml != null) yaml.put(key, value);
        nbt.setDouble(key, value);
        return this;
    }

    public DataCompound putBoolean(String key, boolean value) {
        if (yaml != null) yaml.put(key, value);
        nbt.setBoolean(key, value);
        return this;
    }

    public DataCompound putIntArray(String key, int[] values) {
        if (yaml != null) {
            List<Integer> list = new ArrayList<>();
            for (int v : values) list.add(v);
            yaml.put(key, list);
        }
        nbt.setIntArray(key, values);
        return this;
    }

    public DataCompound putFloatList(String key, List<Float> values) {
        NBTTagList list = new NBTTagList();
        List<Object> yamlList = yaml != null ? new ArrayList<>() : null;
        for (Float f : values) {
            if (yamlList != null) yamlList.add(f);
            list.appendTag(new NBTTagString(String.valueOf(f)));
        }
        if (yaml != null) yaml.put(key, yamlList);
        nbt.setTag(key, list);
        return this;
    }

    public DataCompound putStringList(String key, List<String> values) {
        NBTTagList list = new NBTTagList();
        List<Object> yamlList = yaml != null ? new ArrayList<>() : null;
        for (String s : values) {
            if (yamlList != null) yamlList.add(s);
            list.appendTag(new NBTTagString(s));
        }
        if (yaml != null) yaml.put(key, yamlList);
        nbt.setTag(key, list);
        return this;
    }

    // ── Get methods ───────────────────────────────────────────────────────────

    public String getString(String key, String def) {
        if (yaml != null && yaml.containsKey(key)) {
            Object val = yaml.get(key);
            if (val instanceof String) return (String) val;
            if (val != null) return String.valueOf(val);
        }
        if (nbt.hasKey(key)) return nbt.getString(key);
        return def;
    }

    public int getInt(String key, int def) {
        if (yaml != null && yaml.containsKey(key)) {
            Object val = yaml.get(key);
            try {
                if (val instanceof Number) return ((Number) val).intValue();
                if (val instanceof String) return Integer.parseInt((String) val);
            } catch (NumberFormatException ignored) {}
        }
        if (nbt.hasKey(key)) return nbt.getInteger(key);
        return def;
    }

    public float getFloat(String key, float def) {
        if (yaml != null && yaml.containsKey(key)) {
            Object val = yaml.get(key);
            try {
                if (val instanceof Number) return ((Number) val).floatValue();
                if (val instanceof String) return Float.parseFloat((String) val);
            } catch (NumberFormatException ignored) {}
        }
        if (nbt.hasKey(key)) return nbt.getFloat(key);
        return def;
    }

    public double getDouble(String key, double def) {
        if (yaml != null && yaml.containsKey(key)) {
            Object val = yaml.get(key);
            try {
                if (val instanceof Number) return ((Number) val).doubleValue();
                if (val instanceof String) return Double.parseDouble((String) val);
            } catch (NumberFormatException ignored) {}
        }
        if (nbt.hasKey(key)) return nbt.getDouble(key);
        return def;
    }

    public boolean getBoolean(String key, boolean def) {
        if (yaml != null && yaml.containsKey(key)) {
            Object val = yaml.get(key);
            if (val instanceof Boolean) return (Boolean) val;
            if (val instanceof String) return Boolean.parseBoolean((String) val);
        }
        if (nbt.hasKey(key)) return nbt.getBoolean(key);
        return def;
    }

    
    public int[] getIntArray(String key, int[] def) {
        if (yaml != null && yaml.containsKey(key)) {
            Object val = yaml.get(key);
            if (val instanceof int[]) return (int[]) val;
            if (val instanceof List) {
                try {
                    List<Object> list = (List<Object>) val;
                    int[] result = new int[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        Object item = list.get(i);
                        if (item instanceof Number) result[i] = ((Number) item).intValue();
                        else result[i] = Integer.parseInt(String.valueOf(item));
                    }
                    return result;
                } catch (Exception ignored) {}
            }
        }
        if (nbt.hasKey(key)) return nbt.getIntArray(key);
        return def;
    }

    
    public List<Float> getFloatList(String key) {
        if (yaml != null && yaml.containsKey(key)) {
            Object val = yaml.get(key);
            if (val instanceof List) {
                List<Float> result = new ArrayList<>();
                for (Object item : (List<Object>) val) {
                    try {
                        if (item instanceof Number) result.add(((Number) item).floatValue());
                        else result.add(Float.parseFloat(String.valueOf(item)));
                    } catch (NumberFormatException ignored) {}
                }
                return result;
            }
        }
        if (nbt.hasKey(key)) {
            NBTTagList list = nbt.getTagList(key, 8);
            List<Float> result = new ArrayList<>();
            for (int i = 0; i < list.tagCount(); i++) {
                try { result.add(Float.parseFloat(list.getStringTagAt(i))); } catch (NumberFormatException ignored) {}
            }
            return result;
        }
        return Collections.emptyList();
    }

    
    public List<String> getStringList(String key) {
        if (yaml != null && yaml.containsKey(key)) {
            Object val = yaml.get(key);
            if (val instanceof List) {
                List<String> result = new ArrayList<>();
                for (Object item : (List<Object>) val) {
                    result.add(item != null ? String.valueOf(item) : "");
                }
                return result;
            }
        }
        if (nbt.hasKey(key)) {
            NBTTagList list = nbt.getTagList(key, 8);
            List<String> result = new ArrayList<>();
            for (int i = 0; i < list.tagCount(); i++) result.add(list.getStringTagAt(i));
            return result;
        }
        return Collections.emptyList();
    }

    // ── Keys ───────────────────────────────────────────────────────────────

    
    public Set<String> getKeys() {
        if (yaml != null) {
            Set<String> keys = new LinkedHashSet<>();
            for (String k : yaml.keySet()) {
                if (!k.startsWith("#") && !k.startsWith("~")) {
                    keys.add(k);
                }
            }
            return keys;
        }
        return (Set<String>) nbt.func_150296_c();
    }

    // ── Map helpers ────────────────────────────────────────────────────────

    public DataCompound putStringMap(String key, Map<String, String> map) {
        DataCompound child = child();
        for (Map.Entry<String, String> e : map.entrySet()) {
            child.putString(e.getKey(), e.getValue());
        }
        put(key, child);
        return this;
    }

    public Map<String, String> getStringMap(String key) {
        if (!has(key)) return new LinkedHashMap<>();
        DataCompound child = get(key);
        Map<String, String> result = new LinkedHashMap<>();
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
        put(key, child);
        return this;
    }

    public Map<String, Integer> getIntMap(String key) {
        if (!has(key)) return new LinkedHashMap<>();
        DataCompound child = get(key);
        Map<String, Integer> result = new LinkedHashMap<>();
        for (String k : child.getKeys()) {
            result.put(k, child.getInt(k, 0));
        }
        return result;
    }

    public DataCompound putDoubleMap(String key, Map<String, Double> map) {
        DataCompound child = child();
        for (Map.Entry<String, Double> e : map.entrySet()) {
            if (yaml != null && child.yaml != null) child.yaml.put(e.getKey(), e.getValue());
            child.nbt.setTag(e.getKey(), new NBTTagString(String.valueOf(e.getValue())));
        }
        put(key, child);
        return this;
    }
    
    public Map<String, Double> getDoubleMap(String key) {
        if (!has(key)) return new LinkedHashMap<>();
        DataCompound child = get(key);
        Map<String, Double> result = new LinkedHashMap<>();
        for (String k : child.getKeys()) {
            if (child.yaml != null && child.yaml.containsKey(k)) {
                Object val = child.yaml.get(k);
                try {
                    if (val instanceof Number) result.put(k, ((Number) val).doubleValue());
                    else result.put(k, Double.parseDouble(String.valueOf(val)));
                } catch (NumberFormatException ignored) {}
            } else {
                try { result.put(k, Double.parseDouble(child.nbt.getString(k))); } catch (NumberFormatException ignored) {}
            }
        }
        return result;
    }

    public DataCompound putFloatMap(String key, Map<String, Float> map) {
        DataCompound child = child();
        for (Map.Entry<String, Float> e : map.entrySet()) {
            if (yaml != null && child.yaml != null) child.yaml.put(e.getKey(), e.getValue());
            child.nbt.setTag(e.getKey(), new NBTTagString(String.valueOf(e.getValue())));
        }
        put(key, child);
        return this;
    }

    public Map<String, Float> getFloatMap(String key) {
        if (!has(key)) return new LinkedHashMap<>();
        DataCompound child = get(key);
        Map<String, Float> result = new LinkedHashMap<>();
        for (String k : child.getKeys()) {
            if (child.yaml != null && child.yaml.containsKey(k)) {
                Object val = child.yaml.get(k);
                try {
                    if (val instanceof Number) result.put(k, ((Number) val).floatValue());
                    else result.put(k, Float.parseFloat(String.valueOf(val)));
                } catch (NumberFormatException ignored) {}
            } else {
                try { result.put(k, Float.parseFloat(child.nbt.getString(k))); } catch (NumberFormatException ignored) {}
            }
        }
        return result;
    }

    // ── Enum-keyed map helpers ─────────────────────────────────────────────

    public <K extends Enum<K>> DataCompound putEnumIntMap(String key, Map<K, Integer> map) {
        Map<String, Integer> strMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<K, Integer> e : map.entrySet()) strMap.put(e.getKey().name(), e.getValue());
        return putIntMap(key, strMap);
    }

    public <K extends Enum<K>> DataCompound putEnumDoubleMap(String key, Map<K, Double> map) {
        Map<String, Double> strMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<K, Double> e : map.entrySet()) strMap.put(e.getKey().name(), e.getValue());
        return putDoubleMap(key, strMap);
    }

    public <K extends Enum<K>> DataCompound putEnumFloatMap(String key, Map<K, Float> map) {
        Map<String, Float> strMap = new LinkedHashMap<String, Float>();
        for (Map.Entry<K, Float> e : map.entrySet()) strMap.put(e.getKey().name(), e.getValue());
        return putFloatMap(key, strMap);
    }

    public <K extends Enum<K>> void getEnumIntMap(String key, Map<K, Integer> target, Class<K> enumClass) {
        Map<String, Integer> raw = getIntMap(key);
        for (Map.Entry<String, Integer> e : raw.entrySet()) {
            try { target.put(Enum.valueOf(enumClass, e.getKey()), e.getValue()); } catch (IllegalArgumentException ignored) {}
        }
    }

    public <K extends Enum<K>> void getEnumDoubleMap(String key, Map<K, Double> target, Class<K> enumClass) {
        Map<String, Double> raw = getDoubleMap(key);
        for (Map.Entry<String, Double> e : raw.entrySet()) {
            try { target.put(Enum.valueOf(enumClass, e.getKey()), e.getValue()); } catch (IllegalArgumentException ignored) {}
        }
    }

    public <K extends Enum<K>> void getEnumFloatMap(String key, Map<K, Float> target, Class<K> enumClass) {
        Map<String, Float> raw = getFloatMap(key);
        for (Map.Entry<String, Float> e : raw.entrySet()) {
            try { target.put(Enum.valueOf(enumClass, e.getKey()), e.getValue()); } catch (IllegalArgumentException ignored) {}
        }
    }

    // ── Serializable child helper ──────────────────────────────────────────

    public DataCompound put(String key, DataSerializable child) {
        if(child == null) return this;
        return put(key, child.serialize(this.child()));
    }

    public void deserialize(String key, DataSerializable target) {
        target.deserialize(get(key));
    }

    public <K extends Enum<K>> DataCompound put(K key, DataSerializable child) {
        return put(key.name(), child);
    }

    public <K extends Enum<K>> void deserialize(K key, DataSerializable target) {
        deserialize(key.name(), target);
    }
    
}
