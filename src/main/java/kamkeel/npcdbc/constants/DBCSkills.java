package kamkeel.npcdbc.constants;

import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import kamkeel.npcdbc.api.IDBCAddon;
import kamkeel.npcdbc.api.event.IDBCEvent;
import kamkeel.npcdbc.api.skill.ISkill;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
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
        switch (this) {

            case UltraInstinct:
                return JGConfigUltraInstinct.CONFIG_UI_LEVELS;
            case GodForm:
                return 3;
            case GodOfDestruction:
                return 1;
            default:
                return 10;
        }
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
        level = Math.min(level, this.getMaxLevel());

        if (level <= 0) {
            unlearnSkill(player, postEvent);
            return;
        }

        final int currentLevel = this.getLevel(player);

        if (currentLevel == 0) {
            if (postEvent && DBCEventHooks.onSkillEvent(
                new DBCPlayerEvent.SkillEvent.Learn(player, 1, getId(), 0))) {
                return;
            }
        }
        setLevel(player, level);
    }


    @Override
    public void unlearnSkill(IPlayer player, boolean postEvent) {
        final int currentLevel = this.getLevel(player);
        if (currentLevel == 0) return;

        DBCPlayerEvent.SkillEvent.Unlearn event = new DBCPlayerEvent.SkillEvent.Unlearn(player, 1, getId());
        if (postEvent && DBCEventHooks.onSkillEvent(event))
            return;

        setLevel(player, 0);
    }

    @Override
    public boolean tryToProgressLevel(IPlayer player, boolean postEvent) {
        final int currentLevel = this.getLevel(player);
        final int newLevel = Math.min(currentLevel+1, getMaxLevel());

        if (currentLevel == newLevel) return false;

        IDBCAddon addon = (IDBCAddon) player.getDBCPlayer();
        final int currentMind = addon.getAvailableMind();
        final int currentTP = addon.getTP();

        final int newMindCost = this.getMindCost(newLevel);
        int newTPCost = this.getTPCost(newLevel);

        if (currentMind < newMindCost) return false;

        DBCPlayerEvent.SkillEvent event = getProperEvent(this, player, newTPCost, newLevel);

        if (postEvent && DBCEventHooks.onSkillEvent(event)) {
            return true;
        }

        newTPCost = getTPFromEvent(event, newTPCost);
        if (currentTP < newTPCost)
            return false;

        addon.setTP(currentTP - newTPCost);
        this.setLevel(player, newLevel);

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

    private static int getTPFromEvent(DBCPlayerEvent.SkillEvent event, int fallback) {
        if (event instanceof IDBCEvent.SkillEvent.Learn)
            return ((IDBCEvent.SkillEvent.Learn) event).getCost();
        if (event instanceof IDBCEvent.SkillEvent.Upgrade)
            return ((IDBCEvent.SkillEvent.Upgrade) event).getCost();
        return fallback;
    }

    private static DBCPlayerEvent.SkillEvent getProperEvent(DBCSkills dbcSkills, IPlayer player, int cost, int level) {
        int skillID = dbcSkills.getId();

        if (level == 1)
            return new DBCPlayerEvent.SkillEvent.Learn(player, 1, skillID, cost);
        else
            return new DBCPlayerEvent.SkillEvent.Upgrade(player, 1, skillID, cost, level);
    }
}
