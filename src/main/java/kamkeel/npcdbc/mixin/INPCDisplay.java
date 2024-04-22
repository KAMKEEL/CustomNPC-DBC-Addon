package kamkeel.npcdbc.mixin;

import kamkeel.npcdbc.data.npc.DBCDisplay;

public interface INPCDisplay {
    DBCDisplay getDBCDisplay();

    boolean hasDBCData();
}
