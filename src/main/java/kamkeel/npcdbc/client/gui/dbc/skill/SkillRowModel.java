package kamkeel.npcdbc.client.gui.dbc.skill;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.api.skill.ICustomSkill;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.helper.StatSheetRaceHelper;
import kamkeel.npcdbc.data.race.progression.RaceSkill;
import kamkeel.npcdbc.data.skill.CustomSkillContainer;

import java.util.ArrayList;
import java.util.List;

public final class SkillRowModel {

    public enum RowType {
        VANILLA_OWNED,
        CUSTOM_OWNED,
        RACIAL,
        RACIAL_Y,
        LEARNABLE
    }

    private final RowType type;
    private final String displayName;
    private final String descriptionKey;
    private final String trlPrefix;
    private final int level;
    private final int maxLevel;
    private final int tpCost;
    private final int mindCost;
    private final boolean canAffordTP;
    private final boolean canAffordMind;
    private final boolean upgradeLocked;
    private final boolean tpLocked;
    private final boolean isMaxed;
    private final boolean isOwned;
    private final boolean canDelete;
    private final boolean showArcosianButton;
    private final int skillId;
    private final int vanillaIndex;
    private final String rawSkillString;

    private SkillRowModel(Builder b) {
        this.type = b.type;
        this.displayName = b.displayName;
        this.descriptionKey = b.descriptionKey;
        this.trlPrefix = b.trlPrefix;
        this.level = b.level;
        this.maxLevel = b.maxLevel;
        this.tpCost = b.tpCost;
        this.mindCost = b.mindCost;
        this.canAffordTP = b.canAffordTP;
        this.canAffordMind = b.canAffordMind;
        this.upgradeLocked = b.upgradeLocked;
        this.tpLocked = b.tpLocked;
        this.isMaxed = b.isMaxed;
        this.isOwned = b.isOwned;
        this.canDelete = b.canDelete;
        this.showArcosianButton = b.showArcosianButton;
        this.skillId = b.skillId;
        this.vanillaIndex = b.vanillaIndex;
        this.rawSkillString = b.rawSkillString;
    }

    public RowType getType() { return type; }
    public String getDisplayName() { return displayName; }
    public String getDescriptionKey() { return descriptionKey; }
    public String getTrlPrefix() { return trlPrefix; }
    public int getLevel() { return level; }
    public int getMaxLevel() { return maxLevel; }
    public int getTpCost() { return tpCost; }
    public int getMindCost() { return mindCost; }
    public boolean canAffordTP() { return canAffordTP; }
    public boolean canAffordMind() { return canAffordMind; }
    public boolean canUpgrade() { return !isMaxed && !upgradeLocked && canAffordTP && canAffordMind; }
    public boolean isUpgradeLocked() { return upgradeLocked; }
    public boolean isTpLocked() { return tpLocked; }
    public boolean isMaxed() { return isMaxed; }
    public boolean isOwned() { return isOwned; }
    public boolean canDelete() { return canDelete; }
    public boolean showArcosianButton() { return showArcosianButton; }
    public int getSkillId() { return skillId; }
    public int getVanillaIndex() { return vanillaIndex; }
    public String getRawSkillString() { return rawSkillString; }

    public String getCostLabel() {
        if (isMaxed) return JRMCoreH.trl("jrmc", "Maxed");
        if (upgradeLocked) return JRMCoreH.trl("jrmc", "UpgradeLocked");
        return "TP: " + JRMCoreH.numSep(tpCost) + " M: " + JRMCoreH.numSep(mindCost);
    }

    /**
     * Build overview rows in vanilla rendering order:
     * Racial X → Racial Y → vanilla owned (PlyrSkills) → custom addon skills.
     */
    public static List<SkillRowModel> buildOverviewRows(DBCData data) {
        List<SkillRowModel> rows = new ArrayList<>();
        boolean isChakra = JRMCoreH.isPowerTypeChakra(JRMCoreH.Pwrtyp);
        String modx = isChakra ? "nc" : "dbc";
        int mindUsed = JRMCoreH.skillSlot_MindUsed();

        String[] rSkls = isChakra ? JRMCoreH.ncRSkls : JRMCoreH.vlblRSkls;
        String[] rSklsNms = isChakra ? JRMCoreH.ncRSklsNms : JRMCoreH.vlblRSklsNms;
        int[][] rSklsLvl = isChakra ? JRMCoreH.ncRSklsLvl : JRMCoreH.DBCRacialSkillTPCost;
        int[][] rSklsMR = isChakra ? null : JRMCoreH.DBCRacialSkillMindCost;

        String[] cSkls = isChakra ? JRMCoreH.ncCSkls : JRMCoreH.vlblCSkls;
        String[] cSklsNms = isChakra ? JRMCoreH.NCRacialSkillAbilityNames : JRMCoreH.vlblCSklsNms;
        int[][] cSklsLvl = isChakra ? JRMCoreH.NCRacialSkillTPCost : JRMCoreH.vlblCSklsLvl;
        int[][] cSklsMR = isChakra ? JRMCoreH.NCRacialSkillMindCost : null;

        String[] sklsx = isChakra ? JRMCoreH.NCSkillIDs : JRMCoreH.DBCSkillsIDs;
        String[] sklsNmsx = isChakra ? JRMCoreH.NCSkillNames : JRMCoreH.DBCSkillNames;

        // 1. Racial X row
        SkillRowModel racialXRow = buildRacialXRow(data, modx, rSkls, rSklsNms, rSklsLvl, rSklsMR, mindUsed);
        if (racialXRow != null) {
            rows.add(racialXRow);
        }

        // 2. Racial Y row
        SkillRowModel racialYRow = buildRacialYRow(data, modx, cSkls, cSklsNms, cSklsLvl, cSklsMR, mindUsed);
        if (racialYRow != null) {
            rows.add(racialYRow);
        }

        // 3. Vanilla owned skills (PlyrSkills)
        if (JRMCoreH.PlyrSkills != null) {
            for (int i = 0; i < JRMCoreH.PlyrSkills.length; i++) {
                String skillStr = JRMCoreH.PlyrSkills[i];
                if (skillStr == null || skillStr.contains("pty") || skillStr.length() < 3) continue;

                String un = JRMCoreH.SklName(skillStr, sklsx, sklsNmsx);
                String namexx = JRMCoreH.trl(modx, un);
                int level = parseSkillLevel(skillStr);

                int tpCost = JRMCoreH.skillTPCost(skillStr, sklsx,
                    isChakra ? JRMCoreH.NCSkillTPCost : JRMCoreH.DBCSkillTPCost);
                int mindCost = JRMCoreH.skillMindRequirement(skillStr, sklsx,
                    isChakra ? JRMCoreH.NCSkillMindCost : JRMCoreH.DBCSkillMindCost);
                int mindResult = mindUsed + mindCost;
                boolean canAffordMind = JRMCoreH.canAffordSkill(JRMCoreH.statMindC(), mindResult);
                boolean canAffordTP = JRMCoreH.curTP >= tpCost;
                boolean locked = tpCost == -1;

                int[] sklsUps = isChakra ? JRMCoreH.ncSklsUps : JRMCoreH.vlblSklsUps;
                int maxUpgrades = JRMCoreH.SklInit(skillStr, sklsx, sklsUps);
                boolean isGodFormSpecial = JRMCoreH.isPowerTypeKi() && !JRMCoreH.rSai(JRMCoreH.Race) && skillStr.contains(JRMCoreH.DBCSkillsIDs[9]);
                int displayLevel = isGodFormSpecial
                    ? (level > 0 ? 0 : level)
                    : JRMCoreH.SklLvl_m(skillStr, sklsx, level);
                boolean maxed = !locked && !isGodFormSpecial && (level >= maxUpgrades);
                boolean canUpgrade = !maxed && !locked;

                rows.add(new Builder()
                    .type(RowType.VANILLA_OWNED)
                    .displayName(namexx)
                    .descriptionKey(JRMCoreH.trl(modx, un + "Desc" + getMeditationSuffix(skillStr, sklsx, isChakra)))
                    .trlPrefix(modx)
                    .level(displayLevel + 1)
                    .maxLevel(10)
                    .tpCost(Math.max(tpCost, 0))
                    .mindCost(Math.max(mindCost, 0))
                    .canAffordTP(canAffordTP)
                    .canAffordMind(canAffordMind)
                    .upgradeLocked(locked || !canUpgrade)
                    .tpLocked(locked)
                    .isMaxed(maxed)
                    .isOwned(true)
                    .canDelete(true)
                    .skillId(i)
                    .vanillaIndex(i)
                    .rawSkillString(skillStr)
                    .build());
            }
        }

        // 4. Custom addon skills
        for (CustomSkillContainer container : data.customSkills.values()) {
            ICustomSkill skill = container.getSkill();
            if (skill == null) continue;

            int level = container.getLevel();
            int tpCost = skill.getTPCost(level + 1);
            int mindCost = skill.getMindCost(level + 1);
            boolean canAffordTP = JRMCoreH.curTP >= tpCost;
            boolean canAffordMind = JRMCoreH.skillSlot_AvailableMindLeft() >= mindCost;
            boolean locked = tpCost == -1;
            boolean maxed = level >= skill.getMaxLevel();

            rows.add(new Builder()
                .type(RowType.CUSTOM_OWNED)
                .displayName(skill.getDisplayName())
                .descriptionKey(skill.getDescription())
                .trlPrefix(modx)
                .level(level)
                .maxLevel(skill.getMaxLevel())
                .tpCost(locked ? 0 : tpCost)
                .mindCost(locked ? 0 : Math.max(mindCost, 0))
                .canAffordTP(canAffordTP)
                .canAffordMind(canAffordMind)
                .upgradeLocked(locked)
                .tpLocked(locked)
                .isMaxed(maxed)
                .isOwned(true)
                .canDelete(true)
                .skillId(skill.getId())
                .vanillaIndex(-1)
                .build());
        }

        return rows;
    }

    private static SkillRowModel buildRacialXRow(DBCData data, String modx,
            String[] rSkls, String[] rSklsNms, int[][] rSklsLvl, int[][] rSklsMR, int mindUsed) {
        String racialStr = JRMCoreH.PlyrSkillX;
        if (racialStr == null || racialStr.contains("pty") || racialStr.length() < 2) {
            return null;
        }

        int skillLvl = parseSkillLevel(racialStr);
        Race customRace = StatSheetRaceHelper.getActiveCustomRace();

        if (customRace != null && customRace.skill != null) {
            RaceSkill raceSkill = customRace.skill;
            int nextLevel = skillLvl + 1;
            boolean maxed = nextLevel > raceSkill.getMaxLevel();
            int tpCost = maxed ? 0 : raceSkill.getTPCost(nextLevel);
            int mindCost = maxed ? 0 : raceSkill.getMindCost(nextLevel);
            boolean locked = !maxed && tpCost == -1;
            int mindResult = mindUsed + Math.max(mindCost, 0);
            boolean canAffordMind = !maxed && !locked && JRMCoreH.canAffordSkill(JRMCoreH.statMindC(), mindResult);
            boolean canAffordTP = !maxed && !locked && JRMCoreH.curTP >= tpCost;

            boolean showArcosian = false;

            return new Builder()
                .type(RowType.RACIAL)
                .displayName(raceSkill.getDisplayName())
                .descriptionKey(raceSkill.getDescription())
                .trlPrefix(modx)
                .level(skillLvl)
                .maxLevel(raceSkill.getMaxLevel())
                .tpCost(Math.max(tpCost, 0))
                .mindCost(Math.max(mindCost, 0))
                .canAffordTP(canAffordTP)
                .canAffordMind(canAffordMind)
                .upgradeLocked(locked)
                .tpLocked(locked)
                .isMaxed(maxed)
                .isOwned(true)
                .canDelete(false)
                .showArcosianButton(showArcosian)
                .skillId(100)
                .vanillaIndex(100)
                .rawSkillString(racialStr)
                .build();
        }

        String un = JRMCoreH.SklName(racialStr, rSkls, rSklsNms, JRMCoreH.Race);
        String namex = JRMCoreH.trl(modx, un);
        int tpCost = JRMCoreH.skillTPCost_X(racialStr, JRMCoreH.Race, rSklsLvl);
        int mindCost = JRMCoreH.skillMindRequirement_X(racialStr, JRMCoreH.Race, rSklsMR);
        int mindResult = mindUsed + mindCost;
        boolean canAffordMind = JRMCoreH.canAffordSkill(JRMCoreH.statMindC(), mindResult);
        boolean locked = tpCost == -1;
        boolean maxed = !locked && checkVanillaRacialMaxed(skillLvl, JRMCoreH.Race);
        boolean canAffordTP = !maxed && !locked && JRMCoreH.curTP >= tpCost;

        boolean showArcosian = JRMCoreH.Race == 4
            && JRMCoreH.DBC()
            && skillLvl >= 0;

        return new Builder()
            .type(RowType.RACIAL)
            .displayName(namex)
            .descriptionKey(JRMCoreH.trl(modx, un + "Desc"))
            .trlPrefix(modx)
            .level(skillLvl)
            .maxLevel(getVanillaRacialMaxLevel(JRMCoreH.Race))
            .tpCost(Math.max(tpCost, 0))
            .mindCost(Math.max(mindCost, 0))
            .canAffordTP(canAffordTP)
            .canAffordMind(canAffordMind)
            .upgradeLocked(locked)
            .tpLocked(locked)
            .isMaxed(maxed)
            .isOwned(true)
            .canDelete(false)
            .showArcosianButton(showArcosian)
            .skillId(100)
            .vanillaIndex(100)
            .rawSkillString(racialStr)
            .build();
    }

    private static SkillRowModel buildRacialYRow(DBCData data, String modx,
            String[] cSkls, String[] cSklsNms, int[][] cSklsLvl, int[][] cSklsMR, int mindUsed) {
        String yStr = JRMCoreH.PlyrSkillY;
        if (yStr == null || yStr.contains("pty") || yStr.contains("Sai")
            || JRMCoreH.Race == 1 || JRMCoreH.Race == 2 || yStr.length() < 2) {
            return null;
        }

        String unx = JRMCoreH.SklName(yStr, cSkls, cSklsNms);
        int nxxxx = parseSkillLevel(yStr);

        String displayName = (JRMCoreH.Race != 1 && JRMCoreH.Race != 2)
            ? unx
            : (nxxxx < JRMCoreH.TransSaiUpNam.length ? JRMCoreH.TransSaiUpNam[nxxxx] : unx);

        int tpCost = JRMCoreH.skillTPCost_X(yStr, JRMCoreH.Race, cSklsLvl);
        int mindCost = JRMCoreH.skillMindRequirement(yStr, cSkls, cSklsMR);
        int mindResult = mindUsed + mindCost;
        boolean canAffordMind = JRMCoreH.canAffordSkill(JRMCoreH.statMindC(), mindResult);
        boolean locked = tpCost == -1;
        boolean maxed = !locked && (nxxxx > 8);
        boolean canAffordTP = !maxed && !locked && JRMCoreH.curTP >= tpCost;

        int displayLevel = (JRMCoreH.Race != 1 && JRMCoreH.Race != 2)
            ? nxxxx + 1
            : nxxxx;

        return new Builder()
            .type(RowType.RACIAL_Y)
            .displayName(displayName)
            .descriptionKey(JRMCoreH.trl(modx, unx + "Desc"))
            .trlPrefix(modx)
            .level(displayLevel)
            .maxLevel(9)
            .tpCost(Math.max(tpCost, 0))
            .mindCost(Math.max(mindCost, 0))
            .canAffordTP(canAffordTP)
            .canAffordMind(canAffordMind)
            .upgradeLocked(locked || maxed)
            .tpLocked(locked)
            .isMaxed(maxed)
            .isOwned(true)
            .canDelete(false)
            .skillId(101)
            .vanillaIndex(101)
            .rawSkillString(yStr)
            .build();
    }

    public static List<SkillRowModel> buildLearnableRows(DBCData data) {
        List<SkillRowModel> rows = new ArrayList<>();
        boolean isChakra = JRMCoreH.isPowerTypeChakra(JRMCoreH.Pwrtyp);
        String[] skillNames = isChakra ? JRMCoreH.NCSkillNames : JRMCoreH.DBCSkillNames;
        String[] skillIds = isChakra ? JRMCoreH.NCSkillIDs : JRMCoreH.DBCSkillsIDs;
        String modx = isChakra ? "nc" : "dbc";

        if (skillNames == null) return rows;

        for (int i = 0; i < skillNames.length; i++) {
            boolean owned = JRMCoreH.SklLvl(i, JRMCoreH.Pwrtyp) >= 1;

            int tpCost = JRMCoreH.getSkillTPCost(i, 0, JRMCoreH.isPowerTypeKi());
            int mindCost = JRMCoreH.skillMindRequirement(skillIds[i], skillIds,
                isChakra ? JRMCoreH.NCSkillMindCost : JRMCoreH.DBCSkillMindCost);
            int mindResult = JRMCoreH.skillSlot_MindUsed() + mindCost;
            boolean canAffordMind = JRMCoreH.canAffordSkill(JRMCoreH.statMindC(), mindResult);
            boolean canAffordTP = JRMCoreH.curTP >= tpCost;

            rows.add(new Builder()
                .type(RowType.LEARNABLE)
                .displayName(JRMCoreH.trl(modx, skillNames[i]))
                .trlPrefix(modx)
                .level(0)
                .maxLevel(10)
                .tpCost(Math.max(tpCost, 0))
                .mindCost(Math.max(mindCost, 0))
                .canAffordTP(canAffordTP)
                .canAffordMind(canAffordMind)
                .upgradeLocked(tpCost == -1)
                .tpLocked(tpCost == -1)
                .isMaxed(false)
                .isOwned(owned)
                .skillId(i)
                .vanillaIndex(i)
                .build());
        }

        return rows;
    }

    static int parseSkillLevel(String skillString) {
        if (skillString == null || skillString.length() < 3) return 0;
        try {
            return Integer.parseInt(skillString.substring(2));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean checkVanillaRacialMaxed(int level, int race) {
        return level >= getVanillaRacialMaxLevel(race);
    }

    private static int getVanillaRacialMaxLevel(int race) {
        if (JRMCoreH.rSai(race)) return 7;
        if (race == 4) return 6;
        return 5;
    }

    /**
     * MF-21: Vanilla appends JRMCoreConfig.SklMedCat to the description key
     * for the Meditation skill (Ki index 7, Chakra index 11).
     * Returns the suffix string if the skill is Meditation, empty string otherwise.
     */
    private static String getMeditationSuffix(String skillStr, String[] sklsx, boolean isChakra) {
        int medIndex = isChakra ? 11 : 7;
        if (medIndex < sklsx.length && skillStr.contains(sklsx[medIndex])) {
            return "" + JRMCoreConfig.SklMedCat;
        }
        return "";
    }

    public static final class Builder {
        private RowType type;
        private String displayName = "";
        private String descriptionKey;
        private String trlPrefix = "dbc";
        private int level;
        private int maxLevel = 10;
        private int tpCost;
        private int mindCost;
        private boolean canAffordTP;
        private boolean canAffordMind;
        private boolean upgradeLocked;
        private boolean tpLocked;
        private boolean isMaxed;
        private boolean isOwned;
        private boolean canDelete;
        private boolean showArcosianButton;
        private int skillId = -1;
        private int vanillaIndex = -1;
        private String rawSkillString;

        public Builder type(RowType type) { this.type = type; return this; }
        public Builder displayName(String name) { this.displayName = name; return this; }
        public Builder descriptionKey(String key) { this.descriptionKey = key; return this; }
        public Builder trlPrefix(String prefix) { this.trlPrefix = prefix; return this; }
        public Builder level(int level) { this.level = level; return this; }
        public Builder maxLevel(int max) { this.maxLevel = max; return this; }
        public Builder tpCost(int cost) { this.tpCost = cost; return this; }
        public Builder mindCost(int cost) { this.mindCost = cost; return this; }
        public Builder canAffordTP(boolean can) { this.canAffordTP = can; return this; }
        public Builder canAffordMind(boolean can) { this.canAffordMind = can; return this; }
        public Builder upgradeLocked(boolean locked) { this.upgradeLocked = locked; return this; }
        public Builder tpLocked(boolean locked) { this.tpLocked = locked; return this; }
        public Builder isMaxed(boolean maxed) { this.isMaxed = maxed; return this; }
        public Builder isOwned(boolean owned) { this.isOwned = owned; return this; }
        public Builder canDelete(boolean del) { this.canDelete = del; return this; }
        public Builder showArcosianButton(boolean show) { this.showArcosianButton = show; return this; }
        public Builder skillId(int id) { this.skillId = id; return this; }
        public Builder vanillaIndex(int idx) { this.vanillaIndex = idx; return this; }
        public Builder rawSkillString(String raw) { this.rawSkillString = raw; return this; }

        public SkillRowModel build() {
            return new SkillRowModel(this);
        }
    }
}
