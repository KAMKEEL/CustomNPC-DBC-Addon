package kamkeel.npcdbc.constants;

public enum DBCScriptType {

    FORMCHANGE("formChange"),
    DAMAGED("damaged");

    public String function;

    private DBCScriptType(String function) {
        this.function = function;
    }

    public static DBCScriptType valueOfIgnoreCase(String channelName) {
        channelName = channelName.toUpperCase();
        return valueOf(channelName);
    }
}
