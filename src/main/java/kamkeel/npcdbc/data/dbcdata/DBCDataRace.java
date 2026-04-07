package kamkeel.npcdbc.data.dbcdata;

import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.Race;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.race.progression.RaceDataHolder;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DBCDataRace {

    public final DBCData data;

    public final Map<String, RaceDataHolder> customData = new LinkedHashMap<>();

    public DBCDataRace(DBCData data) {
        this.data = data;
    }

    // ─── Core runtime state ──────────────────────────────────────────────

    public boolean isCustomRace() {
        return data.currentRaceKey != null && RaceController.getInstance().hasName(data.currentRaceKey);
    }

    public Race getRace() {
        return RaceController.getInstance().getByName(data.currentRaceKey);
    }

    public int getRaceID() {
        Race r = getRace();
        return r != null ? r.id : -1;
    }

    public int getRacialSkillLevel() {
        if (!isCustomRace())
            return 0;
        if (data.RacialSkills == null || data.RacialSkills.isEmpty() || data.RacialSkills.contains("pty"))
            return 0;
        return JRMCoreH.SklLvlX(data.Powertype, data.RacialSkills) - 1; //-1 is important
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

    public void writeCustomRaceData(Race race) {
        if (race == null || race.dataHolder == null) {
            customData.clear();
            return;
        }

        RaceDataHolder raceData = race.dataHolder.get();
        raceData.attach(data);
        customData.put(race.getName(), raceData);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        DataCompound c = DataCompound.ofNbt(nbt);
        DataCompound child = DataCompound.create();

        for (Map.Entry<String, RaceDataHolder> entry : customData.entrySet()) {
            child.put(entry.getKey(), entry.getValue().serialize(DataCompound.create()));
        }

        c.put("customRace", child);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        DataCompound c = DataCompound.ofNbt(nbt);
        DataCompound child = c.get("customRace");

        for (Race race : RaceController.getInstance().getRaces()) {
            if (race.dataHolder == null) continue;

            RaceDataHolder raceData = customData.get(race.getName());
            if (raceData == null) {
                raceData = race.dataHolder.get();
                raceData.attach(data);
            }

            if (child.has(race.getName()))
                raceData.deserialize(child.get(race.getName()));

            customData.put(race.getName(), raceData);
        }
    }
}
