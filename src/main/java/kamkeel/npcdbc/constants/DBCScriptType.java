package kamkeel.npcdbc.constants;

public enum DBCScriptType {

    FORMCHANGE("dbcFormChange"),
    DAMAGED("dbcDamaged"),
    CAPSULEUSED("dbcCapsuleUsed"),
    SENZUUSED("dbcSenzuUsed"),
    REVIVED("dbcRevived"),
    KNOCKOUT("dbcKnockout"),
    SKILL_EVENT("dbcSkillSlotEvent");

    public String function;

    private DBCScriptType(String function) {
        this.function = function;
    }

    public static DBCScriptType valueOfIgnoreCase(String channelName) {
        channelName = channelName.toUpperCase();
        return valueOf(channelName);
    }

    public enum Form {
        ASCEND("onAscend"),
        DESCEND("onDescend"),
        TICK("onTick");

        public String function;

        Form(String function) {
            this.function = function;
        }
    }
}
