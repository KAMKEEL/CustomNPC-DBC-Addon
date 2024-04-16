package kamkeel.npcdbc.api;

import kamkeel.npcdbc.data.EnumAuraTypes;

public interface IDBCDisplay {
    boolean isEnabled();

    void setEnabled(boolean enabled);

    EnumAuraTypes getFormAuraTypes();

    void setFormAuraTypes(String type);
}
