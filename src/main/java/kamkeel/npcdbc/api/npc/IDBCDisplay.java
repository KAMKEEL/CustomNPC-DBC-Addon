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


    int getRace();

    void setRace(int race);

    int setBodyType();

    void getBodyType(int bodyType);

    void setHairType(String type);

    boolean hasCoolerMask();

    void hasCoolerMask(boolean has);

    String getHairType(String type);
}
