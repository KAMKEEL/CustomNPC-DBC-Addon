package kamkeel.npcdbc.data.race.builder;

import kamkeel.npcdbc.constants.enums.EnumDBCAttributes;
import kamkeel.npcdbc.constants.enums.EnumDBCClasses;
import kamkeel.npcdbc.constants.enums.EnumDBCStats;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.display.*;
import kamkeel.npcdbc.data.race.registry.RaceRegistry;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.progression.RaceSkill;
import kamkeel.npcdbc.data.race.stats.ClassStats;
import kamkeel.npcdbc.data.race.stats.RaceStats;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RaceBuilder {
    private final int id;
    private final String name;
    private final String menuName;

    private RaceRegistry registry;
    private RaceSkill skill;
    private RaceStats stats = new RaceStats();
    private RaceDisplay display = new RaceDisplay();
    private FormTree formTree = null;

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

    public ClassStatsBuilder forClass(EnumDBCClasses raceClass) {
        return new ClassStatsBuilder(this, raceClass);
    }

    public RaceBuilder registry(RaceRegistry registry) {
        this.registry = registry;
        return this;
    }

    public Race build() {
        if (skill == null)
            throw new IllegalStateException("Race '" + name + "' is missing a racial skill.");

        if (formTree != null) {
            for (FormTree.Branch branch : formTree.getBranches()) {
                branch.setUnlockLevel(skill.getBranchUnlockLevel(branch));
            }
        }

        return new Race(id, name, menuName, registry, display, stats, skill, formTree);
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

    public static class ClassStatsBuilder {
        private final RaceBuilder parent;
        private final EnumDBCClasses raceClass;

        private final Map<EnumDBCAttributes, Integer> initialAttributes = new EnumMap<>(ClassStats.DEFAULT_INITIAL_ATTRIBUTES);
        private final Map<EnumDBCAttributes, Double> attributeMultipliers = new EnumMap<>(ClassStats.DEFAULT_ATTRIBUTE_MULTIPLIERS);
        private final Map<EnumDBCStats, Double> statBonuses = new EnumMap<>(ClassStats.DEFAULT_STAT_BONUSES);
        private final Map<EnumDBCStats, Double> statAttributeMultipliers = new EnumMap<>(ClassStats.DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS);

        ClassStatsBuilder(RaceBuilder parent, EnumDBCClasses raceClass) {
            this.parent = parent;
            this.raceClass = raceClass;
        }

        public ClassStatsBuilder initialAttribute(EnumDBCAttributes attr, int value) {
            initialAttributes.put(attr, value);
            return this;
        }

        public ClassStatsBuilder attributeMultiplier(EnumDBCAttributes attr, double value) {
            attributeMultipliers.put(attr, value);
            return this;
        }

        public ClassStatsBuilder statBonus(EnumDBCStats stat, double value) {
            statBonuses.put(stat, value);
            return this;
        }

        public ClassStatsBuilder statAttributeMultiplier(EnumDBCStats stat, double value) {
            statAttributeMultipliers.put(stat, value);
            return this;
        }

        public ClassStatsBuilder forClass(EnumDBCClasses next) {
            and();
            return new ClassStatsBuilder(parent, next);
        }

        public RaceBuilder and() {
            parent.stats.set(raceClass, new ClassStats(
                initialAttributes,
                attributeMultipliers,
                statBonuses,
                statAttributeMultipliers
            ));
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

        /** Registers a pre-built {@link DisplayComponent} on the display. */
        public DisplayBuilder addComponent(DisplayComponent component) {
            parent.display.addComponent(component);
            return this;
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
