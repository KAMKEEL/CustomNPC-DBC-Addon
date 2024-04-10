package kamkeel.npcdbc.mixin;

import kamkeel.npcdbc.data.DBCDisplay;
import kamkeel.npcdbc.data.DBCStats;

public interface INPCDisplay {
    DBCDisplay getDBCDisplay();

    boolean hasDBCData();
}
