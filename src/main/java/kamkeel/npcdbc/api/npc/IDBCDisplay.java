package kamkeel.npcdbc.api.npc;

import kamkeel.npcdbc.constants.enums.EnumAuraTypes;

public interface IDBCDisplay {
    void setColor(String type, int color);

    int getColor(String type);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    EnumAuraTypes getFormAuraTypes();

    void setFormAuraTypes(String type);

    String getHairCode();

    void setHairCode(String hairCode);

    int getHairColor();

    void setEyeColor(int eyeColor);

    int getEyeColor();

    void setHairColor(int hairColor);

    int getRace();

    void setRace(int race);

    void setHairType(String type);

    String getHairType(String type);
}
