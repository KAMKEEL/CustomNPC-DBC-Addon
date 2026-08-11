package kamkeel.npcdbc.data.race.progression;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;
import kamkeel.npcs.controllers.data.ability.Ability;
import noppes.npcs.LogWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RaceSkill implements DataSerializable {
    public static final class LevelEntry implements DataSerializable {
        private final int level;
        private Form form;
        private int tpCost;
        private int mindCost;

        public LevelEntry(int level, Form form, int tpCost, int mindCost) {
            this.level = level;
            this.form = form;
            this.tpCost = tpCost;
            this.mindCost = mindCost;
        }

        public int level()    { return level; }
        public Form form()    { return form; }
        public int formId()   { return form.id; }
        public int tpCost()   { return tpCost; }
        public int mindCost() { return mindCost; }

        @Override
        public DataCompound serialize(DataCompound data) {
            if (form != null && form.key != null)
                data.putString("formKey", form.key.toString());
            data.putInt("tpCost", tpCost);
            data.putInt("mindCost", mindCost);
            return data;
        }

        @Override
        public void deserialize(DataCompound data) {
            if (data.has("formKey")) {
                String formKey = data.getString("formKey", null);
                form = FormController.getInstance().getFromKey(formKey);
            }
            tpCost   = data.getInt("tpCost",   tpCost);
            mindCost = data.getInt("mindCost",  mindCost);
        }
    }

    private final LinkedHashMap<Integer, LevelEntry> levelEntries;
    private int maxLevel;
    private String displayName = "SuperForm";
    private String description = "SuperForm";

    private final Map<Integer, List<Ability>> abilities = new HashMap<>();
    private final Map<Integer, List<Ability>> toggles = new HashMap<>();

    public RaceSkill(int maxLevel) {
        this(maxLevel, new LinkedHashMap<>());
    }

    public RaceSkill(int maxLevel, LinkedHashMap<Integer, LevelEntry> levelEntries) {
        this.maxLevel = Math.min(Math.max(maxLevel, 1), 10);
        this.levelEntries = new LinkedHashMap<>();

        if (levelEntries != null) {
            for (Map.Entry<Integer, LevelEntry> entry : levelEntries.entrySet()) {
                addLevelEntry(entry.getValue());
            }
        }
    }

    public int getMaxLevel() {
        return maxLevel;
    }

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
    
    public List<Form> getUnlockedForms(int currentLevel) {
        List<Form> result = new ArrayList<>();
        for (LevelEntry entry : levelEntries.values()) {
            if (entry.level() <= currentLevel)
                result.add(entry.form());
        }
        return Collections.unmodifiableList(result);
    }

    public List<Form> getAllForms() {
        List<Form> result = new ArrayList<>();
        for (LevelEntry entry : levelEntries.values()) {
            result.add(entry.form());
        }
        return Collections.unmodifiableList(result);
    }

    public Map<Integer, LevelEntry> getLevelEntries() {
        return Collections.unmodifiableMap(levelEntries);
    }

    public boolean hasFormBindings() {
        return !levelEntries.isEmpty();
    }

    public int getBranchUnlockLevel(FormTree.Branch branch) {
        Form unlockAnchor = branch.getUnlockAnchor();
        if (unlockAnchor != null) {
            for (LevelEntry entry : levelEntries.values()) {
                if (entry.form() == unlockAnchor) {
                    return entry.level();
                }
            }
        }
        return 0;
    }

    // ─── Branch-aware progression queries ────────────────────────────────
    // These operate on pure definition data (FormTree + skill level) and do
    // not depend on runtime player state. DBCDataRace delegates here.

    /**
     * Returns branches whose {@link FormTree.Branch#getUnlockLevel()} is at most
     * {@code skillLevel}. Branch unlock is determined at build time from the
     * lowest skill-level binding for any form in the branch — it is NOT inferred
     * from "any unlocked form happens to be in the branch."
     */
    public List<FormTree.Branch> getUnlockedBranches(FormTree tree, int skillLevel) {
        if (tree == null)
            return Collections.emptyList();

        List<FormTree.Branch> result = new ArrayList<>();
        for (FormTree.Branch branch : tree.getBranches()) {
            if (branch.getUnlockLevel() <= skillLevel)
                result.add(branch);
        }
        return result;
    }

    /**
     * Resolves the active branch for the given selection index, falling back to
     * the first unlocked branch if the stored index is invalid or locked.
     */
    public FormTree.Branch resolveActiveBranch(FormTree tree, int skillLevel, int selectedBranchIndex) {
        if (tree == null)
            return null;

        List<FormTree.Branch> unlocked = getUnlockedBranches(tree, skillLevel);
        if (unlocked.isEmpty())
            return null;

        if (selectedBranchIndex >= 0) {
            FormTree.Branch stored = tree.getBranch(selectedBranchIndex);
            if (stored != null && unlocked.contains(stored))
                return stored;
        }

        return unlocked.get(0);
    }
    

    /**
     * Returns the global index of the next unlocked branch after the current one,
     * wrapping around. Returns {@code -1} if no branch is unlocked. With exactly one
     * unlocked branch this returns that branch's own index.
     */
    public int getNextUnlockedBranchIndex(FormTree tree, int skillLevel, int selectedBranchIndex) {
        if (tree == null)
            return -1;

        List<FormTree.Branch> allBranches = tree.getBranches();
        List<FormTree.Branch> unlocked = getUnlockedBranches(tree, skillLevel);
        if (unlocked.size() < 1)
            return -1;

        FormTree.Branch current = resolveActiveBranch(tree, skillLevel, selectedBranchIndex);
        int currentIdx = current != null ? allBranches.indexOf(current) : -1;
        int total = allBranches.size();

        for (int offset = 1; offset <= total; offset++) {
            int candidateIdx = (currentIdx + offset) % total;
            FormTree.Branch candidate = allBranches.get(candidateIdx);
            if (unlocked.contains(candidate))
                return candidateIdx;
        }
        return -1;
    }

    /**
     * Returns the first unlocked form in the branch at {@code branchIndex}.
     */
    public Form getFirstUnlockedFormInBranch(FormTree tree, int skillLevel, int branchIndex) {
        if (tree == null)
            return null;

        FormTree.Branch branch = tree.getBranch(branchIndex);
        if (branch == null)
            return null;

        return branch.getFirstUnlockedForm(getUnlockedForms(skillLevel));
    }

    public int getBranchIndexForLevel(FormTree tree, int skillLevel) {
        if (tree == null)
            return -1;

        for (int i = 0; i < tree.getBranches().size(); i++) {
            FormTree.Branch branch = tree.getBranch(i);
            if (branch != null && branch.getUnlockLevel() == skillLevel)
                return i;
        }
        return -1;
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        data.comment("Racial skill config. Level entries are stored as levelEntry_N nodes.");
        data.putInt("maxLevel", maxLevel);
        data.putString("displayName", displayName);
        data.putString("description", description);
        int i = 0;
        for (Map.Entry<Integer, LevelEntry> entry : levelEntries.entrySet()) {
            DataCompound entryData = data.child();
            entryData.putInt("level", entry.getKey());
            entryData.put("entry", entry.getValue());
            data.put("levelEntry_" + i, entryData);
            i++;
        }
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        maxLevel = data.getInt("maxLevel", maxLevel);
        setDisplayName(data.getString("displayName", displayName));
        setDescription(data.getString("description", description));
        int i = 0;
        while (data.has("levelEntry_" + i)) {
            DataCompound entryData = data.get("levelEntry_" + i);
            int level = entryData.getInt("level", -1);
            if (level < 0) {
                i++;
                continue;
            }

            DataCompound payload = entryData.get("entry");
            LevelEntry entry = levelEntries.get(level);
            if (entry == null) {
                entry = new LevelEntry(level, null, 0, 0);
                levelEntries.put(level, entry);
            }
            entry.deserialize(payload);
            i++;
        }
    }
}
