package kamkeel.npcdbc.data.race.builder;

import kamkeel.npcdbc.data.race.properties.RaceProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropertyBuilder {

    public static class Int {
        private final RaceBuilder.PropertiesBuilder parent;
        private final String key;
        private final String displayName;

        private int defaultValue = 0;
        private int min = 0;
        private int max = Integer.MAX_VALUE;

        Int(RaceBuilder.PropertiesBuilder parent, String key, String displayName) {
            this.parent = parent;
            this.key = key;
            this.displayName = displayName;
        }

        public Int defaultValue(int value) {
            this.defaultValue = value;
            return this;
        }

        public Int min(int value) {
            this.min = value;
            return this;
        }

        public Int max(int value) {
            this.max = value;
            return this;
        }

        /** Shorthand for setting defaultValue, min, and max at once. */
        public Int clamp(int defaultValue, int min, int max) {
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
            return this;
        }

        public RaceBuilder.PropertiesBuilder and() {
            parent.addProperty(new RaceProperty.Int(key, displayName, defaultValue, min, max));
            return parent;
        }
    }

    // ══════════════════════════════════════════════════════════
    // BoolPropertyBuilder
    // ══════════════════════════════════════════════════════════

    public static class Bool {
        private final RaceBuilder.PropertiesBuilder parent;
        private final String key;
        private final String displayName;

        private boolean defaultValue = false;

        Bool(RaceBuilder.PropertiesBuilder parent, String key, String displayName) {
            this.parent = parent;
            this.key = key;
            this.displayName = displayName;
        }

        public Bool defaultValue(boolean value) {
            this.defaultValue = value;
            return this;
        }

        public RaceBuilder.PropertiesBuilder and() {
            parent.addProperty(new RaceProperty.Bool(key, displayName, defaultValue));
            return parent;
        }
    }

    // ══════════════════════════════════════════════════════════
    // StrPropertyBuilder
    // ══════════════════════════════════════════════════════════

    public static class Str {
        private final RaceBuilder.PropertiesBuilder parent;
        private final String key;
        private final String displayName;

        private String defaultValue = null;
        private final List<String> allowedValues = new ArrayList<>();

        Str(RaceBuilder.PropertiesBuilder parent, String key, String displayName) {
            this.parent = parent;
            this.key = key;
            this.displayName = displayName;
        }

        public Str defaultValue(String value) {
            this.defaultValue = value;
            return this;
        }

        public Str values(String... values) {
            this.allowedValues.addAll(Arrays.asList(values));
            return this;
        }

        public RaceBuilder.PropertiesBuilder and() {
            if (allowedValues.isEmpty())
                throw new IllegalStateException("strProperty '" + key + "' must have at least one allowed value.");
            String def = defaultValue != null ? defaultValue : allowedValues.get(0);
            parent.addProperty(new RaceProperty.Str(key, displayName, def, allowedValues.toArray(new String[0])));
            return parent;
        }
    }
}
