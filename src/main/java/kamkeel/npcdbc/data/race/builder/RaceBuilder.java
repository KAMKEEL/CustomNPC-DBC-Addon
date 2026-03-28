package kamkeel.npcdbc.data.race.builder;

import kamkeel.npcdbc.AddonRegistries;
import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.client.race.RaceRenderContext;
import kamkeel.npcdbc.constants.enums.EnumDBCAttributes;
import kamkeel.npcdbc.constants.enums.EnumDBCClasses;
import kamkeel.npcdbc.constants.enums.EnumDBCStats;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.display.*;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.progression.RaceSkill;
import kamkeel.npcdbc.data.race.stats.ClassStats;
import kamkeel.npcdbc.data.race.stats.RaceAttributeConfig;
import kamkeel.npcdbc.data.race.stats.RaceStats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RaceBuilder {
    private final int id;
    private final String name;
    private final String menuName;

    private RaceSkill skill;
    private RaceStats stats = new RaceStats();
    private RaceDisplay display = new RaceDisplay();
    private FormTree formTree = null;
    private RaceAttributeConfig attributeConfig;

    private final String namespace;

    private RaceBuilder(int id, String name, String menuName, String namespace) {
        this.id = id;
        this.name = name;
        this.menuName = menuName;
        this.namespace = namespace;
    }

    public static RaceBuilder create(int id, String name, String menuName, String namespace) {
        return new RaceBuilder(id, name, menuName, namespace);
    }

    public FormTreeBuilder formTree() {
        return new FormTreeBuilder(this, namespace);
    }

    public RaceBuilder formTree(FormTree tree) {
        this.formTree = tree;
        return this;
    }

    void setFormTree(FormTree tree) {
        this.formTree = tree;
    }

    public SkillBuilder racialSkill() {
        return new SkillBuilder(this);
    }


    public DisplayBuilder display() {
        return new DisplayBuilder(this);
    }

    public StatsBuilder stats() {
        return new StatsBuilder(this);
    }

    public AttributeConfigBuilder attributeConfig() {
        return new AttributeConfigBuilder(this);
    }

    void setAttributeConfig(RaceAttributeConfig config) {
        this.attributeConfig = config;
    }

    public Race build() {
        return build(null);
    }

    public Race build(AddonRegistries.Races registry) {
        if (skill == null)
            throw new IllegalStateException("Race '" + name + "' is missing a racial skill.");

        if (formTree != null) {
            for (FormTree.Branch branch : formTree.getBranches()) {
                branch.setUnlockLevel(skill.getBranchUnlockLevel(branch));
            }
        }

        Race race = new Race(id, name, menuName, display, stats, skill, formTree,
            attributeConfig != null ? attributeConfig : RaceAttributeConfig.defaults());
        if(registry != null)
            registry.register(race);

        return race;
    }

    // ══════════════════════════════════════════════════════════
    // Sub-builders
    // ══════════════════════════════════════════════════════════

    public static class SkillBuilder {
        private final RaceBuilder parent;
        private int maxLevel = 5;
        private int defaultTPCost = 100;
        private int defaultMindCost = 10;
        private String displayName = "Super Form";
        private String description = "Super Form";
        private final LinkedHashMap<Integer, RaceSkill.LevelEntry> levelEntries = new LinkedHashMap<Integer, RaceSkill.LevelEntry>();

        SkillBuilder(RaceBuilder parent) { this.parent = parent; }

        public SkillBuilder maxLevel(int level) {
            this.maxLevel = level; return this;
        }

        public SkillBuilder defaultTPCost(int tpCost) {
            this.defaultTPCost = tpCost;
            return this;
        }

        public SkillBuilder defaultMindCost(int mindCost) {
            this.defaultMindCost = mindCost;
            return this;
        }

        public SkillBuilder displayName(String name) {
            this.displayName = name;
            return this;
        }

        public SkillBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SkillBuilder level(int level, Form form) {
            return level(level, form, defaultTPCost, defaultMindCost);
        }

        public SkillBuilder level(int level, Form form, int tpCost, int mindCost) {
            if (form == null)
                throw new IllegalArgumentException("Form must not be null for level " + level);
            RaceSkill.LevelEntry entry = new RaceSkill.LevelEntry(level, form, tpCost, mindCost);
            if (levelEntries.containsKey(level))
                throw new IllegalArgumentException("Duplicate racial skill entry for level " + level);
            levelEntries.put(level, entry);
            return this;
        }

        public RaceBuilder and() {
            RaceSkill skill = new RaceSkill(maxLevel);
            skill.setDisplayName(displayName);
            skill.setDescription(description);
            for (RaceSkill.LevelEntry entry : levelEntries.values()) {
                skill.addLevelEntry(entry);
            }
            parent.skill = skill;
            return parent;
        }
    }

    // ══════════════════════════════════════════════════════════
    // StatsBuilder — top-level race stats authoring
    // ══════════════════════════════════════════════════════════

    /**
     * Fluent builder for {@link RaceStats}.
     *
     * <p>Usage pattern:</p>
     * <pre>{@code
     * .stats()
     *     .allClasses()
     *         .startAttr().str(15).dex(10).con(10).will(15).mnd(5).spi(5).and()
     *         .statMulti().melee(2.5).defense(4.0).body(20.0)...and()
     *     .forClass(MARTIAL_ARTIST)
     *         .statBonus().melee(30).energyPower(20).flySpeed(10).and()
     *     .forClass(SPIRITUALIST)
     *         .statBonus().melee(20).defense(10).body(-10)...and()
     *     .forClass(WARRIOR)
     *         .statBonus().melee(40).defense(-10).body(10)...and()
     *     .and()
     * }</pre>
     *
     * <p>Each {@code forClass()} block inherits from the {@code allClasses()} base and
     * overrides only the values it explicitly sets. If no {@code allClasses()} block is
     * declared, {@link ClassStats#defaults()} is used as the base.</p>
     */
    public static class StatsBuilder {
        private final RaceBuilder parent;

        // Base applied to every class before per-class overrides
        private Map<EnumDBCAttributes, Integer>  baseInitialAttr   = null;
        private Map<EnumDBCAttributes, Double>   baseAttrMulti     = null;
        private Map<EnumDBCStats, Double>        baseStatBonus     = null;
        private Map<EnumDBCStats, Double>        baseStatMulti     = null;

        // Committed per-class overrides
        private final Map<EnumDBCClasses, Map<EnumDBCAttributes, Integer>>  classInitialAttr  = new EnumMap<>(EnumDBCClasses.class);
        private final Map<EnumDBCClasses, Map<EnumDBCAttributes, Double>>   classAttrMulti    = new EnumMap<>(EnumDBCClasses.class);
        private final Map<EnumDBCClasses, Map<EnumDBCStats, Double>>        classStatBonus    = new EnumMap<>(EnumDBCClasses.class);
        private final Map<EnumDBCClasses, Map<EnumDBCStats, Double>>        classStatMulti    = new EnumMap<>(EnumDBCClasses.class);

        StatsBuilder(RaceBuilder parent) { this.parent = parent; }

        /** Begin an override block that applies to all three classes as a shared base. */
        public ClassOverrideBuilder allClasses() {
            return new ClassOverrideBuilder(this, null);
        }

        /** Begin an override block for a specific DBC class. */
        public ClassOverrideBuilder forClass(EnumDBCClasses raceClass) {
            return new ClassOverrideBuilder(this, raceClass);
        }

        /** Commit all class stats and return to {@link RaceBuilder}. */
        public RaceBuilder and() {
            for (EnumDBCClasses cls : EnumDBCClasses.values()) {
                Map<EnumDBCAttributes, Integer> initAttr  = mergeInt(baseInitialAttr, classInitialAttr.get(cls),  ClassStats.DEFAULT_INITIAL_ATTRIBUTES);
                Map<EnumDBCAttributes, Double>  attrMulti = mergeDbl(baseAttrMulti,   classAttrMulti.get(cls),    ClassStats.DEFAULT_ATTRIBUTE_MULTIPLIERS);
                Map<EnumDBCStats, Double>        statBonus = mergeDbl(baseStatBonus,   classStatBonus.get(cls),    ClassStats.DEFAULT_STAT_BONUSES);
                Map<EnumDBCStats, Double>        statMulti = mergeDbl(baseStatMulti,   classStatMulti.get(cls),    ClassStats.DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS);
                parent.stats.set(cls, new ClassStats(initAttr, attrMulti, statBonus, statMulti));
            }
            return parent;
        }

        // ── Internal helpers ─────────────────────────────────────────────────────

        private static <K extends Enum<K>> Map<K, Integer> mergeInt(
                Map<K, Integer> base, Map<K, Integer> override, Map<K, Integer> fallback) {
            Map<K, Integer> result = new EnumMap<>(base != null ? base : fallback);
            if (override != null) result.putAll(override);
            return result;
        }

        private static <K extends Enum<K>, V> Map<K, V> mergeDbl(
                Map<K, V> base, Map<K, V> override, Map<K, V> fallback) {
            Map<K, V> result = new EnumMap<>(base != null ? base : fallback);
            if (override != null) result.putAll(override);
            return result;
        }

        // ── Commit helpers called by ClassOverrideBuilder ────────────────────────

        void commitBase(Map<EnumDBCAttributes, Integer> ia, Map<EnumDBCAttributes, Double> am,
                        Map<EnumDBCStats, Double> sb, Map<EnumDBCStats, Double> sm) {
            if (ia != null) baseInitialAttr = ia;
            if (am != null) baseAttrMulti   = am;
            if (sb != null) baseStatBonus   = sb;
            if (sm != null) baseStatMulti   = sm;
        }

        void commitClass(EnumDBCClasses cls,
                         Map<EnumDBCAttributes, Integer> ia, Map<EnumDBCAttributes, Double> am,
                         Map<EnumDBCStats, Double> sb, Map<EnumDBCStats, Double> sm) {
            if (ia != null) classInitialAttr.put(cls, ia);
            if (am != null) classAttrMulti  .put(cls, am);
            if (sb != null) classStatBonus  .put(cls, sb);
            if (sm != null) classStatMulti  .put(cls, sm);
        }
    }

    // ══════════════════════════════════════════════════════════
    // ClassOverrideBuilder — per-class (or all-classes) scope
    // ══════════════════════════════════════════════════════════

    /**
     * Scope for overriding individual stat groups for a single class or all classes.
     * {@code raceClass == null} means "apply to all classes as base".
     */
    public static class ClassOverrideBuilder {
        private final StatsBuilder parent;
        /** null = allClasses base */
        private final EnumDBCClasses raceClass;

        private Map<EnumDBCAttributes, Integer> initialAttr  = null;
        private Map<EnumDBCAttributes, Double>  attrMulti    = null;
        private Map<EnumDBCStats, Double>       statBonus    = null;
        private Map<EnumDBCStats, Double>       statMulti    = null;

        ClassOverrideBuilder(StatsBuilder parent, EnumDBCClasses raceClass) {
            this.parent    = parent;
            this.raceClass = raceClass;
        }

        // ── Sub-builder entry points ─────────────────────────────────────────────

        /** Configure starting attribute values (STR, DEX, CON, WILL, MND, SPI). */
        public StartAttrBuilder startAttr() {
            return new StartAttrBuilder(this);
        }

        /** Configure per-attribute multipliers. */
        public AttrMultiBuilder attrMulti() {
            return new AttrMultiBuilder(this);
        }

        /** Configure flat stat bonuses (Melee, Defense, Body, …). */
        public StatBonusBuilder statBonus() {
            return new StatBonusBuilder(this);
        }

        /** Configure stat multipliers derived from attributes. */
        public StatMultiBuilder statMulti() {
            return new StatMultiBuilder(this);
        }

        // ── Cross-class helpers ──────────────────────────────────────────────────

        /** Commit this block and start a new all-classes base block. */
        public ClassOverrideBuilder allClasses() {
            commit();
            return new ClassOverrideBuilder(parent, null);
        }

        /** Commit this block and start a new per-class block. */
        public ClassOverrideBuilder forClass(EnumDBCClasses next) {
            commit();
            return new ClassOverrideBuilder(parent, next);
        }

        /** Commit this block and return to {@link StatsBuilder}. */
        public StatsBuilder and() {
            commit();
            return parent;
        }

        // ── Internal ─────────────────────────────────────────────────────────────

        private void commit() {
            if (raceClass == null) {
                parent.commitBase(initialAttr, attrMulti, statBonus, statMulti);
            } else {
                parent.commitClass(raceClass, initialAttr, attrMulti, statBonus, statMulti);
            }
        }

        void setInitialAttr(Map<EnumDBCAttributes, Integer> map) { this.initialAttr = map; }
        void setAttrMulti  (Map<EnumDBCAttributes, Double>  map) { this.attrMulti   = map; }
        void setStatBonus  (Map<EnumDBCStats, Double>       map) { this.statBonus    = map; }
        void setStatMulti  (Map<EnumDBCStats, Double>       map) { this.statMulti    = map; }
    }

    // ══════════════════════════════════════════════════════════
    // StartAttrBuilder — starting attribute values
    // ══════════════════════════════════════════════════════════

    public static class StartAttrBuilder {
        private final ClassOverrideBuilder parent;
        private final Map<EnumDBCAttributes, Integer> map = new EnumMap<>(EnumDBCAttributes.class);

        StartAttrBuilder(ClassOverrideBuilder parent) { this.parent = parent; }

        public StartAttrBuilder str (int v) { map.put(EnumDBCAttributes.STR,  v); return this; }
        public StartAttrBuilder dex (int v) { map.put(EnumDBCAttributes.DEX,  v); return this; }
        public StartAttrBuilder con (int v) { map.put(EnumDBCAttributes.CON,  v); return this; }
        public StartAttrBuilder will(int v) { map.put(EnumDBCAttributes.WILL, v); return this; }
        public StartAttrBuilder mnd (int v) { map.put(EnumDBCAttributes.MND,  v); return this; }
        public StartAttrBuilder spi (int v) { map.put(EnumDBCAttributes.SPI,  v); return this; }
        public StartAttrBuilder set (EnumDBCAttributes attr, int v) { map.put(attr, v); return this; }

        public ClassOverrideBuilder and() {
            parent.setInitialAttr(map);
            return parent;
        }
    }

    // ══════════════════════════════════════════════════════════
    // AttrMultiBuilder — attribute multipliers
    // ══════════════════════════════════════════════════════════

    public static class AttrMultiBuilder {
        private final ClassOverrideBuilder parent;
        private final Map<EnumDBCAttributes, Double> map = new EnumMap<>(EnumDBCAttributes.class);

        AttrMultiBuilder(ClassOverrideBuilder parent) { this.parent = parent; }

        public AttrMultiBuilder str (double v) { map.put(EnumDBCAttributes.STR,  v); return this; }
        public AttrMultiBuilder dex (double v) { map.put(EnumDBCAttributes.DEX,  v); return this; }
        public AttrMultiBuilder con (double v) { map.put(EnumDBCAttributes.CON,  v); return this; }
        public AttrMultiBuilder will(double v) { map.put(EnumDBCAttributes.WILL, v); return this; }
        public AttrMultiBuilder mnd (double v) { map.put(EnumDBCAttributes.MND,  v); return this; }
        public AttrMultiBuilder spi (double v) { map.put(EnumDBCAttributes.SPI,  v); return this; }
        public AttrMultiBuilder set (EnumDBCAttributes attr, double v) { map.put(attr, v); return this; }

        public ClassOverrideBuilder and() {
            parent.setAttrMulti(map);
            return parent;
        }
    }

    // ══════════════════════════════════════════════════════════
    // StatBonusBuilder — flat stat bonuses
    // ══════════════════════════════════════════════════════════

    public static class StatBonusBuilder {
        private final ClassOverrideBuilder parent;
        private final Map<EnumDBCStats, Double> map = new EnumMap<>(EnumDBCStats.class);

        StatBonusBuilder(ClassOverrideBuilder parent) { this.parent = parent; }

        public StatBonusBuilder melee       (double v) { map.put(EnumDBCStats.MELEE,             v); return this; }
        public StatBonusBuilder defense     (double v) { map.put(EnumDBCStats.DEFENSE,           v); return this; }
        public StatBonusBuilder body        (double v) { map.put(EnumDBCStats.BODY,              v); return this; }
        public StatBonusBuilder stamina     (double v) { map.put(EnumDBCStats.STAMINA,           v); return this; }
        public StatBonusBuilder energyPower (double v) { map.put(EnumDBCStats.ENERGY_POWER,      v); return this; }
        public StatBonusBuilder energyPool  (double v) { map.put(EnumDBCStats.ENERGY_POOL,       v); return this; }
        public StatBonusBuilder maxSkills   (double v) { map.put(EnumDBCStats.MAX_SKILLS,        v); return this; }
        public StatBonusBuilder speed       (double v) { map.put(EnumDBCStats.SPEED,             v); return this; }
        public StatBonusBuilder regenBody   (double v) { map.put(EnumDBCStats.REGEN_RATE_BODY,   v); return this; }
        public StatBonusBuilder regenStamina(double v) { map.put(EnumDBCStats.REGEN_RATE_STAMINA,v); return this; }
        public StatBonusBuilder regenEnergy (double v) { map.put(EnumDBCStats.REGEN_RATE_ENERGY, v); return this; }
        public StatBonusBuilder flySpeed    (double v) { map.put(EnumDBCStats.FLY_SPEED,         v); return this; }
        public StatBonusBuilder set(EnumDBCStats stat, double v) { map.put(stat, v); return this; }

        public ClassOverrideBuilder and() {
            parent.setStatBonus(map);
            return parent;
        }
    }

    // ══════════════════════════════════════════════════════════
    // StatMultiBuilder — stat multipliers from attributes
    // ══════════════════════════════════════════════════════════

    public static class StatMultiBuilder {
        private final ClassOverrideBuilder parent;
        private final Map<EnumDBCStats, Double> map = new EnumMap<>(EnumDBCStats.class);

        StatMultiBuilder(ClassOverrideBuilder parent) { this.parent = parent; }

        public StatMultiBuilder melee       (double v) { map.put(EnumDBCStats.MELEE,             v); return this; }
        public StatMultiBuilder defense     (double v) { map.put(EnumDBCStats.DEFENSE,           v); return this; }
        public StatMultiBuilder body        (double v) { map.put(EnumDBCStats.BODY,              v); return this; }
        public StatMultiBuilder stamina     (double v) { map.put(EnumDBCStats.STAMINA,           v); return this; }
        public StatMultiBuilder energyPower (double v) { map.put(EnumDBCStats.ENERGY_POWER,      v); return this; }
        public StatMultiBuilder energyPool  (double v) { map.put(EnumDBCStats.ENERGY_POOL,       v); return this; }
        public StatMultiBuilder maxSkills   (double v) { map.put(EnumDBCStats.MAX_SKILLS,        v); return this; }
        public StatMultiBuilder speed       (double v) { map.put(EnumDBCStats.SPEED,             v); return this; }
        public StatMultiBuilder regenBody   (double v) { map.put(EnumDBCStats.REGEN_RATE_BODY,   v); return this; }
        public StatMultiBuilder regenStamina(double v) { map.put(EnumDBCStats.REGEN_RATE_STAMINA,v); return this; }
        public StatMultiBuilder regenEnergy (double v) { map.put(EnumDBCStats.REGEN_RATE_ENERGY, v); return this; }
        public StatMultiBuilder flySpeed    (double v) { map.put(EnumDBCStats.FLY_SPEED,         v); return this; }
        public StatMultiBuilder set(EnumDBCStats stat, double v) { map.put(stat, v); return this; }

        public ClassOverrideBuilder and() {
            parent.setStatMulti(map);
            return parent;
        }
    }

    // ══════════════════════════════════════════════════════════
    // AttributeConfigBuilder — per-race attribute calculation configs
    // ══════════════════════════════════════════════════════════

    /**
     * Fluent builder for {@link RaceAttributeConfig}.
     *
     * <p>Uses nested scopes so race authors can group related settings the same way
     * they already do in {@link StatsBuilder}: base-form values, mystic values, and
     * outer getPlayerAttribute stackables/global toggles.</p>
     */
    public static class AttributeConfigBuilder {
        private final RaceBuilder parent;

        private final Map<EnumDBCAttributes, Float> baseMulti = defaultMultiMap();
        private final Map<EnumDBCAttributes, Integer> baseFlat = defaultFlatMap();
        private final Map<EnumDBCAttributes, Float> mysticMulti = defaultMultiMap();
        private final Map<EnumDBCAttributes, Integer> mysticFlat = defaultFlatMap();
        private float mysticDamMulti = -1.0f;
        private RaceAttributeConfig.MysticFormula mysticFormula = RaceAttributeConfig.MysticFormula.ATTRIBUTE_MULTI_PLUS_SKILL;
        private float attrBonusPerSkillLevel = 0.0f;
        private float mysticAttrBonusPerSkillLevel = 0.0f;
        private float godAttrMultiRace = 1.0f;
        private List<Float> uiAttrMultiRaceList = Arrays.asList(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
        private boolean legendaryAppliesInBase = true;

        AttributeConfigBuilder(RaceBuilder parent) { this.parent = parent; }

        private static Map<EnumDBCAttributes, Float> defaultMultiMap() {
            Map<EnumDBCAttributes, Float> map = new EnumMap<>(EnumDBCAttributes.class);
            for (EnumDBCAttributes attr : EnumDBCAttributes.values()) {
                map.put(attr, attr == EnumDBCAttributes.MND ? 0.0f : 1.0f);
            }
            return map;
        }

        private static Map<EnumDBCAttributes, Integer> defaultFlatMap() {
            Map<EnumDBCAttributes, Integer> map = new EnumMap<>(EnumDBCAttributes.class);
            for (EnumDBCAttributes attr : EnumDBCAttributes.values()) {
                map.put(attr, 0);
            }
            return map;
        }

        /** Open a scope for base-form attribute multipliers and flat bonuses. */
        public AttributeValueBuilder base() {
            return new AttributeValueBuilder(this, baseMulti, baseFlat);
        }

        /** Open a scope for mystic (Potential Unleashed) attribute values, formula, and damage multiplier. */
        public MysticConfigBuilder mystic() {
            return new MysticConfigBuilder(this);
        }

        /** Open a scope for cross-state stackable modifiers (skill-level bonuses, GoD, UI, Legendary). */
        public StackableConfigBuilder stackables() {
            return new StackableConfigBuilder(this);
        }

        /** Commit the attribute config and return to {@link RaceBuilder}. */
        public RaceBuilder and() {
            parent.setAttributeConfig(new RaceAttributeConfig(
                new RaceAttributeConfig.FormAttributeBonus(baseMulti, baseFlat),
                new RaceAttributeConfig.FormAttributeBonus(mysticMulti, mysticFlat),
                mysticDamMulti,
                mysticFormula,
                attrBonusPerSkillLevel,
                mysticAttrBonusPerSkillLevel,
                godAttrMultiRace,
                uiAttrMultiRaceList,
                legendaryAppliesInBase
            ));
            return parent;
        }
    }

    /**
     * Sets per-attribute percentage multipliers and flat bonuses for a form state
     * (base or mystic). Which state this targets depends on the parent scope.
     *
     * <p>Defaults: multi 1.0 for all attributes (except MND which defaults to 0.0),
     * flat 0 for all. Only values you set are overridden.</p>
     */
    public static class AttributeValueBuilder {
        private final AttributeConfigBuilder parent;
        private final Map<EnumDBCAttributes, Float> multiMap;
        private final Map<EnumDBCAttributes, Integer> flatMap;

        AttributeValueBuilder(AttributeConfigBuilder parent, Map<EnumDBCAttributes, Float> multiMap, Map<EnumDBCAttributes, Integer> flatMap) {
            this.parent = parent;
            this.multiMap = multiMap;
            this.flatMap = flatMap;
        }

        /**
         * Set all six attribute multipliers at once (order: STR, DEX, CON, WIL, MND, SPI).
         * A value of 1.0 means no change; 0.0 disables the attribute's contribution.
         */
        public AttributeValueBuilder multi(float str, float dex, float con, float wil, float mnd, float spi) {
            multiMap.put(EnumDBCAttributes.STR, str);
            multiMap.put(EnumDBCAttributes.DEX, dex);
            multiMap.put(EnumDBCAttributes.CON, con);
            multiMap.put(EnumDBCAttributes.WILL, wil);
            multiMap.put(EnumDBCAttributes.MND, mnd);
            multiMap.put(EnumDBCAttributes.SPI, spi);
            return this;
        }

        /** Set the multiplier for a single attribute. */
        public AttributeValueBuilder multi(EnumDBCAttributes attr, float value) {
            multiMap.put(attr, value);
            return this;
        }

        /** Set all six flat attribute bonuses at once (order: STR, DEX, CON, WIL, MND, SPI). Added after multipliers. */
        public AttributeValueBuilder flat(int str, int dex, int con, int wil, int mnd, int spi) {
            flatMap.put(EnumDBCAttributes.STR, str);
            flatMap.put(EnumDBCAttributes.DEX, dex);
            flatMap.put(EnumDBCAttributes.CON, con);
            flatMap.put(EnumDBCAttributes.WILL, wil);
            flatMap.put(EnumDBCAttributes.MND, mnd);
            flatMap.put(EnumDBCAttributes.SPI, spi);
            return this;
        }

        /** Set the flat bonus for a single attribute. */
        public AttributeValueBuilder flat(EnumDBCAttributes attr, int value) {
            flatMap.put(attr, value);
            return this;
        }

        /** Commit these attribute values and return to {@link AttributeConfigBuilder}. */
        public AttributeConfigBuilder and() {
            return parent;
        }
    }

    /**
     * Configures how the race behaves when Mystic (Potential Unleashed) is active:
     * attribute overrides, damage multiplier, formula selection, and per-skill-level scaling.
     */
    public static class MysticConfigBuilder {
        private final AttributeConfigBuilder parent;

        MysticConfigBuilder(AttributeConfigBuilder parent) {
            this.parent = parent;
        }

        /** Open a scope for mystic-state attribute multipliers and flat bonuses. */
        public MysticValueBuilder values() {
            return new MysticValueBuilder(this, parent.mysticMulti, parent.mysticFlat);
        }

        /**
         * Set an explicit mystic damage multiplier. Overrides the per-attribute mystic values
         * for damage calculation. Pass {@code -1.0f} (or call {@link #useStateValues()}) to
         * use the per-attribute mystic multipliers instead.
         */
        public MysticConfigBuilder damMulti(float multi) {
            parent.mysticDamMulti = multi;
            return this;
        }

        /** Reset mystic damage multiplier to -1.0 (use per-attribute mystic values instead of a flat override). This is the default. */
        public MysticConfigBuilder useStateValues() {
            parent.mysticDamMulti = -1.0f;
            return this;
        }

        /**
         * Select which DBC formula family to use for mystic attribute calculation.
         * {@code ATTRIBUTE_MULTI_PLUS_SKILL} (default, used by Human/Namekian/Majin) or
         * {@code PERCENT_MULTI_TIMES_SKILL} (used by Saiyan/Half-Saiyan/Arcosian).
         */
        public MysticConfigBuilder formula(RaceAttributeConfig.MysticFormula formula) {
            parent.mysticFormula = formula;
            return this;
        }

        /** Bonus applied per racial skill level while mystic is active: {@code multi = 1.0 + bonus * skillLevel}. */
        public MysticConfigBuilder attrBonusPerSkillLevel(float bonus) {
            parent.mysticAttrBonusPerSkillLevel = bonus;
            return this;
        }

        /** Commit mystic config and return to {@link AttributeConfigBuilder}. */
        public AttributeConfigBuilder and() {
            return parent;
        }
    }

    /**
     * Sets per-attribute multipliers and flat bonuses for the mystic (Potential Unleashed) form state.
     * Same API as {@link AttributeValueBuilder} but writes to the mystic attribute maps.
     */
    public static class MysticValueBuilder {
        private final MysticConfigBuilder parent;
        private final Map<EnumDBCAttributes, Float> multiMap;
        private final Map<EnumDBCAttributes, Integer> flatMap;

        MysticValueBuilder(MysticConfigBuilder parent, Map<EnumDBCAttributes, Float> multiMap, Map<EnumDBCAttributes, Integer> flatMap) {
            this.parent = parent;
            this.multiMap = multiMap;
            this.flatMap = flatMap;
        }

        /** Set all six mystic attribute multipliers at once (order: STR, DEX, CON, WIL, MND, SPI). */
        public MysticValueBuilder multi(float str, float dex, float con, float wil, float mnd, float spi) {
            multiMap.put(EnumDBCAttributes.STR, str);
            multiMap.put(EnumDBCAttributes.DEX, dex);
            multiMap.put(EnumDBCAttributes.CON, con);
            multiMap.put(EnumDBCAttributes.WILL, wil);
            multiMap.put(EnumDBCAttributes.MND, mnd);
            multiMap.put(EnumDBCAttributes.SPI, spi);
            return this;
        }

        /** Set the mystic multiplier for a single attribute. */
        public MysticValueBuilder multi(EnumDBCAttributes attr, float value) {
            multiMap.put(attr, value);
            return this;
        }

        /** Set all six mystic flat bonuses at once (order: STR, DEX, CON, WIL, MND, SPI). */
        public MysticValueBuilder flat(int str, int dex, int con, int wil, int mnd, int spi) {
            flatMap.put(EnumDBCAttributes.STR, str);
            flatMap.put(EnumDBCAttributes.DEX, dex);
            flatMap.put(EnumDBCAttributes.CON, con);
            flatMap.put(EnumDBCAttributes.WILL, wil);
            flatMap.put(EnumDBCAttributes.MND, mnd);
            flatMap.put(EnumDBCAttributes.SPI, spi);
            return this;
        }

        /** Set the mystic flat bonus for a single attribute. */
        public MysticValueBuilder flat(EnumDBCAttributes attr, int value) {
            flatMap.put(attr, value);
            return this;
        }

        /** Commit mystic values and return to {@link MysticConfigBuilder}. */
        public MysticConfigBuilder and() {
            return parent;
        }
    }

    /**
     * Configures global/stackable attribute modifiers that apply outside the inner
     * per-form-state calculation: racial skill level bonuses for base form, God of Destruction
     * multiplier, Ultra Instinct per-level multipliers, and Legendary base-form eligibility.
     */
    public static class StackableConfigBuilder {
        private final AttributeConfigBuilder parent;

        StackableConfigBuilder(AttributeConfigBuilder parent) {
            this.parent = parent;
        }

        /** Bonus applied per racial skill level in base form: {@code multi = 1.0 + bonus * skillLevel}. Default 0.0. */
        public StackableConfigBuilder baseAttrBonusPerSkillLevel(float bonus) {
            parent.attrBonusPerSkillLevel = bonus;
            return this;
        }

        /** Race-specific God of Destruction attribute multiplier. Stacks with the global GoD config. Default 1.0. */
        public StackableConfigBuilder godAttrMultiRace(float multi) {
            parent.godAttrMultiRace = multi;
            return this;
        }

        /**
         * Per-UI-level Ultra Instinct attribute multipliers for this race (varargs, one per UI level).
         * Each entry stacks with the global UI config. Default: all 1.0.
         */
        public StackableConfigBuilder uiAttrMultiRace(Float... multis) {
            parent.uiAttrMultiRaceList = new ArrayList<>(Arrays.asList(multis));
            return this;
        }

        /** List variant of {@link #uiAttrMultiRace(Float...)}. */
        public StackableConfigBuilder uiAttrMultiRace(List<Float> multis) {
            parent.uiAttrMultiRaceList = new ArrayList<>(multis);
            return this;
        }

        /** Whether Legendary status bonus applies when this race is in base form. Default {@code true}. */
        public StackableConfigBuilder legendaryAppliesInBase(boolean applies) {
            parent.legendaryAppliesInBase = applies;
            return this;
        }

        /** Commit stackable config and return to {@link AttributeConfigBuilder}. */
        public AttributeConfigBuilder and() {
            return parent;
        }
    }

    // ══════════════════════════════════════════════════════════
    // DisplayBuilder
    // ══════════════════════════════════════════════════════════

    public static class DisplayBuilder {

        private final RaceBuilder parent;

        DisplayBuilder(RaceBuilder parent) {
            this.parent = parent;
        }

        // ── Components ─────────────────────────────────────────────────────────────

        private DisplayComponent resolveComponent(String componentId, boolean checkSubLayer) {
            DisplayComponent component = parent.display.getComponent(componentId);
            if (component == null && !checkSubLayer) return null;

            if (component == null) {
                for (DisplayComponent c : parent.display.getComponents().values()) {
                    if (!c.hasSubComponent()) continue;
                    component = c.getSubComponent();
                }
            }

            return component;
        }

        /** Registers a pre-built {@link DisplayComponent} on the display. */
        public DisplayBuilder addComponent(DisplayComponent component) {
            parent.display.addComponent(component);
            return this;
        }

        public DisplayBuilder addLayer(String componentId, DisplayLayer layer, boolean checkSubLayer) {
            DisplayComponent component = resolveComponent(componentId, checkSubLayer);
            if (component == null) return this;

            component.addLayer(layer);
            return this;
        }

        public DisplayBuilder addLayer(String componentId, DisplayLayer layer) {
            return addLayer(componentId, layer, false);
        }

        public DisplayBuilder color(String componentId, String layerId, Color color, boolean override, boolean checkSubLayer) {
            DisplayComponent component = resolveComponent(componentId, checkSubLayer);
            if (component == null) return this;

            DisplayLayer layer = component.findLayer(layerId);
            if (layer == null) return this;

            layer.setFixedColor(override);
            layer.setDefaultColor(color);
            return this;
        }

        public DisplayBuilder color(String componentId, String layerId, Color color, boolean override) {
            return color(componentId, layerId, color, override, false);
        }

        public DisplayBuilder color(String componentId, String layerId, Color color) {
            return color(componentId, layerId, color, false, false);
        }

        public DisplayBuilder color(String componentId, String layerId, int color, boolean override, boolean checkSubLayer) {
            return color(componentId, layerId, new Color(color), override, checkSubLayer);
        }

        public DisplayBuilder color(String componentId, String layerId, int color, boolean override) {
            return color(componentId, layerId, new Color(color), override, false);
        }

        public DisplayBuilder color(String componentId, String layerId, int color) {
            return color(componentId, layerId, new Color(color), false, false);
        }

        public DisplayBuilder color(String componentId, String layerId, boolean checkSubLayer, Function<RaceRenderContext, Color> colorFunc) {
            DisplayComponent component = resolveComponent(componentId, checkSubLayer);
            if (component == null) return this;

            DisplayLayer layer = component.findLayer(layerId);
            if (layer == null) return this;

            layer.setColorFunction(colorFunc);
            return this;
        }

        public DisplayBuilder color(String componentId, String layerId, Function<RaceRenderContext, Color> colorFunc) {
            return color(componentId, layerId, false, colorFunc);
        }

        public DisplayBuilder texture(String componentId, String layerId, String texture, boolean override, boolean checkSubLayer) {
            DisplayComponent component = resolveComponent(componentId, checkSubLayer);
            if (component == null) return this;

            DisplayLayer layer = component.findLayer(layerId);
            if (layer == null) return this;

            if (override) {
                layer.setDefaultTexture(texture);
                layer.setFixedTexture(true);
            } else {
                layer.addTextureVariant(texture);
            }
            return this;
        }

        public DisplayBuilder texture(String componentId, String layerId, String texture, boolean override) {
            return texture(componentId, layerId, texture, override, false);
        }

        public DisplayBuilder texture(String componentId, String layerId, String texture) {
            return texture(componentId, layerId, texture, false, false);
        }

        // ── General Components Colors ────────────────────────────────────────────────

        public DisplayBuilder bodyCM(int color, boolean override) {
            return color(RaceDisplay.COMPONENT_BODY, RaceDisplay.LAYER_BODY_CM, color, override);
        }

        public DisplayBuilder bodyC1(int color, boolean override) {
            return color(RaceDisplay.COMPONENT_BODY, RaceDisplay.LAYER_BODY_C1, color, override);
        }

        public DisplayBuilder bodyC2(int color, boolean override) {
            return color(RaceDisplay.COMPONENT_BODY, RaceDisplay.LAYER_BODY_C2, color, override);
        }

        public DisplayBuilder bodyC3(int color, boolean override) {
            return color(RaceDisplay.COMPONENT_BODY, RaceDisplay.LAYER_BODY_C3, color, override);
        }

        public DisplayBuilder eyebaseC(int color, boolean override) {
            return color(RaceDisplay.COMPONENT_FACE, RaceDisplay.LAYER_EYEBASE, color, override);
        }

        public DisplayBuilder eyebrowC(int color, boolean override) {
            return color(RaceDisplay.COMPONENT_FACE, RaceDisplay.LAYER_EYEBROWS, color, override);
        }

        public DisplayBuilder eyeC1(int color, boolean override) {
            return color(RaceDisplay.COMPONENT_EYES, RaceDisplay.LAYER_LEFT_EYE, color, override, true);
        }

        public DisplayBuilder eyeC2(int color, boolean override) {
            return color(RaceDisplay.COMPONENT_EYES, RaceDisplay.LAYER_RIGHT_EYE, color, override, true);
        }

        public DisplayBuilder noseC(int color, boolean override) {
            return color(RaceDisplay.COMPONENT_FACE, RaceDisplay.LAYER_NOSE, color, override);
        }

        public DisplayBuilder mouthC(int color, boolean override) {
            return color(RaceDisplay.COMPONENT_FACE, RaceDisplay.LAYER_MOUTH, color, override);
        }

        public DisplayBuilder hairC(int color, boolean override) {
            return color(RaceDisplay.COMPONENT_HAIR, RaceDisplay.LAYER_HAIR, color, override);
        }

        // ── General Components Textures ────────────────────────────────────────────

        public DisplayBuilder bodyM(String texture, boolean override) {
            return texture(RaceDisplay.COMPONENT_BODY, RaceDisplay.LAYER_BODY_CM, texture, override);
        }

        public DisplayBuilder body1(String texture, boolean override) {
            return texture(RaceDisplay.COMPONENT_BODY, RaceDisplay.LAYER_BODY_C1, texture, override);
        }

        public DisplayBuilder body2(String texture, boolean override) {
            return texture(RaceDisplay.COMPONENT_BODY, RaceDisplay.LAYER_BODY_C2, texture, override);
        }

        public DisplayBuilder body3(String texture, boolean override) {
            return texture(RaceDisplay.COMPONENT_BODY, RaceDisplay.LAYER_BODY_C3, texture, override);
        }

        public DisplayBuilder eyebase(String texture, boolean override) {
            return texture(RaceDisplay.COMPONENT_FACE, RaceDisplay.LAYER_EYEBASE, texture, override);
        }

        public DisplayBuilder eyebrow(String texture, boolean override) {
            return texture(RaceDisplay.COMPONENT_FACE, RaceDisplay.LAYER_EYEBROWS, texture, override);
        }

        public DisplayBuilder eyeLeft(String texture, boolean override) {
            return texture(RaceDisplay.COMPONENT_EYES, RaceDisplay.LAYER_LEFT_EYE, texture, override, true);
        }

        public DisplayBuilder eyeRight(String texture, boolean override) {
            return texture(RaceDisplay.COMPONENT_EYES, RaceDisplay.LAYER_RIGHT_EYE, texture, override, true);
        }

        public DisplayBuilder nose(String texture, boolean override) {
            return texture(RaceDisplay.COMPONENT_FACE, RaceDisplay.LAYER_NOSE, texture, override);
        }

        public DisplayBuilder mouth(String texture, boolean override) {
            return texture(RaceDisplay.COMPONENT_FACE, RaceDisplay.LAYER_MOUTH, texture, override);
        }

        public DisplayBuilder hair(String texture, boolean override) {
            return texture(RaceDisplay.COMPONENT_HAIR, RaceDisplay.LAYER_HAIR, texture, override);
        }

        // ── Metadata ───────────────────────────────────────────────────────────────

        public DisplayBuilder renderer(String rendererKey) {
            parent.display.rendererKey = rendererKey;
            return this;
        }

        public DisplayBuilder skinLimits(int bodyType, int colorSlots, int nose, int mouth, int eyes, int eyeColorSlots) {
            parent.display.setSkinLimits(bodyType, colorSlots, nose, mouth, eyes, eyeColorSlots);
            return this;
        }

        public DisplayBuilder genderCount(int count) {
            parent.display.setGenderCount(count);
            return this;
        }

        public DisplayBuilder hairType(String type) {
            parent.display.setHairType(type);
            return this;
        }

        public DisplayBuilder allowedPowerTypes(String types) {
            parent.display.setAllowedPowerTypes(types);
            return this;
        }

        public DisplayBuilder customSkinMode(int mode) {
            parent.display.setCustomSkinMode(mode);
            return this;
        }

        public DisplayBuilder raceAllow(String allow) {
            parent.display.setRaceAllow(allow);
            return this;
        }

        // ── Finalise ───────────────────────────────────────────────────────────────

        public RaceBuilder and() {
            parent.display.syncCreatorMetadata();
            if (parent == null)
                throw new IllegalStateException("RaceDisplayBuilder was not created from a RaceBuilder; use build() instead.");
            return parent;
        }

        public RaceBuilder build() {
            parent.display.syncCreatorMetadata();
            return parent;
        }
    }
}
