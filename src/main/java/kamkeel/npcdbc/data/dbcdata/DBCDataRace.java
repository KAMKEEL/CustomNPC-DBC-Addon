package kamkeel.npcdbc.data.dbcdata;

import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.skill.RacialSkillContainer;
import net.minecraft.nbt.NBTTagCompound;

public class DBCDataRace {

    private final DBCData data;

    public DBCDataRace(DBCData data) {
        this.data = data;
    }

    public boolean isCustomRace() {
        return data.addonRaceID > -1 && RaceController.getInstance().has(data.addonRaceID);
    }

    public Race getRace() {
        return RaceController.getInstance().get(data.addonRaceID);
    }

    public int getRaceID() {
        return data.addonRaceID;
    }

    public void setRaceID(int raceID) {
        this.data.addonRaceID = raceID;
        if (isCustomRace())
            data.racialSkill = RacialSkillContainer.fromRace(data, raceID);
        else
            data.racialSkill = null;

        data.saveNBTData(true);
    }

    public RacialSkillContainer getRacialSkill() {
        return data.racialSkill;
    }
}
