package kamkeel.npcdbc.data.race.stats;

import kamkeel.npcdbc.constants.enums.EnumDBCClasses;

public class RaceStats {
    private final ClassStats[] classes = new ClassStats[3];

    public void set(EnumDBCClasses raceClass, ClassStats stats) {
        classes[raceClass.ordinal()] = stats;
    }

    public ClassStats get(EnumDBCClasses raceClass) {
        return classes[raceClass.ordinal()];
    }
}
