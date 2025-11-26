package kamkeel.npcdbc.data.skill;

import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.api.skill.ICustomSkill;
import kamkeel.npcdbc.api.skill.ISkillContainer;
import kamkeel.npcdbc.controllers.SkillController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.NpcAPI;

public class SkillContainer implements ISkillContainer {

    private final DBCData data;
    public final IPlayer player;
    private ICustomSkill skill;
    private int level;

    public SkillContainer(IPlayer player, ICustomSkill skill, int level) {
        this(player);
        this.skill = skill;
        setLevel(level);
    }

    private SkillContainer(IPlayer player) {
        this.player = player;
        this.data = DBCData.getClient();
    }

    public static SkillContainer fromNBT(EntityPlayer player, NBTTagCompound comp) {
        SkillContainer container = new SkillContainer(null);
        container.skill = SkillController.Instance.getSkill(comp.getInteger("id"));
        container.setLevel(comp.getInteger("lvl"));
        return container;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setInteger("lvl", getLevel());
        nbtTagCompound.setInteger("id", getSkillID());

        return nbtTagCompound;
    }

    @Override
    public IPlayer getPlayer() {
        return this.player;
    }

    @Override
    public int getSkillID() {
        return skill.getId();
    }

    @Override
    public ICustomSkill getSkill() {
        return skill;
    }

    @Override
    public void setLevel(int level) {
        level = Math.min(Math.max(level, 1), skill.getMaxLevel());
        this.level = level;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public boolean tryToProgressLevel() {
        return tryToProgressLevel(false);
    }

    @Override
    public boolean tryToProgressLevel(boolean postEvent) {
        int currentLevel = getLevel();
        int newLevel = Math.min(skill.getMaxLevel(), getLevel() + 1);

        if (newLevel == currentLevel)
            return false;

        int tpCost = getSkill().getTPCost(newLevel);
        int mindCost = getSkill().getMindCost(newLevel);

        if (data.TP < tpCost || mindCost < data.getAvailableMind())
            return false;

        DBCPlayerEvent.SkillEvent.Upgrade event = new DBCPlayerEvent.SkillEvent.Upgrade(player, 2, getSkillID(), tpCost, newLevel);
        if (postEvent && DBCEventHooks.onSkillEvent(event))
            return false;

        level = newLevel;

        return true;
    }

    @Override
    public void unlearnSkill() {
        unlearnSkill(false);
    }

    @Override
    public void unlearnSkill(boolean postEvent) {
        DBCPlayerEvent.SkillEvent.Unlearn event = new DBCPlayerEvent.SkillEvent.Unlearn(player, 2, getSkillID());
        if (postEvent && DBCEventHooks.onSkillEvent(event))
            return;
        data.customSkills.remove(getSkillID());
    }
}
