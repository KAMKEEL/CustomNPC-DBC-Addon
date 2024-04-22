package kamkeel.npcdbc.mixin;

import kamkeel.npcdbc.data.PlayerFormData;

public interface IPlayerFormData {
    PlayerFormData getPlayerFormData();

    boolean getFormUpdate();

    void updateFormInfo();

    void finishFormInfo();
}
