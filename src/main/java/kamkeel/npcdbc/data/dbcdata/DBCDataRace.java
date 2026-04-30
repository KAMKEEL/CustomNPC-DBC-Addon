package kamkeel.npcdbc.data.dbcdata;

import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.Race;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.race.progression.RaceDataHolder;
import kamkeel.npcdbc.data.race.properties.RacePropertyData;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DBCDataRace {

    public final DBCData data;

    public final Map<String, RaceDataHolder> customData = new LinkedHashMap<>();
    public final RacePropertyData properties = new RacePropertyData();

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
        if (race == null) {
            properties.clear();
            customData.clear();
            return;
        }

        properties.initDefaults(race);
        customData.clear();

        if (race.dataHolder == null) {
            return;
        }

        RaceDataHolder raceData = race.dataHolder.get();
        raceData.attach(data);
        customData.put(raceData.getKey(), raceData);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        DataCompound c = DataCompound.ofNbt(nbt);

        properties.serialize(c);

        DataCompound child = DataCompound.create();

        for (RaceDataHolder holder : customData.values()) {
            boolean correctRace = false;
            for (Race race : RaceController.getInstance().getRaces()) {
                if (race.id != this.getRaceID() || race.dataHolder == null) continue;
                if (race.dataHolder.get().getKey().equals(holder.getKey())) {
                    correctRace = true;
                    break;
                }
            }

            if (!correctRace) continue;
            child.put(holder.getKey(), holder.serialize(DataCompound.create()));
        }

        c.put("raceData", child);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        DataCompound c = DataCompound.ofNbt(nbt);

        // Schema must be populated before deserializing so the typed
        // property loop in RacePropertyData.deserialize has entries to iterate.
        properties.initDefaults(getRace());
        properties.deserialize(c);

        DataCompound child = c.get("raceData");

        for (Race race : RaceController.getInstance().getRaces()) {
            if (this.getRace().id != race.id) continue;
            if (race.dataHolder == null) continue;

            RaceDataHolder raceData = race.dataHolder.get();
            raceData.attach(data);

            if (child.has(raceData.getKey()))
                raceData.deserialize(child.get(raceData.getKey()));

            customData.put(raceData.getKey(), raceData);
        }
    }
}
