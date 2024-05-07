package kamkeel.npcdbc.api.npc;

import kamkeel.npcdbc.constants.enums.EnumAuraTypes;

public interface IDBCDisplay {
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

    void setHairType(String type);

    String getHairType(String type);
}
