package kamkeel.npcdbc.data.race.progression;

import kamkeel.npcs.controllers.data.ability.Ability;
import noppes.npcs.LogWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class RaceSkill {
    public static final class LevelEntry {
        private final int level;
        private final int formId;
        private final int tpCost;
        private final int mindCost;

        public LevelEntry(int level, int formId, int tpCost, int mindCost) {
            if (level < 1 || level > 10)
                throw new IllegalArgumentException("Level " + level + " out of range [1..10]");
            if (formId < 0)
                throw new IllegalArgumentException("Form ID must be non-negative, got " + formId);
            if (tpCost < 0)
                throw new IllegalArgumentException("TP cost must be non-negative, got " + tpCost);
            if (mindCost < 0)
                throw new IllegalArgumentException("Mind cost must be non-negative, got " + mindCost);

            this.level = level;
            this.formId = formId;
            this.tpCost = tpCost;
            this.mindCost = mindCost;
        }

        public int level() {
            return level;
        }

        public int formId() {
            return formId;
        }

        public int tpCost() {
            return tpCost;
        }

        public int mindCost() {
            return mindCost;
        }
    }

    private final int maxLevel;
    private final LinkedHashMap<Integer, LevelEntry> levelEntries;
    private String displayName = "SuperForm";
    private String description = "SuperForm";

    private final Map<Integer, List<Ability>> abilities = new HashMap<>();
    private final Map<Integer, List<Ability>> toggles = new HashMap<>();

    public RaceSkill(int maxLevel) {
        this(maxLevel, new LinkedHashMap<Integer, LevelEntry>());
    }

    public RaceSkill(int maxLevel, LinkedHashMap<Integer, LevelEntry> levelEntries) {
        this.maxLevel = Math.min(Math.max(maxLevel, 1), 10);
        this.levelEntries = new LinkedHashMap<Integer, LevelEntry>();

        if (levelEntries != null) {
            for (Map.Entry<Integer, LevelEntry> entry : levelEntries.entrySet()) {
                addLevelEntry(entry.getValue());
            }
        }
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Returns the display name for this racial skill shown in the GUI.
     * Defaults to "SuperForm" to match vanilla Saiyan convention.
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        String normalized = displayName != null ? displayName : "SuperForm";
        if (description == null || description.equals(this.displayName)) {
            this.description = normalized;
        }
        this.displayName = normalized;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null && !description.isEmpty() ? description : this.displayName;
    }

    public void addLevelEntry(LevelEntry entry) {
        if (entry == null)
            throw new IllegalArgumentException("Level entry must not be null");
        if (entry.level() > maxLevel)
            throw new IllegalArgumentException("Level " + entry.level() + " out of range [1.." + maxLevel + "]");
        if (levelEntries.containsKey(entry.level()))
            throw new IllegalArgumentException("Duplicate racial skill entry for level " + entry.level());
        levelEntries.put(entry.level(), entry);
    }

    public void addAbility(int level, Ability ability) {
        if (!ability.isBuiltIn()) {
            LogWriter.error("RaceSkill: ability '" + ability.getName() + "' is not built-in and cannot be registered.");
            return;
        }
        if (ability.isToggleable()) {
            LogWriter.error("RaceSkill: ability '" + ability.getName() + "' is toggleable, use addToggle() instead.");
            return;
        }
        abilities.computeIfAbsent(level, k -> new ArrayList<>()).add(ability);
    }

    public void addToggle(int level, Ability ability) {
        if (!ability.isBuiltIn()) {
            LogWriter.error("RaceSkill: toggle '" + ability.getName() + "' is not built-in and cannot be registered.");
            return;
        }
        if (!ability.isToggleable()) {
            LogWriter.error("RaceSkill: ability '" + ability.getName() + "' is not toggleable, use addAbility() instead.");
            return;
        }
        toggles.computeIfAbsent(level, k -> new ArrayList<>()).add(ability);
    }

    public List<Ability> getAbilitiesAtLevel(int level) {
        return Collections.unmodifiableList(abilities.getOrDefault(level, Collections.emptyList()));
    }

    public List<Ability> getTogglesAtLevel(int level) {
        return Collections.unmodifiableList(toggles.getOrDefault(level, Collections.emptyList()));
    }

    public Map<Integer, List<Ability>> getAbilities() {
        return Collections.unmodifiableMap(abilities);
    }

    public Map<Integer, List<Ability>> getToggles() {
        return Collections.unmodifiableMap(toggles);
    }

    public void addLevelEntry(int level, int formId, int tpCost, int mindCost) {
        addLevelEntry(new LevelEntry(level, formId, tpCost, mindCost));
    }

    public LevelEntry getEntry(int level) {
        return levelEntries.get(level);
    }

    public int getTPCost(int level) {
        LevelEntry entry = levelEntries.get(level);
        return entry != null ? entry.tpCost() : -1;
    }

    public int getMindCost(int level) {
        LevelEntry entry = levelEntries.get(level);
        return entry != null ? entry.mindCost() : -1;
    }

    public int getTotalTPCost(int level) {
        int total = 0;
        for (LevelEntry entry : levelEntries.values()) {
            if (entry.level() <= level)
                total += entry.tpCost();
        }
        return total;
    }

    public int getTotalMindCost(int level) {
        int total = 0;
        for (LevelEntry entry : levelEntries.values()) {
            if (entry.level() <= level)
                total += entry.mindCost();
        }
        return total;
    }

    public List<Integer> getFormIdsAtLevel(int level) {
        LevelEntry entry = levelEntries.get(level);
        if (entry == null)
            return Collections.emptyList();
        return Collections.singletonList(entry.formId());
    }

    public List<Integer> getUnlockedFormIds(int currentLevel) {
        List<Integer> result = new ArrayList<Integer>();
        for (LevelEntry entry : levelEntries.values()) {
            if (entry.level() <= currentLevel)
                result.add(entry.formId());
        }
        return Collections.unmodifiableList(result);
    }

    public List<Integer> getAllFormIds() {
        List<Integer> result = new ArrayList<Integer>();
        for (LevelEntry entry : levelEntries.values()) {
            result.add(entry.formId());
        }
        return Collections.unmodifiableList(result);
    }

    public Map<Integer, LevelEntry> getLevelEntries() {
        return Collections.unmodifiableMap(levelEntries);
    }

    public boolean hasFormBindings() {
        return !levelEntries.isEmpty();
    }
}
