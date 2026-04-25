package kamkeel.npcdbc.data.race.properties;

import kamkeel.npcdbc.data.race.Race;

import java.util.function.Predicate;

/**
 * Defines a single customizable numeric property for a race.
 * This is a template — it holds the definition, not the player's current value.
 *
 * Examples: wing type, wing color, eye type, hair color, etc.
 */
public class RaceProperty {

    public final String key;

    public final String displayName;

    public final int defaultValue;

    public final Predicate<RacePropertyData> condition;

    public RaceProperty(String key, String displayName, int defaultValue, Predicate<RacePropertyData> condition) {
        this.key = key;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.condition = condition;
    }

    public RaceProperty(String key, String displayName, int defaultValue) {
        this(key, displayName, defaultValue, null);
    }

    public boolean isAvailable(RacePropertyData data) {
        return condition == null || condition.test(data);
    }
}
