package kamkeel.npcdbc.mixin;

import kamkeel.npcdbc.data.DBCStats;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.data.SyncedData.CustomFormData;

public interface IPlayerFormData {
    PlayerCustomFormData getCustomFormData();
}
