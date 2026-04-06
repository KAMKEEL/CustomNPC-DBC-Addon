package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.controllers.sync.DBCSyncType;
import kamkeel.npcdbc.controllers.sync.handlers.RaceSyncHandler;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.helper.RaceSelectorHelper;
import kamkeel.npcdbc.data.race.serial.ConfigManager;
import kamkeel.npcs.controllers.SyncController;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.LogWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaceController {
    public static RaceController Instance = new RaceController();

    public final Map<String, Race> races = new HashMap<>();
    public final List<Race> raceOrder = new ArrayList<>();

    public final ConfigManager<Race> configManager = new ConfigManager<>(() -> CustomNpcPlusDBC.addonConfig, "races", Race::getName);

    public RaceController() {
        Instance = this;
    }

    public void load() {
        LogWriter.info("Loading custom races...");
        registerAddonRaces();
        loadConfigs();
        LogWriter.info("Done loading custom races. Registered " + races.size() + " race(s).");
    }

    /**
     * Load (or generate) server-side config files for all registered races.
     * Must be called after the world save directory is available (FMLServerAboutToStartEvent).
     */
    public void loadConfigs() {
        LogWriter.info("Loading race configs...");
        for (Race race : raceOrder) {
            configManager.loadOrCreate(race);
        }
        LogWriter.info("Done loading race configs.");
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
     * Name must be unique. Order of registration defines GUI index (starting at 6).
     */
    public void register(Race race) {
        if (race == null) {
            LogWriter.error("Attempted to register a null race.");
            return;
        }
        if (races.containsKey(race.getName())) {
            LogWriter.error("Race with name '" + race.getName() + "' is already registered. Skipping.");
            return;
        }
        races.put(race.getName(), race);
        raceOrder.add(race);
        RaceSelectorHelper.markDirty();
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
    public int getIndex(String name) {
        for (int i = 0; i < raceOrder.size(); i++)
            if (raceOrder.get(i).getName().equalsIgnoreCase(name))
                return i;
        return -1;
    }

    /** Legacy: get GUI index by int ID. Linear scan — only used for legacy migration paths. */
    public int getIndex(int raceID) {
        for (int i = 0; i < raceOrder.size(); i++)
            if (raceOrder.get(i).id == raceID)
                return i;
        return -1;
    }

    public Race getByName(String name) {
        return races.get(name);
    }

    /** Legacy: look up by int ID. Linear scan — only used for NBT legacy migration. */
    public Race get(int id) {
        for (Race race : raceOrder)
            if (race.id == id)
                return race;
        return null;
    }

    public boolean hasName(String name) {
        return races.containsKey(name);
    }

    /** Legacy: check by int ID. Linear scan — only used for legacy migration paths. */
    public boolean has(int id) {
        return get(id) != null;
    }

    public Collection<Race> getRaces() {
        return races.values();
    }

    public List<Race> getRaceOrder() {
        return raceOrder;
    }

    public List<String> getRaceNames() {
        List<String> list = new ArrayList<>();
        for (Race race : getRaceOrder()) {
            list.add(race.getName());
        }

        return list;
    }

    /** Legacy: check if an int ID maps to a custom race. Linear scan. */
    public boolean isCustomRace(int raceID) {
        return get(raceID) != null;
    }

    public int getCustomRaceCount() {
        return raceOrder.size();
    }

    public Race save(Race race) {
        if (race == null)
            return null;

        Race existing = get(race.id);
        if (existing != null) {
            races.remove(existing.getName());
            raceOrder.remove(existing);
        }

        races.put(race.getName(), race);
        if (!raceOrder.contains(race))
            raceOrder.add(race);

        configManager.loadOrCreate(race);

        NBTTagCompound nbt = race.writeToNBT();
        SyncController.syncUpdate(DBCSyncType.RACE, nbt);

        return race;
    }

    public void delete(String name) {
        Race removed = races.remove(name);
        if (removed == null) return;

        raceOrder.remove(removed);
        SyncController.syncRemove(DBCSyncType.RACE, name);
    }

    public void deleteRaceFile(Race race) {
        configManager.deleteConfig(race.getName());
    }

    /**
     * Client-side: atomically replace all race data after RELOAD.
     * Called by {@link RaceSyncHandler}.
     */
    public void setRaceData(Map<String, Race> newRaces, List<Race> newOrder) {
        races.clear();
        races.putAll(newRaces);
        raceOrder.clear();
        raceOrder.addAll(newOrder);
        RaceSelectorHelper.markDirty();
    }

    /**
     * Client-side: insert or replace a single race after UPDATE.
     * Called by {@link RaceSyncHandler}.
     */
    public void put(Race race) {
        Race existing = getByName(race.getName());
        if (existing != null) raceOrder.remove(existing);
        races.put(race.getName(), race);
        raceOrder.add(race);
        RaceSelectorHelper.markDirty();
    }

    /**
     * Client-side: remove a race by name after REMOVE.
     * Called by {@link RaceSyncHandler}.
     */
    public void remove(String name) {
        Race removed = races.remove(name);
        if (removed != null) raceOrder.remove(removed);
        RaceSelectorHelper.markDirty();
    }

    public static RaceController getInstance() {
        return Instance;
    }
}
