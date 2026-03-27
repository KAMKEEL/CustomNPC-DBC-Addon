package kamkeel.npcdbc.data.race.stats;

import kamkeel.npcdbc.constants.enums.EnumDBCClasses;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class RaceStats {
    private final Map<EnumDBCClasses, ClassStats> classes = new EnumMap<>(EnumDBCClasses.class);

    public void set(EnumDBCClasses raceClass, ClassStats stats) {
        classes.put(raceClass, stats);
    }

    public boolean has(EnumDBCClasses raceClass) {
        return classes.containsKey(raceClass);
    }

    public ClassStats get(EnumDBCClasses raceClass) {
        return classes.get(raceClass);
    }

    public ClassStats getOrDefault(EnumDBCClasses raceClass) {
        ClassStats stats = classes.get(raceClass);
        return stats != null ? stats : ClassStats.defaults();
    }

    public Collection<ClassStats> all() {
        return classes.values();
    }

    public Map<EnumDBCClasses, ClassStats> entries() {
        return classes;
    }
}
