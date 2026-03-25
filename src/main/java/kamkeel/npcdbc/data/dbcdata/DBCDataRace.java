package kamkeel.npcdbc.data.dbcdata;

import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.progression.RaceSkill;

import java.util.HashSet;
import java.util.List;

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

    public int getRacialSkillLevel() {
        if (!isCustomRace())
            return 0;
        if (data.RacialSkills == null || data.RacialSkills.isEmpty() || data.RacialSkills.contains("pty"))
            return 0;
        return getRawRacialSkillLevel();
    }

    public int getRawRacialSkillLevel() {
        if (!isCustomRace())
            return 0;
        if (data.RacialSkills == null || data.RacialSkills.isEmpty() || data.RacialSkills.contains("pty") || data.RacialSkills.length() < 3)
            return 0;
        try {
            return Integer.parseInt(data.RacialSkills.substring(2));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
