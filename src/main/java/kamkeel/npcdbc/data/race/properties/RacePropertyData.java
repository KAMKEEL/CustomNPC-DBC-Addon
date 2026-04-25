package kamkeel.npcdbc.data.race.properties;// ─── Race Property Data ──────────────────────────────────────────────

import kamkeel.npcdbc.data.dbcdata.DBCDataRace;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores the current numeric values of all {@link RaceProperty} definitions
 * belonging to the player's race. Lives inside {@link DBCDataRace}.
 *
 * Values are keyed by {@link RaceProperty#key} and default to
 * {@link RaceProperty#defaultValue} when first initialized.
 */
public class RacePropertyData implements DataSerializable {

    private final Map<String, Integer> values = new LinkedHashMap<>();

    public void initDefaults(Race race) {
        if (race == null || race.properties == null) return;

        for (RaceProperty property : race.properties.getAll()) {
            if (!values.containsKey(property.key)) {
                values.put(property.key, property.defaultValue);
            }
        }
    }

    public int get(String key, int defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    public int get(String key) {
        return get(key, 0);
    }

    public void set(String key, int value) {
        values.put(key, value);
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public void clear() {
        values.clear();
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        DataCompound child = DataCompound.create();
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            child.putInt(entry.getKey(), entry.getValue());
        }

        return child;
    }

    @Override
    public void deserialize(DataCompound data) {
        values.clear();
        DataCompound child = data.get("raceProperties");

        for (String key : child.getKeys()) {
            values.put(key, child.getInt(key, 0));
        }
    }
}
