package kamkeel.npcdbc.data.race.stats;

import kamkeel.npcdbc.constants.enums.EnumDBCAttributes;
import kamkeel.npcdbc.constants.enums.EnumDBCStats;

import java.util.EnumMap;
import java.util.Map;

public class ClassStats {
    public static final Map<EnumDBCAttributes, Integer> DEFAULT_INITIAL_ATTRIBUTES = new EnumMap<>(EnumDBCAttributes.class);
    public static final Map<EnumDBCAttributes, Double> DEFAULT_ATTRIBUTE_MULTIPLIERS = new EnumMap<>(EnumDBCAttributes.class);
    public static final Map<EnumDBCStats, Double> DEFAULT_STAT_BONUSES = new EnumMap<>(EnumDBCStats.class);
    public static final Map<EnumDBCStats, Double> DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS = new EnumMap<>(EnumDBCStats.class);

    static {
        for (EnumDBCAttributes attr : EnumDBCAttributes.values()) {
            DEFAULT_INITIAL_ATTRIBUTES.put(attr, 10);
            DEFAULT_ATTRIBUTE_MULTIPLIERS.put(attr, 1.0);
        }

        DEFAULT_STAT_BONUSES.put(EnumDBCStats.MELEE, 0.0);
        DEFAULT_STAT_BONUSES.put(EnumDBCStats.DEFENSE, 0.0);
        DEFAULT_STAT_BONUSES.put(EnumDBCStats.BODY, 0.0);
        DEFAULT_STAT_BONUSES.put(EnumDBCStats.STAMINA, 30.0);
        DEFAULT_STAT_BONUSES.put(EnumDBCStats.ENERGY_POWER, 10.0);
        DEFAULT_STAT_BONUSES.put(EnumDBCStats.ENERGY_POOL, 10.0);
        DEFAULT_STAT_BONUSES.put(EnumDBCStats.MAX_SKILLS, 0.0);
        DEFAULT_STAT_BONUSES.put(EnumDBCStats.SPEED, 10.0);
        DEFAULT_STAT_BONUSES.put(EnumDBCStats.REGEN_RATE_BODY, 0.0);
        DEFAULT_STAT_BONUSES.put(EnumDBCStats.REGEN_RATE_STAMINA, 0.0);
        DEFAULT_STAT_BONUSES.put(EnumDBCStats.REGEN_RATE_ENERGY, 0.0);
        DEFAULT_STAT_BONUSES.put(EnumDBCStats.FLY_SPEED, 0.0);

        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.MELEE, 2.5);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.DEFENSE, 4.0);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.BODY, 20.0);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.STAMINA, 3.5);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.ENERGY_POWER, 5.2);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.ENERGY_POOL, 40.0);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.MAX_SKILLS, 0.15);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.SPEED, 1.0);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.REGEN_RATE_BODY, 1.0);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.REGEN_RATE_STAMINA, 1.0);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.REGEN_RATE_ENERGY, 1.0);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.put(EnumDBCStats.FLY_SPEED, 1.0);
    }

    public final Map<EnumDBCAttributes, Integer> initialAttributes;
    public final Map<EnumDBCAttributes, Double> attributeMultipliers;
    public final Map<EnumDBCStats, Double> statBonuses;
    public final Map<EnumDBCStats, Double> statAttributeMultipliers;

    public ClassStats(
        Map<EnumDBCAttributes, Integer> initialAttributes,
        Map<EnumDBCAttributes, Double> attributeMultipliers,
        Map<EnumDBCStats, Double> statBonuses,
        Map<EnumDBCStats, Double> statAttributeMultipliers
    ) {
        this.initialAttributes = initialAttributes;
        this.attributeMultipliers = attributeMultipliers;
        this.statBonuses = statBonuses;
        this.statAttributeMultipliers = statAttributeMultipliers;
    }

    public static ClassStats defaults() {
        return new ClassStats(
            new EnumMap<>(DEFAULT_INITIAL_ATTRIBUTES),
            new EnumMap<>(DEFAULT_ATTRIBUTE_MULTIPLIERS),
            new EnumMap<>(DEFAULT_STAT_BONUSES),
            new EnumMap<>(DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS)
        );
    }
}
