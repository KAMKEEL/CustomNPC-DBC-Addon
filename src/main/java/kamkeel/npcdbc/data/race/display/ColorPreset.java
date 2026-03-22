package kamkeel.npcdbc.data.race.display;

import kamkeel.npcdbc.api.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ColorPreset {
    private final Map<String, Color> values = new HashMap<>();

    public ColorPreset set(String slotId, Color color) {
        values.put(slotId.toLowerCase(), color);
        return this;
    }

    public Color get(String slotId) {
        return values.get(slotId.toLowerCase());
    }

    public boolean has(String slotId) {
        return values.containsKey(slotId.toLowerCase());
    }

    public Map<String, Color> getValues() {
        return Collections.unmodifiableMap(values);
    }
}
