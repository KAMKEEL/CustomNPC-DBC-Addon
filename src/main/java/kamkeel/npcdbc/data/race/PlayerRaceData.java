package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.skill.RacialSkillContainer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerRaceData {

    private final DBCData data;
    private int raceID = -1;
    private RacialSkillContainer racialSkill;

    public PlayerRaceData(DBCData data) {
        this.data = data;
    }

    public boolean isCustomRace() {
        return raceID > -1 && RaceController.getInstance().has(raceID);
    }

    public Race getRace() {
        return RaceController.getInstance().get(raceID);
    }

    public int getRaceID() {
        return raceID;
    }

    public void setRaceID(int raceID) {
        this.raceID = raceID;
        if (isCustomRace())
            racialSkill = RacialSkillContainer.fromRace(data, raceID);
        else
            racialSkill = null;
        if (data != null)
            data.saveNBTData(true);
    }

    public RacialSkillContainer getRacialSkill() {
        return racialSkill;
    }

    public void readFromNBT(NBTTagCompound tag) {
        raceID = tag.getInteger("raceID");
        if (isCustomRace())
            racialSkill = RacialSkillContainer.fromRace(data, raceID);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("raceID", raceID);
        return tag;
    }
}
