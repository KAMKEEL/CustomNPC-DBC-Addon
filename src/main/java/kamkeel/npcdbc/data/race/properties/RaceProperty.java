package kamkeel.npcdbc.data.race.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Defines a single customizable property for a race.
 * This is a template — it holds the definition, not the player's current value.
 *
 * Examples: wing type, wing color, eye type, hair color, etc.
 *
 * @param <T> the type of value this property holds (Integer, Boolean, String, etc.)
 */
public abstract class RaceProperty<T> {

    public final String key;
    public final String displayName;

    public RaceProperty(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
    }

    public abstract T getDefault();

    public abstract boolean isValid(T value);

    // -------------------------------------------------------------------------

    /**
     * A numeric (int) race property with a min, max, and default value.
     *
     * Example: wing type index, hair color index, eye type, etc.
     */
    public static class Int extends RaceProperty<Integer> {

        public final int defaultValue;
        public final int min;
        public final int max;

        public Int(String key, String displayName, int defaultValue, int min, int max) {
            super(key, displayName);
            if (min > max) throw new IllegalArgumentException("min > max for property: " + key);
            if (defaultValue < min || defaultValue > max) throw new IllegalArgumentException("defaultValue out of range for property: " + key);
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public Integer getDefault() {
            return defaultValue;
        }

        @Override
        public boolean isValid(Integer value) {
            return value != null && value >= min && value <= max;
        }
    }

    // -------------------------------------------------------------------------

    /**
     * A boolean race property with a default value.
     *
     * Example: has wings, is immortal, can fly, etc.
     */
    public static class Bool extends RaceProperty<Boolean> {

        public final boolean defaultValue;

        public Bool(String key, String displayName, boolean defaultValue) {
            super(key, displayName);
            this.defaultValue = defaultValue;
        }

        @Override
        public Boolean getDefault() {
            return defaultValue;
        }

        @Override
        public boolean isValid(Boolean value) {
            return value != null;
        }
    }

    // -------------------------------------------------------------------------

    /**
     * A string race property restricted to a fixed set of allowed values.
     *
     * Example: skin tone ("light", "medium", "dark"), voice type ("low", "mid", "high"), etc.
     */
    public static class Str extends RaceProperty<String> {

        public final String defaultValue;
        public final List<String> allowedValues;

        public Str(String key, String displayName, String defaultValue, String... allowedValues) {
            super(key, displayName);
            if (allowedValues == null || allowedValues.length == 0) throw new IllegalArgumentException("allowedValues cannot be empty for property: " + key);
            List<String> mutable = new ArrayList<>(Arrays.asList(allowedValues));
            if (!mutable.contains(defaultValue)) mutable.add(0, defaultValue);
            this.allowedValues = Collections.unmodifiableList(mutable);
            this.defaultValue = defaultValue;
        }

        @Override
        public String getDefault() {
            return defaultValue;
        }

        @Override
        public boolean isValid(String value) {
            return value != null && allowedValues.contains(value);
        }
    }
}
