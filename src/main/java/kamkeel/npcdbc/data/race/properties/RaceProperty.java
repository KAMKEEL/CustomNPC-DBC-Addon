package kamkeel.npcdbc.data.race.properties;

import kamkeel.npcdbc.data.race.serial.DataCompound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Defines a single customizable property for a race.
 * This is a template — it holds the definition, not the player's current value.
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

    /**
     * Writes this property's value into the given DataCompound under {@link #key}.
     */
    public abstract void write(DataCompound data, T value);

    /**
     * Reads this property's value from the given DataCompound,
     * returning {@link #getDefault()} if the key is absent or invalid.
     */
    public abstract T read(DataCompound data);

    protected abstract String stringify(T value);

    @SuppressWarnings("unchecked")
    public String toString(Object value) {
        return stringify(value != null ? (T) value : getDefault());
    }

    public Button getButtonType() {
        return Button.ARROW;
    }

    // -------------------------------------------------------------------------

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

        @Override public Integer getDefault() { return defaultValue; }

        @Override
        public boolean isValid(Integer value) {
            return value != null && value >= min && value <= max;
        }

        @Override
        public void write(DataCompound data, Integer value) {
            data.putInt(key, value != null ? value : defaultValue);
        }

        @Override
        public Integer read(DataCompound data) {
            int val = data.getInt(key, defaultValue);
            return (val >= min && val <= max) ? val : defaultValue;
        }

        public String stringify(Integer value) {
            return value + "";
        }
    }

    // -------------------------------------------------------------------------

    public static class Bool extends RaceProperty<Boolean> {

        public final boolean defaultValue;

        public Bool(String key, String displayName, boolean defaultValue) {
            super(key, displayName);
            this.defaultValue = defaultValue;
        }

        @Override public Boolean getDefault() { return defaultValue; }

        @Override
        public boolean isValid(Boolean value) { return value != null; }

        @Override
        public void write(DataCompound data, Boolean value) {
            data.putBoolean(key, value != null ? value : defaultValue);
        }

        @Override
        public Boolean read(DataCompound data) {
            return data.getBoolean(key, defaultValue);
        }

        @Override
        public String stringify(Boolean value) {
            return value ? "Enabled" : "Disabled";
        }

        @Override
        public Button getButtonType() {
            return Button.TOGGLE;
        }
    }

    // -------------------------------------------------------------------------

    public static class Str extends RaceProperty<String> {

        public final String defaultValue;
        public final List<String> allowedValues;

        public Str(String key, String displayName, String defaultValue, String... allowedValues) {
            super(key, displayName);
            if (allowedValues == null || allowedValues.length == 0)
                throw new IllegalArgumentException("allowedValues cannot be empty for property: " + key);
            List<String> mutable = new ArrayList<>(Arrays.asList(allowedValues));
            if (!mutable.contains(defaultValue)) mutable.add(0, defaultValue);
            this.allowedValues = Collections.unmodifiableList(mutable);
            this.defaultValue = defaultValue;
        }

        @Override public String getDefault() { return defaultValue; }

        @Override
        public boolean isValid(String value) {
            return value != null && allowedValues.contains(value);
        }

        @Override
        public void write(DataCompound data, String value) {
            data.putString(key, value != null ? value : defaultValue);
        }

        @Override
        public String read(DataCompound data) {
            String val = data.getString(key, defaultValue);
            return allowedValues.contains(val) ? val : defaultValue;
        }

        @Override
        public String stringify(String value) {
            return value;
        }
    }

    public enum Button {
        ARROW,
        TOGGLE,
        COLOR,
        SLIDER
    }
}
