package kamkeel.npcdbc.data.race.helper;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.display.DisplayComponent;
import kamkeel.npcdbc.data.race.display.RaceDisplay;

import java.util.Arrays;
import java.util.List;

/**
 * Builds and caches expanded versions of DBC's race-indexed arrays
 * (JRMCoreH.Races, RaceAllow, customSknLimits, etc.) that include
 * custom races from RaceController, appended after the 6 vanilla entries.
 *
 * Each expanded array is rebuilt only when the dirty flag is set
 * (i.e. when custom races are registered or the registry changes).
 */
public final class RaceSelectorHelper {

    public static final int VANILLA_RACE_COUNT = 6;

    private static boolean dirty = true;
    private static String previewCustomRaceKey = null;
    private static boolean previewActive = false;

    private static String[] expandedRaces;
    private static String[] expandedRaceAllow;
    private static int[][] expandedCustomSknLimits;
    private static int[] expandedCustomSknLimitsBCP;
    private static int[][] expandedDefEyeCols;
    private static int[][][] expandedDefBodyCols;
    private static int[] expandedRaceGenders;
    private static String[] expandedRaceCanHaveHair;
    private static String[] expandedRaceCanHavePwr;
    private static int[] expandedRaceCustomSkin;
    private static int[] expandedRaceHairColor;

    private RaceSelectorHelper() {}

    public static void markDirty() {
        dirty = true;
    }

    public static void setPreviewRaceIndex(int raceIndex) {
        Race race = getCustomRaceByIndex(raceIndex);
        previewCustomRaceKey = race != null ? race.getName() : null;
    }

    public static void setPreviewActive(boolean active) {
        previewActive = active;
        if (!active) {
            previewCustomRaceKey = null;
        }
    }

    public static boolean isPreviewActive() {
        return previewActive;
    }

    public static void clearPreviewRace() {
        previewCustomRaceKey = null;
    }
    

    public static Race getPreviewCustomRace() {
        if (previewCustomRaceKey == null) {
            return null;
        }
        return RaceController.Instance.getByName(previewCustomRaceKey);
    }

    private static void rebuildIfDirty() {
        if (!dirty) return;
        dirty = false;

        List<Race> customRaces = RaceController.Instance.getRaceOrder();
        int customCount = customRaces.size();
        int totalCount = VANILLA_RACE_COUNT + customCount;

        for (Race race : customRaces) {
            race.display.syncCreatorMetadata();
        }

        expandedRaces = expandStringArray(JRMCoreH.Races, totalCount, customRaces, RaceSelectorHelper::raceName);
        expandedRaceAllow = expandStringArray(JRMCoreH.RaceAllow, totalCount, customRaces, RaceSelectorHelper::raceAllow);
        expandedRaceCanHaveHair = expandStringArray(JRMCoreH.RaceCanHaveHair, totalCount, customRaces, RaceSelectorHelper::hairType);
        expandedRaceCanHavePwr = expandStringArray(JRMCoreH.RaceCanHavePwr, totalCount, customRaces, RaceSelectorHelper::allowedPowerTypes);

        expandedRaceGenders = expandIntArray(JRMCoreH.RaceGenders, totalCount, customRaces, RaceSelectorHelper::genderCount);
        expandedRaceCustomSkin = expandIntArray(JRMCoreH.RaceCustomSkin, totalCount, customRaces, RaceSelectorHelper::customSkinMode);
        expandedRaceHairColor = expandIntArray(JRMCoreH.RaceHairColor, totalCount, customRaces, RaceSelectorHelper::fixedHairColor);

        expandedCustomSknLimits = buildExpandedSkinLimits(customRaces, totalCount);
        expandedCustomSknLimitsBCP = buildExpandedSkinLimitsBCP(customRaces, totalCount);
        expandedDefEyeCols = buildExpandedDefEyeCols(customRaces, totalCount);
        expandedDefBodyCols = buildExpandedDefBodyCols(customRaces, totalCount);
    }

    public static String[] getRaces() {
        rebuildIfDirty();
        return expandedRaces;
    }

    public static String[] getRaceAllow() {
        rebuildIfDirty();
        return expandedRaceAllow;
    }

    public static int[][] getCustomSknLimits() {
        rebuildIfDirty();
        return expandedCustomSknLimits;
    }

    public static int[] getCustomSknLimitsBCP() {
        rebuildIfDirty();
        return expandedCustomSknLimitsBCP;
    }

    public static int[][] getDefEyeCols() {
        rebuildIfDirty();
        return expandedDefEyeCols;
    }

    public static int[][][] getDefBodyCols() {
        rebuildIfDirty();
        return expandedDefBodyCols;
    }

    public static int[] getRaceGenders() {
        rebuildIfDirty();
        return expandedRaceGenders;
    }

    public static String[] getRaceCanHaveHair() {
        rebuildIfDirty();
        return expandedRaceCanHaveHair;
    }

    public static String[] getRaceCanHavePwr() {
        rebuildIfDirty();
        return expandedRaceCanHavePwr;
    }

    public static int[] getRaceCustomSkin() {
        rebuildIfDirty();
        return expandedRaceCustomSkin;
    }

    public static int[] getRaceHairColor() {
        rebuildIfDirty();
        return expandedRaceHairColor;
    }

    /**
     * Clamps a race index > 5 down to 0 (Human) for use in
     * stat preview methods that internally access size-6 config arrays.
     */
    public static int clampRaceForStats(int race) {
        return race >= VANILLA_RACE_COUNT ? 0 : race;
    }

    public static boolean isCustomRaceIndex(int raceIndex) {
        return raceIndex >= VANILLA_RACE_COUNT;
    }

    public static Race getCustomRaceByIndex(int raceIndex) {
        if (!isCustomRaceIndex(raceIndex)) return null;
        return RaceController.Instance.getByIndex(raceIndex - VANILLA_RACE_COUNT);
    }

    // Array builders

    @FunctionalInterface
    private interface StringExtractor {
        String extract(Race race);
    }

    @FunctionalInterface
    private interface IntExtractor {
        int extract(Race race);
    }

    private static String[] expandStringArray(String[] vanilla, int totalCount, List<Race> customRaces, StringExtractor extractor) {
        String[] result = Arrays.copyOf(vanilla, totalCount);
        for (int i = 0; i < customRaces.size(); i++) {
            result[VANILLA_RACE_COUNT + i] = extractor.extract(customRaces.get(i));
        }
        return result;
    }

    private static int[] expandIntArray(int[] vanilla, int totalCount, List<Race> customRaces, IntExtractor extractor) {
        int[] result = Arrays.copyOf(vanilla, totalCount);
        for (int i = 0; i < customRaces.size(); i++) {
            result[VANILLA_RACE_COUNT + i] = extractor.extract(customRaces.get(i));
        }
        return result;
    }

    private static int[][] buildExpandedSkinLimits(List<Race> customRaces, int totalCount) {
        int[][] vanilla = JRMCoreH.customSknLimits;
        int[][] result = new int[totalCount][];
        for (int i = 0; i < vanilla.length && i < totalCount; i++) {
            result[i] = vanilla[i];
        }
        for (int i = 0; i < customRaces.size(); i++) {
            result[VANILLA_RACE_COUNT + i] = customRaces.get(i).display.getSkinLimits().clone();
        }
        return result;
    }

    /**
     * Builds the expanded customSknLimitsBCP array.
     * Uses {@link RaceDisplay#bodyColorPresetCount} which is auto-derived
     * by {@link RaceDisplay#syncCreatorMetadata()} from the body component's presetCount.
     */
    private static int[] buildExpandedSkinLimitsBCP(List<Race> customRaces, int totalCount) {
        int[] vanilla = JRMCoreH.customSknLimitsBCP;
        int[] result = Arrays.copyOf(vanilla, totalCount);
        for (int i = 0; i < customRaces.size(); i++) {
            Race race = customRaces.get(i);
            result[VANILLA_RACE_COUNT + i] = Math.max(1, race.display.bodyColorPresetCount);
        }
        return result;
    }

    /**
     * defeyecols structure: int[presetRow][raceIndex]
     * Each preset row needs a new column per custom race.
     */
    private static int[][] buildExpandedDefEyeCols(List<Race> customRaces, int totalCount) {
        int[][] vanilla = JRMCoreH.defeyecols;
        int presetCount = vanilla.length;
        int[][] result = new int[presetCount][totalCount];
        for (int p = 0; p < presetCount; p++) {
            System.arraycopy(vanilla[p], 0, result[p], 0, Math.min(vanilla[p].length, totalCount));
            for (int i = 0; i < customRaces.size(); i++) {
                int[] eyeColors = customRaces.get(i).display.buildEyeColorRows();
                result[p][VANILLA_RACE_COUNT + i] = p < eyeColors.length ? eyeColors[p] : 1;
            }
        }
        return result;
    }

    /**
     * defbodycols structure: int[presetRow][raceIndex][colorComponents]
     * Each preset row needs a new column per custom race.
     * <p>
     * For custom races, body color data is synthesized from the display model:
     * <ul>
     *   <li>Row 0 is always the default colors declared on each body layer.</li>
     *   <li>Row p (p >= 1) reads preset index {@code p - 1} from each layer via
     *       {@link RaceDisplay#buildBodyColorRowForPreset(int)}.
     *       If a layer has fewer presets than expected (not yet normalized),
     *       it falls back to the layer's default color.</li>
     * </ul>
     * Call {@link DisplayComponent#normalizeLayerPresets()} on the body component
     * before registration to guarantee all layers have a consistent preset count.
     */
    private static int[][][] buildExpandedDefBodyCols(List<Race> customRaces, int totalCount) {
        int[][][] vanilla = JRMCoreH.defbodycols;
        int presetCount = vanilla.length;
        int[][][] result = new int[presetCount][totalCount][];

        for (int p = 0; p < presetCount; p++) {
            for (int r = 0; r < vanilla[p].length && r < totalCount; r++) {
                result[p][r] = vanilla[p][r];
            }
            for (int i = 0; i < customRaces.size(); i++) {
                Race race = customRaces.get(i);
                result[p][VANILLA_RACE_COUNT + i] = (p == 0)
                    ? race.display.buildDefaultBodyColorRow()
                    : race.display.buildBodyColorRowForPreset(p - 1);
            }
        }
        return result;
    }

    // Race property extractors

    private static String raceName(Race race) {
        return race.getMenuName();
    }

    private static String raceAllow(Race race) {
        return race.display.getRaceAllow();
    }

    private static String hairType(Race race) {
        return race.display.getHairType();
    }

    private static String allowedPowerTypes(Race race) {
        return race.display.getAllowedPowerTypes();
    }

    private static int genderCount(Race race) {
        return race.display.getGenderCount();
    }

    private static int customSkinMode(Race race) {
        return race.display.getCustomSkinMode();
    }

    private static int fixedHairColor(Race race) {
        return race.display.getFixedHairColor();
    }
}
