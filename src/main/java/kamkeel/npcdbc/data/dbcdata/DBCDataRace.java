package kamkeel.npcdbc.data.dbcdata;

import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.progression.FormTree.Branch;
import kamkeel.npcdbc.data.race.progression.RaceSkill;

import JinRyuu.JRMCore.JRMCoreH;

import java.util.Collections;
import java.util.List;

public class DBCDataRace {

    public final DBCData data;

    public DBCDataRace(DBCData data) {
        this.data = data;
    }

    // ─── Core runtime state ──────────────────────────────────────────────

    public boolean isCustomRace() {
        return data.addonRaceID > -1 && RaceController.getInstance().has(data.addonRaceID);
    }

    public Race getRace() {
        return RaceController.getInstance().get(data.addonRaceID);
    }

    public int getRaceID() {
        return data.addonRaceID;
    }

    public void assignRace(int raceID, PlayerDBCInfo info) {
        if (raceID != -1 && !RaceController.getInstance().has(raceID))
            return;

        info.currentRace = raceID;
        info.selectedBranchIndex = 0;
        data.addonRaceID = raceID;

        data.saveNBTData(true);
    }

    public int getRacialSkillLevel() {
        if (!isCustomRace())
            return 0;
        if (data.RacialSkills == null || data.RacialSkills.isEmpty() || data.RacialSkills.contains("pty"))
            return 0;
        return JRMCoreH.SklLvlX(data.Powertype, data.RacialSkills);
    }

    // ─── Form queries ────────────────────────────────────────────────────

    public List<Form> getUnlockedForms() {
        Race race = getRace();
        if (race == null)
            return Collections.emptyList();

        return race.skill.getUnlockedForms(getRacialSkillLevel());
    }

    public boolean hasForm(String formKey) {
        if (formKey == null || !isCustomRace())
            return false;

        for (Form form : getUnlockedForms()) {
            if (form != null && form.hasKey() && form.getKeyString().equals(formKey))
                return true;
        }
        return false;
    }

    public Form getForm(String formKey) {
        if (formKey == null)
            return null;

        for (Form form : getUnlockedForms()) {
            if (form != null && form.hasKey() && form.getKeyString().equals(formKey))
                return form;
        }
        return null;
    }
    
    
}
