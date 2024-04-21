package kamkeel.npcdbc.mixin;

import kamkeel.npcdbc.data.PlayerCustomFormData;

public interface IPlayerFormData {
    PlayerCustomFormData getCustomFormData();

    boolean getFormUpdate();

    void updateFormInfo();

    void finishFormInfo();
}
