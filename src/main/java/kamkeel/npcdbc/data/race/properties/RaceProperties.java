package kamkeel.npcdbc.data.race.properties;

import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container for all {@link RaceProperty} definitions belonging to a race.
 * Lives inside {@link Race} as a template — player values are stored elsewhere.
 */
public class RaceProperties {

    private final List<RaceProperty<?>> properties = new ArrayList<>();

    public void add(RaceProperty<?> property) {
        properties.add(property);
    }

    public List<RaceProperty<?>> getAll() {
        return Collections.unmodifiableList(properties);
    }

    public RaceProperty<?> get(String key) {
        for (RaceProperty<?> property : properties) {
            if (property.key.equals(key)) {
                return property;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }
}
