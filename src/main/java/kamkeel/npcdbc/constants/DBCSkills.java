package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.api.skill.ISkill;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPlayer;

public enum DBCSkills implements ISkill {
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

    public static DBCSkills byIndex(int index) {
        for (DBCSkills skill : values())
            if (index == skill.getId())
                return skill;

        return null;
    }

    public static DBCSkills byId(String id) {
        for (DBCSkills skill : values())
            if (id.equals(skill.getStringId()))
                return skill;

        return null;
    }

    public static DBCSkills byName(String name) {
        for (DBCSkills skill : values())
            if (name.equals(skill.name()))
                return skill;

        return null;
    }

    @Override
    public int getId() {
        return DBCUtils.getDBCSkillIndex(name());
    }

    @Override
    public String getStringId() {
        return stringId;
    }

    @Override
    public int getTPCost(int level) {
        return DBCUtils.calculateDBCSkillTPCost(getId(), level);
    }

    @Override
    public int getTotalTPCost(int level) {
        return DBCUtils.calculateDBCSkillTPCostRecursively(getId(), level);
    }

    @Override
    public int getMindCost(int level) {
        return DBCUtils.calculateDBCSkillMindCost(getId(), level);
    }

    @Override
    public int getMaxLevel() {
        return DBCUtils.getMaxSkillLevel(getId());
    }

    @Override
    public int getTotalMindCost(int level) {
        return DBCUtils.calculateDBCSkillMindCostRecursively(getId(), level);
    }

    @Override
    public boolean doesPlayerHaveSkill(IPlayer player, int level) {
        if (true) throw new RuntimeException("Not implemented yet");
        return false;
    }


    @Override
    public void teachPlayerSkill(IPlayer player, int level, boolean postEvent) {
        if (true) throw new RuntimeException("Not implemented yet");
    }


    @Override
    public void unlearnSkill(IPlayer player, boolean postEvent) {
        if (true) throw new RuntimeException("Not implemented yet");
    }

    @Override
    public boolean tryToProgressLevel(IPlayer player, boolean postEvent) {
        if (true) throw new RuntimeException("Not implemented yet");
        return false;
    }

    @Override
    public int getLevel(IPlayer player) {
        if (true) throw new RuntimeException("Not implemented yet");
        return 0;
    }

    @Override
    public void setLevel(IPlayer player, int level) {
        if (true) throw new RuntimeException("Not implemented yet");
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound comp = new NBTTagCompound();
        comp.setString("name", name());
        comp.setInteger("id", ordinal());

        return comp;
    }

}
