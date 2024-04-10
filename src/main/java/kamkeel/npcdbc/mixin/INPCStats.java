package kamkeel.npcdbc.mixin;

import kamkeel.npcdbc.data.DBCStats;

public interface INPCStats {
    DBCStats getDBCStats();

    boolean hasDBCData();
}
