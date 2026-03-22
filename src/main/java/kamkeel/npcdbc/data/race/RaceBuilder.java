package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.constants.DBCClass;

public class RaceBuilder {
    private final int id;
    private final String name;

    private RaceSkill skill;
    private RaceStats stats   = new RaceStats();
    private RaceDisplay display = new RaceDisplay();

    private RaceBuilder(int id, String name) {
        this.id   = id;
        this.name = name;
    }

    public static RaceBuilder create(int id, String name) {
        return new RaceBuilder(id, name);
    }

    // ── Skill ────────────────────────────────────────────────
    public SkillBuilder skill() {
        return new SkillBuilder(this);
    }

    // ── Stats ─────────────────────────────────────────────────
    public ClassStatsBuilder forClass(DBCClass raceClass) {
        return new ClassStatsBuilder(this, raceClass);
    }

    // ── Build ─────────────────────────────────────────────────
    public Race build() {
        if (skill == null)
            throw new IllegalStateException("Race '" + name + "' is missing a racial skill.");
        return new Race(id, name, display, stats, skill);
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
        private final DBCClass raceClass;

        private int[] initialAttributes = RaceStats.ClassStats.DEFAULT_INITIAL_ATTRIBUTES.clone();
        private double[] attributeMultipliers = RaceStats.ClassStats.DEFAULT_ATTRIBUTE_MULTIPLIERS.clone();
        private double[] statBonuses = RaceStats.ClassStats.DEFAULT_STAT_BONUSES.clone();
        private double[] statAttributeMultipliers = RaceStats.ClassStats.DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS.clone();

        ClassStatsBuilder(RaceBuilder parent, DBCClass raceClass) {
            this.parent = parent;
            this.raceClass = raceClass;
        }

        public ClassStatsBuilder initialAttributes(int... values) {
            this.initialAttributes = values; return this;
        }
        public ClassStatsBuilder attributeMultipliers(double... values) {
            this.attributeMultipliers = values; return this;
        }
        public ClassStatsBuilder statBonuses(double... values) {
            this.statBonuses = values; return this;
        }
        public ClassStatsBuilder statAttributeMultipliers(double... values) {
            this.statAttributeMultipliers = values; return this;
        }

        public RaceBuilder and() {
            parent.stats.set(raceClass, new RaceStats.ClassStats(
                initialAttributes,
                attributeMultipliers,
                statBonuses,
                statAttributeMultipliers
            ));
            return parent;
        }

        public ClassStatsBuilder forClass(DBCClass next) {
            and();
            return new ClassStatsBuilder(parent, next);
        }
    }
}
