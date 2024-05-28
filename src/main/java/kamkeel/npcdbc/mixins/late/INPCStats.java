package kamkeel.npcdbc.mixins.late;

import kamkeel.npcdbc.data.npc.DBCStats;

public interface INPCStats {
    DBCStats getDBCStats();

    boolean hasDBCData();
}
