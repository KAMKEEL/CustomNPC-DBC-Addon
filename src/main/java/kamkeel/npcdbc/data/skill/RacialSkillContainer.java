package kamkeel.npcdbc.data.skill;

import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import net.minecraft.nbt.NBTTagCompound;

public class RacialSkillContainer {

    private final DBCData data;
    private final RacialSkill skill;

    public RacialSkillContainer(DBCData data, RacialSkill skill) {
        this.data = data;
        this.skill = skill;
    }

    public static RacialSkillContainer fromRace(DBCData data, int raceID) {
        Race race = RaceController.getInstance().get(raceID);
        if (race == null)
            return null;
        return new RacialSkillContainer(data, race.getRacialSkill());
    }

    // -------------------------
    // Skill info — read only
    // -------------------------

    public int getTPCost(int level) {
        return skill.getTPCost(level);
    }

    public int getMindCost(int level) {
        return skill.getMindCost(level);
    }

    public int getTotalTPCost(int level) {
        return skill.getTotalTPCost(level);
    }

    public int getTotalMindCost(int level) {
        return skill.getTotalMindCost(level);
    }

    public int getMaxLevel() {
        return skill.getMaxLevel();
    }

    public int[] getTPCosts() {
        return skill.getTPCosts();
    }

    public int[] getMindCosts() {
        return skill.getMindCosts();
    }

    public String getDisplayName() {
        return skill.getDisplayName();
    }

    public String getDescription() {
        return skill.getDescription();
    }

    /**
     * Current level is read directly from jrmcSSltX via DBCData.
     * The DBC native system owns the level — we only read it.
     */
    public int getCurrentLevel() {
        return JinRyuu.JRMCore.JRMCoreH.SklLvlX(data.Powertype, data.RacialSkills);
    }

    public RacialSkill getSkill() {
        return skill;
    }
}
