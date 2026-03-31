package kamkeel.npcdbc.data.race.stats;

import kamkeel.npcdbc.constants.enums.EnumDBCClasses;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class RaceStats implements DataSerializable {
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

    public ClassStats get(int raceClass) {
        EnumDBCClasses dbcClass = EnumDBCClasses.fromOrdinal(raceClass);
        if (dbcClass == null) return null;
        return classes.get(dbcClass);
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

    @Override
    public DataCompound serialize(DataCompound data) {
        data.comment("Per-class stat overrides. Keys: MARTIAL_ARTIST, SPIRITUALIST, WARRIOR");
        for (Map.Entry<EnumDBCClasses, ClassStats> entry : classes.entrySet())
            data.put(entry.getKey(), entry.getValue());
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        for (EnumDBCClasses raceClass : EnumDBCClasses.values()) {
            if (!data.has(raceClass.name())) continue;
            ClassStats stats = classes.get(raceClass);
            if (stats == null) {
                stats = ClassStats.defaults();
                classes.put(raceClass, stats);
            }
            data.deserialize(raceClass, stats);
        }
    }
}
