package kamkeel.npcdbc.client.gui.dbc.creator;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.helper.RaceSelectorHelper;

/**
 * Ephemeral client-side state model for the enhanced character creator wizard.
 * <p>
 * This is the <b>single mutable source of truth</b> during the wizard lifecycle.
 * Pages read/write through this session; the {@link VanillaCreatorBridge} syncs
 * session state to vanilla statics for preview rendering and final commit.
 * <p>
 * Created via {@link #open()}, which snapshots current JRMCoreGuiScreen statics
 * and addon race state. The snapshot is used for cancel/reset behavior.
 */
public final class CreatorSession {

    // ── Race ──
    public int raceIndex;
    public int addonRaceId;
    public int stateSelected;

    // ── Appearance ──
    public int gender;
    public int years;
    public int hairBack;
    public int hairFront;
    public int hairColor;
    public int breastSize;
    public int skinType;
    public int bodyType;

    // ── Body Colors ──
    public int bodyColPreset;
    public int bodyColMain;
    public int bodyColSub1;
    public int bodyColSub2;
    public int bodyColSub3;

    // ── Face ──
    public int faceNose;
    public int faceMouth;
    public int eyes;
    public int eyeColPreset;
    public int eyeCol1;
    public int eyeCol2;

    // ── Hair Presets ──
    public int hairPreset;
    public boolean canSavePreset;

    // ── Power ──
    public int powerType;
    public int classType;
    public int kiColor;

    // ── Misc ──
    public boolean tail;
    public float brightness;

    // ── Snapshot (captured on open for cancel/reset) ──
    private int snapRaceIndex;
    private int snapAddonRaceId;
    private int snapStateSelected;
    private int snapGender;
    private int snapYears;
    private int snapHairBack;
    private int snapHairFront;
    private int snapHairColor;
    private int snapBreastSize;
    private int snapSkinType;
    private int snapBodyType;
    private int snapBodyColPreset;
    private int snapBodyColMain;
    private int snapBodyColSub1;
    private int snapBodyColSub2;
    private int snapBodyColSub3;
    private int snapFaceNose;
    private int snapFaceMouth;
    private int snapEyes;
    private int snapEyeColPreset;
    private int snapEyeCol1;
    private int snapEyeCol2;
    private int snapHairPreset;
    private boolean snapCanSavePreset;
    private int snapPowerType;
    private int snapClassType;
    private int snapKiColor;
    private boolean snapTail;
    private float snapBrightness;

    private CreatorSession() {}

    /**
     * Creates a new session by snapshotting current vanilla creator statics
     * and addon race state. Called once when the wizard opens.
     */
    public static CreatorSession open() {
        CreatorSession s = new CreatorSession();

        // Read current vanilla creator statics
        s.raceIndex = JRMCoreGuiScreen.RaceSlcted;
        s.stateSelected = JRMCoreGuiScreen.StateSlcted;
        s.gender = JRMCoreGuiScreen.GenderSlcted;
        s.years = JRMCoreGuiScreen.YearsSlcted;
        s.hairBack = JRMCoreGuiScreen.HairSlcted;
        s.hairFront = JRMCoreGuiScreen.Hair2Slcted;
        s.hairColor = JRMCoreGuiScreen.ColorSlcted;
        s.breastSize = JRMCoreGuiScreen.BreastSizeSlcted;
        s.skinType = JRMCoreGuiScreen.SkinTypeSlcted;
        s.bodyType = JRMCoreGuiScreen.BodyTypeSlcted;
        s.bodyColPreset = JRMCoreGuiScreen.BodyColPresetSlcted;
        s.bodyColMain = JRMCoreGuiScreen.BodyColMainSlcted;
        s.bodyColSub1 = JRMCoreGuiScreen.BodyColSub1Slcted;
        s.bodyColSub2 = JRMCoreGuiScreen.BodyColSub2Slcted;
        s.bodyColSub3 = JRMCoreGuiScreen.BodyColSub3Slcted;
        s.faceNose = JRMCoreGuiScreen.FaceNoseSlcted;
        s.faceMouth = JRMCoreGuiScreen.FaceMouthSlcted;
        s.eyes = JRMCoreGuiScreen.EyesSlcted;
        s.eyeColPreset = JRMCoreGuiScreen.EyeColPresetSlcted;
        s.eyeCol1 = JRMCoreGuiScreen.EyeCol1Slcted;
        s.eyeCol2 = JRMCoreGuiScreen.EyeCol2Slcted;
        s.hairPreset = JRMCoreGuiScreen.HairPrstsSlcted;
        s.canSavePreset = JRMCoreGuiScreen.canSavePreset;
        s.powerType = JRMCoreGuiScreen.PwrtypSlcted;
        s.classType = JRMCoreGuiScreen.ClassSlcted;
        s.kiColor = JRMCoreGuiScreen.KiColorSlcted;
        s.tail = JRMCoreGuiScreen.tail;
        s.brightness = JRMCoreGuiScreen.BrghtSlcted;

        // Seed race from vanilla; then check addon override
        s.addonRaceId = -1;
        DBCData clientData = DBCData.getClient();
        if (clientData != null && clientData.addonRaceID > 0) {
            int customIndex = RaceController.Instance.getIndex(clientData.addonRaceID);
            if (customIndex >= 0) {
                s.raceIndex = RaceSelectorHelper.VANILLA_RACE_COUNT + customIndex;
                s.addonRaceId = clientData.addonRaceID;
            }
        }

        s.captureSnapshot();
        return s;
    }

    /** Captures current working values as the snapshot for cancel/reset. */
    private void captureSnapshot() {
        snapRaceIndex = raceIndex;
        snapAddonRaceId = addonRaceId;
        snapStateSelected = stateSelected;
        snapGender = gender;
        snapYears = years;
        snapHairBack = hairBack;
        snapHairFront = hairFront;
        snapHairColor = hairColor;
        snapBreastSize = breastSize;
        snapSkinType = skinType;
        snapBodyType = bodyType;
        snapBodyColPreset = bodyColPreset;
        snapBodyColMain = bodyColMain;
        snapBodyColSub1 = bodyColSub1;
        snapBodyColSub2 = bodyColSub2;
        snapBodyColSub3 = bodyColSub3;
        snapFaceNose = faceNose;
        snapFaceMouth = faceMouth;
        snapEyes = eyes;
        snapEyeColPreset = eyeColPreset;
        snapEyeCol1 = eyeCol1;
        snapEyeCol2 = eyeCol2;
        snapHairPreset = hairPreset;
        snapCanSavePreset = canSavePreset;
        snapPowerType = powerType;
        snapClassType = classType;
        snapKiColor = kiColor;
        snapTail = tail;
        snapBrightness = brightness;
    }

    /** Restores all working values to their initial snapshot state. */
    public void resetToSnapshot() {
        raceIndex = snapRaceIndex;
        addonRaceId = snapAddonRaceId;
        stateSelected = snapStateSelected;
        gender = snapGender;
        years = snapYears;
        hairBack = snapHairBack;
        hairFront = snapHairFront;
        hairColor = snapHairColor;
        breastSize = snapBreastSize;
        skinType = snapSkinType;
        bodyType = snapBodyType;
        bodyColPreset = snapBodyColPreset;
        bodyColMain = snapBodyColMain;
        bodyColSub1 = snapBodyColSub1;
        bodyColSub2 = snapBodyColSub2;
        bodyColSub3 = snapBodyColSub3;
        faceNose = snapFaceNose;
        faceMouth = snapFaceMouth;
        eyes = snapEyes;
        eyeColPreset = snapEyeColPreset;
        eyeCol1 = snapEyeCol1;
        eyeCol2 = snapEyeCol2;
        hairPreset = snapHairPreset;
        canSavePreset = snapCanSavePreset;
        powerType = snapPowerType;
        classType = snapClassType;
        kiColor = snapKiColor;
        tail = snapTail;
        brightness = snapBrightness;
    }

    /** Whether the currently selected race is a custom addon race (index >= 6). */
    public boolean isCustomRace() {
        return RaceSelectorHelper.isCustomRaceIndex(raceIndex);
    }

    /** Resolves the custom Race object for the current selection, or null if vanilla. */
    public Race getSelectedCustomRace() {
        return RaceSelectorHelper.getCustomRaceByIndex(raceIndex);
    }

    /**
     * Returns the vanilla race index to use for array lookups.
     * For custom races (index >= 6), clamps to the vanilla base race.
     */
    public int getVanillaRaceIndex() {
        return RaceSelectorHelper.clampRaceForStats(raceIndex);
    }

    /**
     * Applies race-change side effects, mirroring {@code JRMCoreGuiScreen.setchangerace()}.
     * <p>
     * Clamps body type, face, eyes to the new race's limits; applies default body/eye color
     * presets; sets StateSlcted (4 for Arcosians, 0 otherwise); forces gender if race is
     * single-gender; forces Namekian tail on.
     * <p>
     * Uses the expanded arrays from {@link RaceSelectorHelper} so custom races are handled.
     */
    public void applyRaceChange() {
        String[] races = RaceSelectorHelper.getRaces();
        int[] raceGenders = RaceSelectorHelper.getRaceGenders();
        int[][] sknLimits = RaceSelectorHelper.getCustomSknLimits();
        int[] sknLimitsBCP = RaceSelectorHelper.getCustomSknLimitsBCP();

        // Single-gender race: force male
        if (raceIndex < raceGenders.length && raceGenders[raceIndex] == 1) {
            gender = 0;
        }

        // Arcosian state
        if (JRMCoreH.isRaceArcosian(getVanillaRaceIndex())) {
            stateSelected = 4;
        } else {
            stateSelected = 0;
        }

        // Clamp body type
        if (raceIndex < sknLimits.length) {
            int maxBT = sknLimits[raceIndex][0];
            if (bodyType > maxBT - 1) bodyType = maxBT - 1;
        }

        // Apply body color preset
        applyBodyColorPreset();

        // Clamp face/eye indices
        if (raceIndex < sknLimits.length) {
            int maxNose = sknLimits[raceIndex][2];
            if (faceNose > maxNose - 1) faceNose = maxNose - 1;
            int maxMouth = sknLimits[raceIndex][3];
            if (faceMouth > maxMouth - 1) faceMouth = maxMouth - 1;
            int maxEyes = sknLimits[raceIndex][4];
            if (eyes > maxEyes - 1) eyes = maxEyes - 1;
        }

        // Apply eye color preset
        applyEyeColorPreset();

        // Namekian: force tail on
        if (JRMCoreH.isRaceNamekian(getVanillaRaceIndex())) {
            tail = true;
        }

    }

    /** Applies body colors from the current preset, mirroring {@code setchangebodycol()}. */
    public void applyBodyColorPreset() {
        int[][][] bodyCols = RaceSelectorHelper.getDefBodyCols();
        if (bodyColPreset < bodyCols.length && raceIndex < bodyCols[bodyColPreset].length) {
            int[] preset = bodyCols[bodyColPreset][raceIndex];
            bodyColMain = preset.length > 0 ? preset[0] : 0;
            bodyColSub1 = preset.length > 1 ? preset[1] : 0;
            bodyColSub2 = preset.length > 2 ? preset[2] : 0;
            bodyColSub3 = preset.length > 3 ? preset[3] : 0;
        }
    }

    /** Applies eye colors from the current preset, mirroring {@code setchangeeyecol()}. */
    public void applyEyeColorPreset() {
        int[][] eyeCols = RaceSelectorHelper.getDefEyeCols();
        if (eyeColPreset < eyeCols.length && raceIndex < eyeCols[eyeColPreset].length) {
            eyeCol1 = eyeCols[eyeColPreset][raceIndex];
            eyeCol2 = eyeCols[eyeColPreset][raceIndex];
        }
    }

    /** Syncs Majin hair color to body main color, mirroring {@code updateMajinHairToBodyColor()}. */
    public void syncMajinHairColor() {
        if (JRMCoreH.isRaceMajin(getVanillaRaceIndex()) && hairColor != bodyColMain) {
            hairColor = bodyColMain;
        }
    }

    /**
     * Resolves the enablement flags for the current race/config.
     * Returns a snapshot of what controls should be available.
     */
    public AppearanceFlags resolveAppearanceFlags() {
        return new AppearanceFlags(this);
    }

    /**
     * Computed enablement flags for appearance controls, derived from
     * current session state and expanded race arrays. Immutable snapshot.
     */
    public static final class AppearanceFlags {
        public final boolean canRace;
        public final boolean canGender;
        public final boolean canYears;
        public final boolean canHair;
        public final boolean canColor;
        public final boolean canCustomSkin;
        public final boolean canTail;
        public final int bodyColorSlots;
        public final int eyeColorSlots;

        AppearanceFlags(CreatorSession s) {
            String[] raceAllow = RaceSelectorHelper.getRaceAllow();
            int[] raceGenders = RaceSelectorHelper.getRaceGenders();
            String[] raceCanHaveHair = RaceSelectorHelper.getRaceCanHaveHair();
            int[] raceCustomSkin = RaceSelectorHelper.getRaceCustomSkin();
            int[] raceHairColor = RaceSelectorHelper.getRaceHairColor();
            int[][] sknLimits = RaceSelectorHelper.getCustomSknLimits();

            canRace = s.raceIndex < raceAllow.length && JRMCoreH.Allow(raceAllow[s.raceIndex]);
            canGender = s.raceIndex < raceGenders.length && raceGenders[s.raceIndex] != 1
                && JRMCoreH.Allow(JRMCoreH.GenderAllow[s.gender < JRMCoreH.GenderAllow.length ? s.gender : 0]);
            canYears = JRMCoreH.Allow("JYC");
            canHair = s.raceIndex < raceCanHaveHair.length && raceCanHaveHair[s.raceIndex].contains("H");
            canTail = JRMCoreH.isRaceSaiyan(s.getVanillaRaceIndex())
                || JRMCoreH.isRaceHalfSaiyan(s.getVanillaRaceIndex());

            // Hair color: Majin copies body color; fixed-color races can't change
            if (JRMCoreH.isRaceMajin(s.getVanillaRaceIndex())) {
                canColor = false;
            } else if (canHair && s.raceIndex < raceHairColor.length && raceHairColor[s.raceIndex] == -1) {
                canColor = true;
            } else {
                canColor = false;
            }

            // Custom skin toggle
            if (s.raceIndex < raceCustomSkin.length) {
                canCustomSkin = raceCustomSkin[s.raceIndex] == 2;
            } else {
                canCustomSkin = false;
            }

            // Body/eye color slot counts
            bodyColorSlots = s.raceIndex < sknLimits.length ? sknLimits[s.raceIndex][1] : 1;
            eyeColorSlots = s.raceIndex < sknLimits.length ? sknLimits[s.raceIndex][5] : 0;
        }
    }

    /**
     * Writes current session values back to JRMCoreGuiScreen statics.
     * This is required before calling vanilla helper methods (setdns, setchangerace, etc.)
     * and for preview rendering which reads these statics.
     */
    public void syncToVanillaStatics() {
        JRMCoreGuiScreen.RaceSlcted = raceIndex;
        JRMCoreGuiScreen.StateSlcted = stateSelected;
        JRMCoreGuiScreen.GenderSlcted = gender;
        JRMCoreGuiScreen.YearsSlcted = years;
        JRMCoreGuiScreen.HairSlcted = hairBack;
        JRMCoreGuiScreen.Hair2Slcted = hairFront;
        JRMCoreGuiScreen.ColorSlcted = hairColor;
        JRMCoreGuiScreen.BreastSizeSlcted = breastSize;
        JRMCoreGuiScreen.SkinTypeSlcted = skinType;
        JRMCoreGuiScreen.BodyTypeSlcted = bodyType;
        JRMCoreGuiScreen.BodyColPresetSlcted = bodyColPreset;
        JRMCoreGuiScreen.BodyColMainSlcted = bodyColMain;
        JRMCoreGuiScreen.BodyColSub1Slcted = bodyColSub1;
        JRMCoreGuiScreen.BodyColSub2Slcted = bodyColSub2;
        JRMCoreGuiScreen.BodyColSub3Slcted = bodyColSub3;
        JRMCoreGuiScreen.FaceNoseSlcted = faceNose;
        JRMCoreGuiScreen.FaceMouthSlcted = faceMouth;
        JRMCoreGuiScreen.EyesSlcted = eyes;
        JRMCoreGuiScreen.EyeColPresetSlcted = eyeColPreset;
        JRMCoreGuiScreen.EyeCol1Slcted = eyeCol1;
        JRMCoreGuiScreen.EyeCol2Slcted = eyeCol2;
        JRMCoreGuiScreen.HairPrstsSlcted = hairPreset;
        JRMCoreGuiScreen.canSavePreset = canSavePreset;
        JRMCoreGuiScreen.PwrtypSlcted = powerType;
        JRMCoreGuiScreen.ClassSlcted = classType;
        JRMCoreGuiScreen.KiColorSlcted = kiColor;
        JRMCoreGuiScreen.tail = tail;
        JRMCoreGuiScreen.BrghtSlcted = brightness;
    }
}
