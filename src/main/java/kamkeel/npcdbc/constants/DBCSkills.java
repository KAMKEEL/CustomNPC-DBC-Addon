package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.util.DBCUtils;

public enum DBCSkills {
    Fusion("FZ"),
    Jump("JP"),
    Dash("DS"),
    Fly("FL"),
    Endurance("EN"),
    PotentialUnlock("OC"),
    KiSense("KS"),
    Meditation("MD"),
    Kaioken("KK"),
    GodForm("GF"),
    OldKaiUnlock("OK"),
    KiProtection("KP"),
    KiFist("KF"),
    KiBoost("KB"),
    DefensePenetration("DF"),
    KiInfuse("KI"),
    UltraInstinct("UI"),
    InstantTransmission("IT"),
    GodOfDestruction("GD");

    private final String stringId;

    DBCSkills(String stringId) {
        this.stringId = stringId;
    }

    public int index() {
        return DBCUtils.getDBCSkillIndex(name());
    }

    public String id() {
        return stringId;
    }

    public int tpCost(int level) {
        return DBCUtils.calculateDBCSkillTPCost(index(), level);
    }

    public int tpCostRecursive(int level) {
        return DBCUtils.calculateDBCSkillTPCostRecursively(index(), level);
    }

    public int tpCost() {
        return tpCost(1);
    }

    public int tpCostRecursive() {
        return tpCostRecursive(1);
    }

    public int mindCost(int level) {
        return DBCUtils.calculateDBCSkillMindCost(index(), level);
    }

    public int mindCostRecursive(int level) {
        return DBCUtils.calculateDBCSkillMindCostRecursively(index(), level);
    }

    public int mindCost() {
        return mindCost(1);
    }

    public int mindCostRecursive() {
        return mindCostRecursive(1);
    }
}
