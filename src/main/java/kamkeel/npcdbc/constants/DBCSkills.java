package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.api.IDBCAddon;
import kamkeel.npcdbc.api.skill.ISkill;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.entity.player.EntityPlayer;
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
        level = Math.min(level, this.getMaxLevel());
        return this.getLevel(player) == level;
    }


    @Override
    public void teachPlayerSkill(IPlayer player, int level, boolean postEvent) {
        if (true) throw new RuntimeException("Not implemented yet");
        level = Math.min(level, this.getMaxLevel());

        final int currentLevel = this.getLevel(player);
        if (currentLevel >= level) return;

        if (currentLevel == 0 && postEvent) {
            throw new RuntimeException("Remember to post the skill");
        }
        setLevel(player, level);
    }


    @Override
    public void unlearnSkill(IPlayer player, boolean postEvent) {
        if (true) throw new RuntimeException("Not implemented yet");

        final int currentLevel = this.getLevel(player);
        if (currentLevel == 0) return;

        if (postEvent) {
            throw new RuntimeException("Remember to post the skill");
        }

        setLevel(player, 0);
    }

    @Override
    public boolean tryToProgressLevel(IPlayer player, boolean postEvent) {
        if (true) throw new RuntimeException("Not implemented yet");

        final int currentLevel = this.getLevel(player);
//        if (currentLevel == 0) return false;

        if (currentLevel == this.getMaxLevel()) return false;


//        IDBCAddon addon = (IDBCAddon) player.getDBCPlayer();
//        final int currentMind = addon.getAvailableMind();
//        final int currentTP = addon.getTP();
//
//        final int newMindCost = this.getMindCost(currentLevel+1);
//        final int newTPCost = this.getTPCost(currentLevel+1);
//
//        boolean canAfford = currentTP >= newTPCost && currentMind >= newMindCost;
//
//        if (!canAfford) return false;
//
//        if (postEvent)
//            throw new RuntimeException("Remember to post the skill event");


//        this.setLevel(player, currentLevel+1);

        return true;
    }

    @Override
    public int getLevel(IPlayer player) {
        DBCData data = DBCData.get((EntityPlayer) player.getMCEntity());
        return data.getSkillLevel(getId());
    }

    @Override
    public void setLevel(IPlayer player, int level) {
        level = Math.min(level, getMaxLevel());
        DBCData data = DBCData.get((EntityPlayer) player.getMCEntity());
        data.setSkillLevel(getId(), level);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound comp = new NBTTagCompound();
        comp.setString("name", name());
        comp.setInteger("id", ordinal());

        return comp;
    }

}
