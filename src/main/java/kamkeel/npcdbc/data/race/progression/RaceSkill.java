package kamkeel.npcdbc.data.race.progression;

import kamkeel.npcs.controllers.data.ability.Ability;
import noppes.npcs.LogWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaceSkill {
    private int maxLevel;
    private int[] tpCosts;
    private int[] mindCosts;

    private final Map<Integer, List<Ability>> abilities = new HashMap<>();
    private final Map<Integer, List<Ability>> toggles = new HashMap<>();

    public RaceSkill(int maxLevel, int[] tpCosts, int[] mindCosts) {
        this.maxLevel = Math.min(Math.max(maxLevel, 1), 10);
        this.tpCosts = normalizeCosts(tpCosts, this.maxLevel);
        this.mindCosts = normalizeCosts(mindCosts, this.maxLevel);
    }

    private int[] normalizeCosts(int[] source, int size) {
        int[] result = new int[size];
        if (source != null && source.length > 0) {
            for (int i = 0; i < size; i++) {
                int j = Math.min(i, source.length - 1);
                result[i] = source[j];
            }
        }
        return result;
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

    public int getMaxLevel() { return maxLevel; }

    public int getTPCost(int level) {
        level = Math.min(Math.max(1, level), maxLevel);
        return tpCosts[level - 1];
    }

    public int getMindCost(int level) {
        level = Math.min(Math.max(1, level), maxLevel);
        return mindCosts[level - 1];
    }

    public int getTotalTPCost(int level) {
        int cap = Math.min(Math.max(level, 1), maxLevel);
        int sum = 0;
        for (int i = 1; i <= cap; i++) sum += getTPCost(i);
        return sum;
    }

    public int getTotalMindCost(int level) {
        int cap = Math.min(Math.max(level, 1), maxLevel);
        int sum = 0;
        for (int i = 1; i <= cap; i++) sum += getMindCost(i);
        return sum;
    }
}
