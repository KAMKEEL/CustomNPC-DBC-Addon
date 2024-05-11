package kamkeel.npcdbc.constants;

public enum DBCScriptType {

    FORMCHANGE("dbcFormChange"),
    DAMAGED("dbcDamaged"),
    CAPSULEUSED("capsuleUsed"),
    REVIVED("revived");

    public String function;

    private DBCScriptType(String function) {
        this.function = function;
    }

    public static DBCScriptType valueOfIgnoreCase(String channelName) {
        channelName = channelName.toUpperCase();
        return valueOf(channelName);
    }
}
