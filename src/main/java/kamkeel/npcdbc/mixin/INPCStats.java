package kamkeel.npcdbc.mixin;

import kamkeel.npcdbc.data.npc.DBCStats;

public interface INPCStats {
    DBCStats getDBCStats();

    boolean hasDBCData();
}
