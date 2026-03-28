package kamkeel.npcdbc.data.race.stats;

import kamkeel.npcdbc.constants.enums.EnumDBCAttributes;
import kamkeel.npcdbc.constants.enums.EnumDBCStats;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

import java.util.EnumMap;
import java.util.Map;

public class ClassStats implements DataSerializable {

    // ── Defaults ──────────────────────────────────────────────────────────────

    public static final Map<EnumDBCAttributes, Integer> DEFAULT_INITIAL_ATTRIBUTES;
    public static final Map<EnumDBCAttributes, Double>  DEFAULT_ATTRIBUTE_MULTIPLIERS;
    public static final Map<EnumDBCStats, Double>       DEFAULT_STAT_BONUSES;
    public static final Map<EnumDBCStats, Double>       DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS;

    static {
        Map<EnumDBCAttributes, Integer> ia = new EnumMap<>(EnumDBCAttributes.class);
        Map<EnumDBCAttributes, Double>  am = new EnumMap<>(EnumDBCAttributes.class);
        for (EnumDBCAttributes attr : EnumDBCAttributes.values()) {
            ia.put(attr, 10);
            am.put(attr, 1.0);
        }
        DEFAULT_INITIAL_ATTRIBUTES    = ia;
        DEFAULT_ATTRIBUTE_MULTIPLIERS = am;

        Map<EnumDBCStats, Double> sb = new EnumMap<>(EnumDBCStats.class);
        sb.put(EnumDBCStats.MELEE,              0.0);
        sb.put(EnumDBCStats.DEFENSE,            0.0);
        sb.put(EnumDBCStats.BODY,               0.0);
        sb.put(EnumDBCStats.STAMINA,           30.0);
        sb.put(EnumDBCStats.ENERGY_POWER,      10.0);
        sb.put(EnumDBCStats.ENERGY_POOL,       10.0);
        sb.put(EnumDBCStats.MAX_SKILLS,         0.0);
        sb.put(EnumDBCStats.SPEED,             10.0);
        sb.put(EnumDBCStats.REGEN_RATE_BODY,    0.0);
        sb.put(EnumDBCStats.REGEN_RATE_STAMINA, 0.0);
        sb.put(EnumDBCStats.REGEN_RATE_ENERGY,  0.0);
        sb.put(EnumDBCStats.FLY_SPEED,          0.0);
        DEFAULT_STAT_BONUSES = sb;

        Map<EnumDBCStats, Double> sm = new EnumMap<>(EnumDBCStats.class);
        sm.put(EnumDBCStats.MELEE,               2.5);
        sm.put(EnumDBCStats.DEFENSE,             4.0);
        sm.put(EnumDBCStats.BODY,               20.0);
        sm.put(EnumDBCStats.STAMINA,             3.5);
        sm.put(EnumDBCStats.ENERGY_POWER,        5.2);
        sm.put(EnumDBCStats.ENERGY_POOL,        40.0);
        sm.put(EnumDBCStats.MAX_SKILLS,          0.15);
        sm.put(EnumDBCStats.SPEED,               1.0);
        sm.put(EnumDBCStats.REGEN_RATE_BODY,     1.0);
        sm.put(EnumDBCStats.REGEN_RATE_STAMINA,  1.0);
        sm.put(EnumDBCStats.REGEN_RATE_ENERGY,   1.0);
        sm.put(EnumDBCStats.FLY_SPEED,           1.0);
        DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS = sm;
    }

    // ── State ─────────────────────────────────────────────────────────────────

    private final Map<EnumDBCAttributes, Integer> initialAttributes;
    private final Map<EnumDBCAttributes, Double>  attributeMultipliers;
    private final Map<EnumDBCStats, Double>       statBonuses;
    private final Map<EnumDBCStats, Double>       statAttributeMultipliers;

    public ClassStats(
        Map<EnumDBCAttributes, Integer> initialAttributes,
        Map<EnumDBCAttributes, Double>  attributeMultipliers,
        Map<EnumDBCStats, Double>       statBonuses,
        Map<EnumDBCStats, Double>       statAttributeMultipliers
    ) {
        this.initialAttributes         = new EnumMap<>(initialAttributes);
        this.attributeMultipliers      = new EnumMap<>(attributeMultipliers);
        this.statBonuses               = new EnumMap<>(statBonuses);
        this.statAttributeMultipliers  = new EnumMap<>(statAttributeMultipliers);
    }

    public static ClassStats defaults() {
        return new ClassStats(
            DEFAULT_INITIAL_ATTRIBUTES,
            DEFAULT_ATTRIBUTE_MULTIPLIERS,
            DEFAULT_STAT_BONUSES,
            DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS
        );
    }

    // ── Starting attributes ───────────────────────────────────────────────────

    public int getInitialAttribute(EnumDBCAttributes attr) {
        Integer v = initialAttributes.get(attr);
        return v != null ? v : DEFAULT_INITIAL_ATTRIBUTES.get(attr);
    }

    public void setInitialAttribute(EnumDBCAttributes attr, int value) {
        initialAttributes.put(attr, value);
    }

    public Map<EnumDBCAttributes, Integer> getInitialAttributes() {
        return initialAttributes;
    }

    // ── Attribute multipliers ─────────────────────────────────────────────────

    public double getAttributeMultiplier(EnumDBCAttributes attr) {
        Double v = attributeMultipliers.get(attr);
        return v != null ? v : DEFAULT_ATTRIBUTE_MULTIPLIERS.get(attr);
    }

    public void setAttributeMultiplier(EnumDBCAttributes attr, double value) {
        attributeMultipliers.put(attr, value);
    }

    public Map<EnumDBCAttributes, Double> getAttributeMultipliers() {
        return attributeMultipliers;
    }

    // ── Flat stat bonuses ─────────────────────────────────────────────────────

    public double getStatBonus(EnumDBCStats stat) {
        Double v = statBonuses.get(stat);
        return v != null ? v : DEFAULT_STAT_BONUSES.get(stat);
    }

    public void setStatBonus(EnumDBCStats stat, double value) {
        statBonuses.put(stat, value);
    }

    public Map<EnumDBCStats, Double> getStatBonuses() {
        return statBonuses;
    }

    // ── Stat multipliers from attributes ──────────────────────────────────────

    public double getStatAttributeMultiplier(EnumDBCStats stat) {
        Double v = statAttributeMultipliers.get(stat);
        return v != null ? v : DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.get(stat);
    }

    public void setStatAttributeMultiplier(EnumDBCStats stat, double value) {
        statAttributeMultipliers.put(stat, value);
    }

    public Map<EnumDBCStats, Double> getStatAttributeMultipliers() {
        return statAttributeMultipliers;
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        data.comment("initialAttributes keys: STR DEX CON WILL MND SPI. attributeMultipliers/statBonuses/statAttributeMultipliers use same key pattern.");
        data.putEnumIntMap("initialAttributes", initialAttributes);
        data.putEnumDoubleMap("attributeMultipliers", attributeMultipliers);
        data.putEnumDoubleMap("statBonuses", statBonuses);
        data.putEnumDoubleMap("statAttributeMultipliers", statAttributeMultipliers);
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        data.getEnumIntMap("initialAttributes", initialAttributes, EnumDBCAttributes.class);
        data.getEnumDoubleMap("attributeMultipliers", attributeMultipliers, EnumDBCAttributes.class);
        data.getEnumDoubleMap("statBonuses", statBonuses, EnumDBCStats.class);
        data.getEnumDoubleMap("statAttributeMultipliers", statAttributeMultipliers, EnumDBCStats.class);
    }
}
