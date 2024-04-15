package kamkeel.npcdbc.constants;

import noppes.npcs.constants.EnumScriptType;

public enum DBCScriptType {

    FORMCHANGE("formChange");

    public String function;

    public static DBCScriptType valueOfIgnoreCase(String channelName) {
        channelName = channelName.toUpperCase();
        return valueOf(channelName);
    }

    private DBCScriptType(String function) {
        this.function = function;
    }
}
