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
 * Values are accessed via the typed {@link RaceProperty} itself
 */
public class RacePropertyData implements DataSerializable {

    /** Schema: maps each property key to its definition. Populated by {@link #initDefaults(Race)}. */
    private final Map<String, RaceProperty<?>> schema = new LinkedHashMap<>();

    /** Live values, keyed by {@link RaceProperty#key}. Type matches the property subclass. */
    private final Map<String, Object> values = new LinkedHashMap<>();

    // ── Initialization ────────────────────────────────────────────────────────

    /**
     * Rebuilds the schema from the given race and fills in missing values with defaults.
     * Existing keys are preserved so player edits survive minor race reloads.
     * Always call this before {@link #deserialize(DataCompound)}.
     */
    public void initDefaults(Race race) {
        if (race == null || race.properties == null) return;

        schema.clear();
        values.clear();
        for (RaceProperty<?> property : race.properties.getAll()) {
            schema.put(property.key, property);
            if (!values.containsKey(property.key)) {
                values.put(property.key, property.getDefault());
            }
        }
    }

    // ── Typed access ──────────────────────────────────────────────────────────

    /**
     * Returns the current value for the given property,
     * or its default if no value has been set.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(RaceProperty<T> property) {
        Object val = values.get(property.key);
        if (val == null) return property.getDefault();
        try {
            return (T) val;
        } catch (ClassCastException e) {
            return property.getDefault();
        }
    }

    public Object getRaw(String key, Object defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    /**
     * Sets the value for the given property.
     * Ignores the call if the value fails {@link RaceProperty#isValid(Object)}.
     */
    public <T> void set(RaceProperty<T> property, T value) {
        if (property.isValid(value)) {
            values.put(property.key, value);
        }
    }

    // ── Generic ───────────────────────────────────────────────────────────────

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public void clear() {
        schema.clear();
        values.clear();
    }

    // ── Serialization ─────────────────────────────────────────────────────────

    @Override
    @SuppressWarnings("unchecked")
    public DataCompound serialize(DataCompound data) {
        DataCompound child = DataCompound.create();

        for (Map.Entry<String, RaceProperty<?>> entry : schema.entrySet()) {
            RaceProperty<Object> property = (RaceProperty<Object>) entry.getValue();
            Object value = values.getOrDefault(property.key, property.getDefault());
            property.write(child, value);
        }

        data.put("raceProperties", child);
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deserialize(DataCompound data) {
        if (!data.has("raceProperties")) return;

        DataCompound child = data.get("raceProperties");

        for (Map.Entry<String, RaceProperty<?>> entry : schema.entrySet()) {
            RaceProperty<Object> property = (RaceProperty<Object>) entry.getValue();
            values.put(property.key, property.read(child));
        }
    }
}
