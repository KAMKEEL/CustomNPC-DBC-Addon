package kamkeel.npcdbc.data.race.builder;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.constants.enums.EnumDBCAttributes;
import kamkeel.npcdbc.constants.enums.EnumDBCClasses;
import kamkeel.npcdbc.constants.enums.EnumDBCStats;
import kamkeel.npcdbc.data.form.BuiltInForm;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.display.ColorPreset;
import kamkeel.npcdbc.data.race.display.ColorSlot;
import kamkeel.npcdbc.data.race.display.RaceDisplay;
import kamkeel.npcdbc.data.race.display.TextureSlot;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.progression.RaceSkill;
import kamkeel.npcdbc.data.race.stats.ClassStats;
import kamkeel.npcdbc.data.race.stats.RaceStats;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.LogWriter;

import java.util.EnumMap;
import java.util.Map;

public class RaceBuilder {
    private final int id;
    private final String name;
    private final String menuName;

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

    public SkillBuilder skill() {
        return new SkillBuilder(this);
    }

    public DisplayBuilder display() {
        return new DisplayBuilder(this);
    }

    public ClassStatsBuilder forClass(EnumDBCClasses raceClass) {
        return new ClassStatsBuilder(this, raceClass);
    }

    public Race build() {
        if (skill == null)
            throw new IllegalStateException("Race '" + name + "' is missing a racial skill.");
        return new Race(id, name, menuName, display, stats, skill, formTree);
    }

    // ══════════════════════════════════════════════════════════
    // Sub-builders
    // ══════════════════════════════════════════════════════════

    public static class SkillBuilder {
        private final RaceBuilder parent;
        private int maxLevel = 5;
        private int[] tpCosts = {100};
        private int[] mindCosts = {10};

        SkillBuilder(RaceBuilder parent) { this.parent = parent; }

        public SkillBuilder maxLevel(int level) {
            this.maxLevel = level; return this;
        }
        public SkillBuilder tpCosts(int... costs) {
            this.tpCosts = costs; return this;
        }
        public SkillBuilder mindCosts(int... costs) {
            this.mindCosts = costs; return this;
        }

        public RaceBuilder and() {
            parent.skill = new RaceSkill(maxLevel, tpCosts, mindCosts);
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

    public static class DisplayBuilder {
        private final RaceBuilder parent;

        DisplayBuilder(RaceBuilder parent) {
            this.parent = parent;
        }

        public DisplayBuilder addColorSlot(String id, String displayName) {
            parent.display.addColorSlot(new ColorSlot(id, displayName));
            return this;
        }

        public DisplayBuilder addColorPreset(ColorPreset preset) {
            parent.display.addColorPreset(preset);
            return this;
        }

        public DisplayBuilder addColorOverride(String id, Color color) {
            parent.display.addColorOverride(id, color);
            return this;
        }

        public DisplayBuilder addTexture(String slotId, ResourceLocation texture) {
            TextureSlot slot = parent.display.getTextureSlot(slotId);
            if (slot != null)
                slot.add(texture);
            return this;
        }

        public RaceBuilder and() {
            return parent;
        }
    }

    public static class FormTreeBuilder {
        private final RaceBuilder parent;
        private final FormTree formTree;

        FormTreeBuilder(RaceBuilder parent, String raceNamespace) {
            this.parent = parent;
            this.formTree = new FormTree(raceNamespace);
        }

        public LevelBuilder level(int level) {
            return new LevelBuilder(this, level);
        }

        public RaceBuilder and() {
            parent.formTree = formTree;
            return parent;
        }

        public FormTree getFormTree() {
            return formTree;
        }

        public class LevelBuilder {
            private final FormTreeBuilder parent;
            private final int level;

            LevelBuilder(FormTreeBuilder parent, int level) {
                this.parent = parent;
                this.level = level;
            }

            public LevelBuilder add(BuiltInForm form) {
                parent.formTree.add(level, form);
                return this;
            }

            public LevelBuilder level(int nextLevel) {
                return new LevelBuilder(parent, nextLevel);
            }

            public RaceBuilder and() {
                return parent.and();
            }
        }
    }
}
