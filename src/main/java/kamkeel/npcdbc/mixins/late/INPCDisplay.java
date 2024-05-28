package kamkeel.npcdbc.mixins.late;

import kamkeel.npcdbc.data.npc.DBCDisplay;

public interface INPCDisplay {
    DBCDisplay getDBCDisplay();

    boolean hasDBCData();
}
