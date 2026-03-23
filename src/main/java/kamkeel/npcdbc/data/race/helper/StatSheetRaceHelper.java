package kamkeel.npcdbc.data.race.helper;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.config.dbc.JGConfigRaces;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.util.PlayerDataUtil;

/**
 * Race-aware helpers for stat sheet GUIs.
 * <p>
 * Both the vanilla DBC stat sheet ({@code JRMCoreGuiScreen} guiID=10) and
 * the addon's {@code StatSheetGui} share the same two problems when a
 * custom race is active:
 * <ol>
 *   <li>{@code JRMCoreH.Races[JRMCoreH.Race]} is only valid for indices 0-5;
 *       a custom race stores DBC race=0 (Human) as carrier, so the label
 *       shows "Human" instead of the custom race name.</li>
 *   <li>{@code JGConfigRaces.CONFIG_RACES_ATTRIBUTE_MULTI[Race]} is a
 *       size-6 array that will AIOOBE if a raw custom race index > 5
 *       somehow leaks through (and even when it doesn't, it would show
 *       Human multipliers — which may be misleading for tooltip display).</li>
 * </ol>
 * This helper centralises the fix for both cases.
 */
public final class StatSheetRaceHelper {

    private StatSheetRaceHelper() {}

    /**
     * Returns the display name for the player's race, substituting the
     * custom race name when one is active.
     * <p>
     * For vanilla races this is equivalent to
     * {@code JRMCoreH.trl("jrmc", JRMCoreH.Races[JRMCoreH.Race])}.
     */
    public static String getPlayerRaceDisplayName() {
        Race customRace = getActiveCustomRace();
        if (customRace != null) {
            return customRace.getMenuName();
        }
        return JRMCoreH.trl("jrmc", JRMCoreH.Races[JRMCoreH.Race]);
    }

    /**
     * Returns the race-class attribute multiplier string safe for tooltip display.
     * <p>
     * When a custom race is active the DBC config array is only size-6,
     * so we clamp to index 0 (Human) as a fallback. The resulting value
     * may not be the real custom race multiplier, but it prevents AIOOBE
     * and is "close enough" for tooltip informational purposes.
     */
    public static double getRaceClassAttributeMulti(int race, int classID, int attributeID) {
        int safeRace = clampRaceForConfig(race);
        return JGConfigRaces.CONFIG_RACES_ATTRIBUTE_MULTI[safeRace][classID][attributeID];
    }

    /**
     * Clamps a race index to the vanilla range (0-5) for use with DBC
     * config arrays that are only sized for vanilla races.
     * Custom races use Human (0) as the carrier race.
     */
    public static int clampRaceForConfig(int race) {
        return race >= RaceSelectorHelper.VANILLA_RACE_COUNT ? 0 : race;
    }

    /**
     * Returns the active custom race for the local player, or null if
     * the player is using a vanilla DBC race.
     */
    public static Race getActiveCustomRace() {
        PlayerDBCInfo info = PlayerDataUtil.getClientDBCInfo();
        if (info != null && info.isCustomRace()) {
            return info.getRace();
        }

        DBCData data = DBCData.getClient();
        if (data == null || !data.addonRace.isCustomRace()) {
            return null;
        }

        return data.addonRace.getRace();
    }

    /**
     * Returns true if the local player has a custom addon race active.
     */
    public static boolean isCustomRaceActive() {
        return getActiveCustomRace() != null;
    }
}
