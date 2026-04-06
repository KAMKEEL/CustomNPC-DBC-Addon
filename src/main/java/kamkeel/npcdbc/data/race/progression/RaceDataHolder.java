package kamkeel.npcdbc.data.race.progression;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

public abstract class RaceDataHolder implements DataSerializable {

    protected DBCData dbcData;

    public RaceDataHolder() {}

    public void attach(DBCData data) {
        this.dbcData = data;
    }

    protected DBCData getDBCData() {
        return dbcData;
    }
}
