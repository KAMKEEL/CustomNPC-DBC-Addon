package kamkeel.npcdbc.data.race.properties;

import kamkeel.npcdbc.data.dbcdata.DBCDataRace;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores the current values of all {@link RaceProperty} definitions
 * belonging to the player's race. Lives inside {@link DBCDataRace}.
 *
 * Values are keyed by {@link RaceProperty#key} and default to
 * {@link RaceProperty#getDefault()} when first initialized.
 *
 * Supports Integer, Boolean and String values depending on the property type.
 */
public class RacePropertyData implements DataSerializable {
    // TODO REWRITE ALL OF THIS AND FIND A BETTER WAY TO STORE SHIT
    private final Map<String, Object> values = new LinkedHashMap<>();

    public void initDefaults(Race race) {
        if (race == null || race.properties == null) return;

        for (RaceProperty<?> property : race.properties.getAll()) {
            if (!values.containsKey(property.key)) {
                values.put(property.key, property.getDefault());
            }
        }
    }

    // ── Generic ───────────────────────────────────────────────────────────────

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public void clear() {
        values.clear();
    }

    // ── Int ───────────────────────────────────────────────────────────────────

    public int getInt(String key, int defaultValue) {
        Object val = values.get(key);
        return (val instanceof Integer) ? (Integer) val : defaultValue;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public void set(String key, int value) {
        values.put(key, value);
    }

    // ── Bool ──────────────────────────────────────────────────────────────────

    public boolean getBool(String key, boolean defaultValue) {
        Object val = values.get(key);
        return (val instanceof Boolean) ? (Boolean) val : defaultValue;
    }

    public boolean getBool(String key) {
        return getBool(key, false);
    }

    public void set(String key, boolean value) {
        values.put(key, value);
    }

    // ── String ────────────────────────────────────────────────────────────────

    public String getString(String key, String defaultValue) {
        Object val = values.get(key);
        return (val instanceof String) ? (String) val : defaultValue;
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public void set(String key, String value) {
        values.put(key, value);
    }

    // ── Serialization ─────────────────────────────────────────────────────────

    @Override
    public DataCompound serialize(DataCompound data) {
        DataCompound ints = DataCompound.create();
        DataCompound bools = DataCompound.create();
        DataCompound strings = DataCompound.create();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();

            if (val instanceof Integer) {
                ints.putInt(key, (Integer) val);
            } else if (val instanceof Boolean) {
                bools.putBoolean(key, (Boolean) val);
            } else if (val instanceof String) {
                strings.putString(key, (String) val);
            }
        }

        DataCompound child = DataCompound.create();
        child.put("integers", ints);
        child.put("booleans", bools);
        child.put("strings", strings);
        data.put("raceProperties", child);
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        values.clear();
        if (!data.has("raceProperties")) return;
        DataCompound child = data.get("raceProperties");

        DataCompound ints = child.get("integers");
        for (String key : ints.getKeys()) {
            values.put(key, ints.getInt(key, 0));
        }

        DataCompound bools = child.get("booleans");
        for (String key : bools.getKeys()) {
            values.put(key, bools.getBoolean(key, false));
        }

        DataCompound strings = child.get("strings");
        for (String key : strings.getKeys()) {
            values.put(key, strings.getString(key, ""));
        }
    }
}
