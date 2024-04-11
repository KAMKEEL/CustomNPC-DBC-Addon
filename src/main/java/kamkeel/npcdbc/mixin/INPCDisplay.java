package kamkeel.npcdbc.mixin;

import kamkeel.npcdbc.data.DBCDisplay;

public interface INPCDisplay {
    DBCDisplay getDBCDisplay();

    boolean hasDBCData();
}
