package kamkeel.npcdbc.api.npc;

import kamkeel.npcdbc.constants.enums.EnumAuraTypes;

public interface IDBCDisplay {
    boolean isEnabled();

    void setEnabled(boolean enabled);

    EnumAuraTypes getFormAuraTypes();

    void setFormAuraTypes(String type);
}
