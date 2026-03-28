package kamkeel.npcdbc.data.race.stats;

import kamkeel.npcdbc.constants.enums.EnumDBCAttributes;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

import java.util.*;

/**
 * Per-race attribute calculation config that replaces the race-indexed arrays
 * in DBC's config system for custom races.
 *
 * <p>All per-attribute data is keyed by {@link EnumDBCAttributes} using
 * {@link EnumMap} so there are no raw-array indices in the model.
 * UI-level multipliers are stored as an immutable {@link List} since UI levels
 * are runtime-config-bound (not a fixed enum domain).</p>
 *
 * <p>These mirror:
 * <ul>
 *   <li>{@code TransXStBnP[state][attr]} → {@link #base}.multi / {@link #mystic}.multi</li>
 *   <li>{@code TransXStBnF[state][attr]} → {@link #base}.flat / {@link #mystic}.flat</li>
 *   <li>{@code MysticDamMulti[race]}     → {@link #mysticDamMulti}</li>
 *   <li>{@code AttibuteBonusPerRacialSkill[race][state]} → {@link #attrBonusPerSkillLevel} /
 *       {@link #mysticAttrBonusPerSkillLevel}</li>
 *   <li>{@code CONFIG_GOD_ATTRIBUTE_MULTI_RACE[race]} → {@link #godAttrMultiRace}</li>
 *   <li>{@code CONFIG_UI_ATTRIBUTE_MULTI_RACE[uiLevel][race]} → {@link #uiAttrMultiRace}</li>
 *   <li>{@code lgndb(race, 0)} → {@link #legendaryAppliesInBase}</li>
 * </ul>
 */
public class RaceAttributeConfig implements DataSerializable {

    public enum MysticFormula {
        ATTRIBUTE_MULTI_PLUS_SKILL,
        PERCENT_MULTI_TIMES_SKILL
    }

    // ─── Grouped per-attribute bonus: multiplier + flat ────────────────────────

    /**
     * Per-attribute percentage multiplier and flat bonus for a single form-state
     * (base or mystic).
     *
     * <p>All attributes that are absent from the map default to:
     * <ul>
     *   <li>multi → 1.0f</li>
     *   <li>flat  → 0</li>
     * </ul>
     */
    public static class FormAttributeBonus {

        /** Per-attribute percentage multiplier. Missing keys default to 1.0f. */
        public Map<EnumDBCAttributes, Float> multi;

        /** Per-attribute flat bonus. Missing keys default to 0. */
        public Map<EnumDBCAttributes, Integer> flat;

        public FormAttributeBonus(
                Map<EnumDBCAttributes, Float>   multi,
                Map<EnumDBCAttributes, Integer> flat
        ) {
            EnumMap<EnumDBCAttributes, Float>   m = new EnumMap<>(EnumDBCAttributes.class);
            EnumMap<EnumDBCAttributes, Integer> f = new EnumMap<>(EnumDBCAttributes.class);
            m.putAll(multi);
            f.putAll(flat);
            this.multi = m;
            this.flat  = f;
        }

        /** Returns the percentage multiplier for {@code attr}, defaulting to 1.0f. */
        public float getMulti(EnumDBCAttributes attr) {
            Float v = multi.get(attr);
            return v != null ? v : 1.0f;
        }

        /** Returns the flat bonus for {@code attr}, defaulting to 0. */
        public int getFlat(EnumDBCAttributes attr) {
            Integer v = flat.get(attr);
            return v != null ? v : 0;
        }

        /**
         * Convenience: index-based access using the ordinal of {@link EnumDBCAttributes}.
         * Returns 1.0f for out-of-range indices.
         */
        public float getMulti(int attrIndex) {
            EnumDBCAttributes[] values = EnumDBCAttributes.values();
            if (attrIndex < 0 || attrIndex >= values.length) return 1.0f;
            return getMulti(values[attrIndex]);
        }

        /**
         * Convenience: index-based flat access.
         * Returns 0 for out-of-range indices.
         */
        public int getFlat(int attrIndex) {
            EnumDBCAttributes[] values = EnumDBCAttributes.values();
            if (attrIndex < 0 || attrIndex >= values.length) return 0;
            return getFlat(values[attrIndex]);
        }

        /** Returns a human-like {@link FormAttributeBonus} (multi 1.0, flat 0, MND multi 0.0). */
        public static FormAttributeBonus humanDefaults() {
            EnumMap<EnumDBCAttributes, Float>   m = new EnumMap<>(EnumDBCAttributes.class);
            EnumMap<EnumDBCAttributes, Integer> f = new EnumMap<>(EnumDBCAttributes.class);
            for (EnumDBCAttributes a : EnumDBCAttributes.values()) {
                m.put(a, a == EnumDBCAttributes.MND ? 0.0f : 1.0f);
                f.put(a, 0);
            }
            return new FormAttributeBonus(m, f);
        }
    }

    // ── Inner race getter: base form state ────────────────────────────────────

    /**
     * Per-attribute multiplier and flat bonus for base form state.
     * Mirrors {@code TransXStBnP[0]} / {@code TransXStBnF[0]} for the corresponding
     * vanilla race.
     */
    public FormAttributeBonus base;

    // ── Inner race getter: mystic form state ──────────────────────────────────

    /**
     * Per-attribute multiplier and flat bonus when Mystic (Potential Unleashed) is active.
     * Used when {@link #mysticDamMulti} == -1.
     * Mirrors {@code TransXStBnP[mysticState]} / {@code TransXStBnF[mysticState]}.
     */
    public FormAttributeBonus mystic;

    /**
     * Mystic damage multiplier override.
     * -1.0 = use {@link #mystic}.multi instead (DBC default when MysticDamMulti[race] == -1).
     * Any other value is used as the secondary multiplier for mystic.
     * Mirrors {@code MysticDamMulti[race]}.
     */
    public float mysticDamMulti;

    /**
     * Chooses which DBC mystic formula family is used when {@link #mysticDamMulti} == -1.
     * Human/Namekian/Majin use ATTRIBUTE_MULTI_PLUS_SKILL.
     * Saiyan/Half-Saiyan/Arcosian use PERCENT_MULTI_TIMES_SKILL.
     */
    public MysticFormula mysticFormula;

    // ── Racial skill attribute bonus ──────────────────────────────────────────

    /**
     * Attribute bonus per racial skill level for base form.
     * Applied as: {@code secondaryMulti = 1.0 + attrBonusPerSkillLevel * skillLevel}
     * Mirrors {@code AttibuteBonusPerRacialSkill[race][0]}.
     */
    public float attrBonusPerSkillLevel;

    /**
     * Attribute bonus per racial skill level when Mystic is active.
     * Mirrors {@code AttibuteBonusPerRacialSkill[race][trans[race].length]} (last entry).
     */
    public float mysticAttrBonusPerSkillLevel;

    // ── Outer getPlayerAttribute: GoD ─────────────────────────────────────────

    /**
     * Per-race God of Destruction attribute multiplier.
     * Applied as: {@code result *= CONFIG_GOD_ATTRIBUTE_MULTI * godAttrMultiRace}
     * Mirrors {@code CONFIG_GOD_ATTRIBUTE_MULTI_RACE[race]}.
     */
    public float godAttrMultiRace;

    // ── Outer getPlayerAttribute: Ultra Instinct ──────────────────────────────

    /**
     * Per-race per-UI-level attribute multiplier list.
     * Indexed by UI level (0-based); {@link #getUiAttrMultiRace} handles out-of-bounds.
     * Applied as: {@code result *= CONFIG_UI_ATTRIBUTE_MULTI[uiLevel] * 0.01 * uiAttrMultiRace[uiLevel]}
     * Mirrors {@code CONFIG_UI_ATTRIBUTE_MULTI_RACE[uiLevel][race]}.
     */
    public List<Float> uiAttrMultiRace;

    // ── Outer getPlayerAttribute: Legendary ───────────────────────────────────

    /**
     * Whether the Legendary status bonus applies when this race is in base form.
     * Mirrors {@code lgndb(race, 0)}.
     */
    public boolean legendaryAppliesInBase;

    // ── Constructor ───────────────────────────────────────────────────────────

    public RaceAttributeConfig(
            FormAttributeBonus base,
            FormAttributeBonus mystic,
            float mysticDamMulti,
            MysticFormula mysticFormula,
            float attrBonusPerSkillLevel,
            float mysticAttrBonusPerSkillLevel,
            float godAttrMultiRace,
            List<Float> uiAttrMultiRace,
            boolean legendaryAppliesInBase
    ) {
        this.base                         = base;
        this.mystic                       = mystic;
        this.mysticDamMulti               = mysticDamMulti;
        this.mysticFormula                = mysticFormula;
        this.attrBonusPerSkillLevel       = attrBonusPerSkillLevel;
        this.mysticAttrBonusPerSkillLevel = mysticAttrBonusPerSkillLevel;
        this.godAttrMultiRace             = godAttrMultiRace;
        this.uiAttrMultiRace              = new ArrayList<Float>(uiAttrMultiRace);
        this.legendaryAppliesInBase       = legendaryAppliesInBase;
    }

    // ── Default factory ───────────────────────────────────────────────────────

    /**
     * Returns a default config with Human-like values.
     * MND multiplier is 0.0 by default (does not contribute to base attribute calc).
     */
    public static RaceAttributeConfig defaults() {
        FormAttributeBonus humanBase = FormAttributeBonus.humanDefaults();
        return new RaceAttributeConfig(
                humanBase,
                humanBase,
                -1.0f,
                MysticFormula.ATTRIBUTE_MULTI_PLUS_SKILL,
                0.0f,
                0.0f,
                1.0f,
                java.util.Arrays.asList(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f),
                true
        );
    }

    // ── Safe accessors ────────────────────────────────────────────────────────

    /** Safe index-based access to base-form multiplier. Returns 1.0f for out-of-range. */
    public float getBaseFormMulti(int attribute) {
        return base.getMulti(attribute);
    }

    /** Safe index-based access to base-form flat bonus. Returns 0 for out-of-range. */
    public int getBaseFormFlatBonus(int attribute) {
        return base.getFlat(attribute);
    }

    /** Safe index-based access to mystic-form multiplier. Returns 1.0f for out-of-range. */
    public float getMysticFormMulti(int attribute) {
        return mystic.getMulti(attribute);
    }

    /** Safe index-based access to mystic-form flat bonus. Returns 0 for out-of-range. */
    public int getMysticFormFlatBonus(int attribute) {
        return mystic.getFlat(attribute);
    }

    /** Safe access to uiAttrMultiRace by UI level. Returns 1.0f for out-of-range. */
    public float getUiAttrMultiRace(int uiLevel) {
        if (uiLevel < 0 || uiLevel >= uiAttrMultiRace.size()) return 1.0f;
        Float v = uiAttrMultiRace.get(uiLevel);
        return v != null ? v : 1.0f;
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        data.comment("Attribute calculation config. baseMulti/baseFlat/mysticMulti/mysticFlat keys: STR DEX CON WILL MND SPI.");
        data.putEnumFloatMap("baseMulti", base.multi);
        data.putEnumIntMap("baseFlat", base.flat);
        data.putEnumFloatMap("mysticMulti", mystic.multi);
        data.putEnumIntMap("mysticFlat", mystic.flat);
        data.putFloat("mysticDamMulti", mysticDamMulti);
        data.putString("mysticFormula", mysticFormula.name());
        data.putFloat("attrBonusPerSkillLevel", attrBonusPerSkillLevel);
        data.putFloat("mysticAttrBonusPerSkillLevel", mysticAttrBonusPerSkillLevel);
        data.putFloat("godAttrMultiRace", godAttrMultiRace);
        data.putFloatList("uiAttrMultiRace", new ArrayList<Float>(uiAttrMultiRace));
        data.putBoolean("legendaryAppliesInBase", legendaryAppliesInBase);
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        data.getEnumFloatMap("baseMulti",   base.multi,   EnumDBCAttributes.class);
        data.getEnumIntMap("baseFlat",      base.flat,    EnumDBCAttributes.class);
        data.getEnumFloatMap("mysticMulti", mystic.multi, EnumDBCAttributes.class);
        data.getEnumIntMap("mysticFlat",    mystic.flat,  EnumDBCAttributes.class);

        mysticDamMulti               = data.getFloat("mysticDamMulti", mysticDamMulti);
        mysticAttrBonusPerSkillLevel = data.getFloat("mysticAttrBonusPerSkillLevel", mysticAttrBonusPerSkillLevel);
        attrBonusPerSkillLevel       = data.getFloat("attrBonusPerSkillLevel", attrBonusPerSkillLevel);
        godAttrMultiRace             = data.getFloat("godAttrMultiRace", godAttrMultiRace);
        legendaryAppliesInBase       = data.getBoolean("legendaryAppliesInBase", legendaryAppliesInBase);

        String formula = data.getString("mysticFormula", mysticFormula.name());
        try { mysticFormula = MysticFormula.valueOf(formula); } catch (IllegalArgumentException ignored) {}

        List<Float> ui = data.getFloatList("uiAttrMultiRace");
        if (!ui.isEmpty()) uiAttrMultiRace = ui;
    }
}
