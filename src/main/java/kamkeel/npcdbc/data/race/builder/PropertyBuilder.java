package kamkeel.npcdbc.data.race.builder;

import kamkeel.npcdbc.data.race.properties.RaceProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public abstract class PropertyBuilder<V, P extends RaceProperty<V>, S extends PropertyBuilder<V, P, S>> {
    protected final RaceBuilder.PropertiesBuilder parent;
    protected final String key;
    protected final String displayName;

    PropertyBuilder(RaceBuilder.PropertiesBuilder parent, String key, String displayName) {
        this.parent = parent;
        this.key = key;
        this.displayName = displayName;
    }

    public abstract P createProperty();

    public RaceBuilder.PropertiesBuilder and() {
        P prop = createProperty();
        if (prop != null) parent.addProperty(prop);
        return parent;
    }

    public static class Int extends PropertyBuilder<Integer, RaceProperty.Int, Int> {
        private int defaultValue = 0;
        private int min = 0;
        private int max = Integer.MAX_VALUE;

        Int(RaceBuilder.PropertiesBuilder parent, String key, String displayName) {
            super(parent, key, displayName);
        }

        public Int defaultValue(int v) {
            this.defaultValue = v;
            return this;
        }

        public Int min(int v) {
            this.min = v;
            return this;
        }

        public Int max(int v) {
            this.max = v;
            return this;
        }

        public Int clamp(int def, int min, int max) {
            this.defaultValue = def;
            this.min = min;
            this.max = max;
            return this;
        }

        @Override
        public RaceProperty.Int createProperty() {
            return new RaceProperty.Int(key, displayName, defaultValue, min, max);
        }
    }

    public static class Bool extends PropertyBuilder<Boolean, RaceProperty.Bool, Bool> {
        private boolean defaultValue = false;

        Bool(RaceBuilder.PropertiesBuilder parent, String key, String displayName) {
            super(parent, key, displayName);
        }

        public Bool defaultValue(boolean v) {
            this.defaultValue = v;
            return this;
        }

        @Override
        public RaceProperty.Bool createProperty() {
            return new RaceProperty.Bool(key, displayName, defaultValue);
        }
    }

    public static class Str extends PropertyBuilder<String, RaceProperty.Str, Str> {
        private String defaultValue = null;
        private final List<String> allowedValues = new ArrayList<>();

        Str(RaceBuilder.PropertiesBuilder parent, String key, String displayName) {
            super(parent, key, displayName);
        }

        public Str defaultValue(String v) {
            this.defaultValue = v;
            return this;
        }

        public Str values(String... vals) {
            allowedValues.addAll(Arrays.asList(vals));
            return this;
        }

        @Override
        public RaceProperty.Str createProperty() {
            if (allowedValues.isEmpty())
                throw new IllegalStateException("Str property '" + key + "' precisa de ao menos um valor.");
            String def = defaultValue != null ? defaultValue : allowedValues.get(0);
            return new RaceProperty.Str(key, displayName, def, allowedValues.toArray(new String[0]));
        }
    }

    public static class Custom<V, P extends RaceProperty<V>> extends PropertyBuilder<V, P, Custom<V, P>> {
        private V defaultValue = null;
        private final Function<V, P> factory;

        Custom(RaceBuilder.PropertiesBuilder parent, String key, String displayName, Function<V, P> factory) {
            super(parent, key, displayName);
            this.factory = factory;
        }

        public Custom<V, P> defaultValue(V value) {
            this.defaultValue = value;
            return this;
        }

        @Override
        public P createProperty() {
            return factory.apply(defaultValue);
        }
    }
}
