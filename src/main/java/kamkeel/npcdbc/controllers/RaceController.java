package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.data.race.Race;
import noppes.npcs.LogWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaceController {
    public static RaceController Instance = new RaceController();

    private final Map<Integer, Race> races = new HashMap<>();
    private final List<Race> raceOrder = new ArrayList<>();

    public RaceController() {
        Instance = this;
    }

    public void load() {
        races.clear();
        raceOrder.clear();
        LogWriter.info("Loading custom races...");
        registerAddonRaces();
        LogWriter.info("Done loading custom races. Registered " + races.size() + " race(s).");
    }

    /**
     * Register all races provided by this addon.
     * Add new races here.
     */
    private void registerAddonRaces() {
        // races go here
    }

    /**
     * Register a race explicitly.
     * ID must be unique and explicitly set in the Race object.
     * Order of registration defines GUI index (starting at 6).
     */
    public void register(Race race) {
        if (race == null) {
            LogWriter.error("Attempted to register a null race.");
            return;
        }
        if (races.containsKey(race.id)) {
            LogWriter.error("Race with ID " + race.id + " is already registered. Skipping: " + race.getName());
            return;
        }
        races.put(race.id, race);
        raceOrder.add(race);
        LogWriter.info("Registered race: " + race.getName() + " (ID: " + race.id + ") at GUI index: " + (5 + raceOrder.size()));
    }

    /**
     * Get race by its GUI index.
     * Native DBC races occupy indices 0-5.
     * Custom races start at index 6 (customIndex 0).
     */
    public Race getByIndex(int customIndex) {
        if (customIndex < 0 || customIndex >= raceOrder.size())
            return null;
        return raceOrder.get(customIndex);
    }

    /**
     * Get the GUI index of a race (0-based from the start of custom races).
     * Add 6 to get the absolute GUI index.
     */
    public int getIndex(int raceID) {
        for (int i = 0; i < raceOrder.size(); i++)
            if (raceOrder.get(i).id == raceID)
                return i;
        return -1;
    }

    public Race get(int id) {
        return races.get(id);
    }

    public Race getByName(String name) {
        for (Race race : races.values())
            if (race.getName().equalsIgnoreCase(name))
                return race;
        return null;
    }

    public boolean has(int id) {
        return races.containsKey(id);
    }

    public boolean hasName(String name) {
        return getByName(name) != null;
    }

    public Collection<Race> getRaces() {
        return races.values();
    }

    public List<Race> getRaceOrder() {
        return raceOrder;
    }

    public boolean isCustomRace(int raceID) {
        return races.containsKey(raceID);
    }

    public int getCustomRaceCount() {
        return raceOrder.size();
    }

    public static RaceController getInstance() {
        return Instance;
    }
}
