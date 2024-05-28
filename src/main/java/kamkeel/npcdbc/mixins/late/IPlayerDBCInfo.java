package kamkeel.npcdbc.mixins.late;

import kamkeel.npcdbc.data.PlayerDBCInfo;

public interface IPlayerDBCInfo {
    PlayerDBCInfo getPlayerDBCInfo();

    boolean getDBCInfoUpdate();

    void updateDBCInfo();

    void endDBCInfo();
}
