package kamkeel.npcdbc.api.npc;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;

public interface IDBCDisplay {
    void setColor(String type, int color);

    int getColor(String type);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    EnumAuraTypes2D getFormAuraTypes();

    void setFormAuraTypes(String type);

    String getHairCode();

    void setHairCode(String hairCode);


    byte getRace();

    void setRace(byte race);

    int setBodyType();

    void getBodyType(int bodyType);

    byte getTailState();

    void setTailState(byte tail);

    void setHairType(String type);

    boolean hasCoolerMask();

    void hasCoolerMask(boolean has);

    boolean hasEyebrows();

    void hasEyebrows(boolean has);

    String getHairType(String type);

    boolean hasAura();

    IAura getAura();

    void setAura(IAura aura);

    void setAura(int auraID);

    boolean isAuraToggled();

    void toggleAura(boolean toggle);

    boolean isInAura(IAura aura);
}
