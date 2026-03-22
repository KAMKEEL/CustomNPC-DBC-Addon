package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.constants.DBCClass;

public class RaceStats {
    private final ClassStats[] classes = new ClassStats[3];

    public void set(DBCClass raceClass, ClassStats stats) {
        classes[raceClass.ordinal()] = stats;
    }

    public ClassStats get(DBCClass raceClass) {
        return classes[raceClass.ordinal()];
    }

    public static class ClassStats {
        public static final int[] DEFAULT_INITIAL_ATTRIBUTES = {10, 10, 10, 10, 10, 10};
        public static final double[] DEFAULT_ATTRIBUTE_MULTIPLIERS = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        public static final double[] DEFAULT_STAT_BONUSES = {0, 0, 0, 30, 10, 10, 0, 10, 0, 0, 0, 0};
        public static final double[] DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS = {2.5, 4.0, 20.0, 3.5, 5.2, 40.0, 0.15, 1.0, 1.0, 1.0, 1.0, 1.0};

        // STR, DEX, CON, SPI, WIL, MND
        public final int[] initialAttributes; // 6
        public final double[] attributeMultipliers; // 6

        // melee, defense, body, stamina, energypower, energypool,
        // maxskills, speed, regenratebody, regenratestamina, regenrateenergy, flyspeed
        public final double[] statBonuses; // 12
        public final double[] statAttributeMultipliers; // 12

        public ClassStats(
            int[] initialAttributes,
            double[] attributeMultipliers,
            double[] statBonuses,
            double[] statAttributeMultipliers
        ) {
            this.initialAttributes = initialAttributes;
            this.attributeMultipliers = attributeMultipliers;
            this.statBonuses = statBonuses;
            this.statAttributeMultipliers = statAttributeMultipliers;
        }

        public static ClassStats defaults() {
            return new ClassStats(
                DEFAULT_INITIAL_ATTRIBUTES,
                DEFAULT_ATTRIBUTE_MULTIPLIERS,
                DEFAULT_STAT_BONUSES,
                DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS
            );
        }
    }
}
