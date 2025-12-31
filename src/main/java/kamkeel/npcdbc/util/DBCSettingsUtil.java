package kamkeel.npcdbc.util;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.constants.DBCSettings;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class DBCSettingsUtil {

    public static String settings(EntityPlayer player) {
        return player.getEntityData().getCompoundTag("PlayerPersisted").getString("jrmcSettings");
    }

    private static DBCData data(EntityPlayer player) {
        return DBCData.get(player);
    }

    private static int get(EntityPlayer player, int setting) {
        return JRMCoreH.PlyrSettings(data(player).getRawCompound(), setting);
    }

    private static void set(EntityPlayer player, int setting, int value) {
        data(player).setSetting(setting, value);
    }

    private static void addSetting(EntityPlayer player, int setting) {
        JRMCoreH.PlyrSettingsOn(data(player).getRawCompound(), setting);
    }

    private static void removeSetting(EntityPlayer player, int setting) {
        JRMCoreH.PlyrSettingsRem(data(player).getRawCompound(), setting);
    }

    private static boolean isEnabled(EntityPlayer player, int setting) {
        return JRMCoreH.PlyrSettingsB(data(player).getRawCompound(), setting);
    }

    private static void setEnabled(EntityPlayer player, int setting, boolean enabled) {
        if (!isEnabled(player, setting) && enabled) {
            addSetting(player, setting);
        } else if (isEnabled(player, setting) && !enabled) {
            removeSetting(player, setting);
        }
    }

    public static boolean isFusion(EntityPlayer player) {
        return isEnabled(player, DBCSettings.FUSION_ENABLED);
    }

    public static void setFusion(EntityPlayer player, boolean enabled) {
        setEnabled(player, DBCSettings.FUSION_ENABLED, enabled);
    }

    public static boolean isPotentialUnleashed(EntityPlayer player) {
        return isEnabled(player, DBCSettings.POTENTIAL_UNLEASHED);
    }

    public static void setPotentialUnleashed(EntityPlayer player, boolean enabled) {
        setEnabled(player, DBCSettings.POTENTIAL_UNLEASHED, enabled);
    }

    public static boolean isKaioken(EntityPlayer player) {
        return isEnabled(player, DBCSettings.KAIOKEN_ENABLED);
    }

    public static void setKaioken(EntityPlayer player, boolean enabled) {
        setEnabled(player, DBCSettings.KAIOKEN_ENABLED, enabled);
    }

    public static boolean isUI(EntityPlayer player) {
        return isEnabled(player, DBCSettings.ULTRA_INSTINCT);
    }

    public static void setUI(EntityPlayer player, boolean enabled) {
        setEnabled(player, DBCSettings.ULTRA_INSTINCT, enabled);
    }

    public static boolean isGOD(EntityPlayer player) {
        return isEnabled(player, DBCSettings.GOD_OF_DESTRUCTION);
    }

    public static void setGOD(EntityPlayer player, boolean enabled) {
        setEnabled(player, DBCSettings.GOD_OF_DESTRUCTION, enabled);
    }

    public static boolean isFriendlyFist(EntityPlayer player) {
        return isEnabled(player, DBCSettings.FRIENDLY_FIST);
    }

    public static void setFriendlyFist(EntityPlayer player, boolean enabled) {
        setEnabled(player, DBCSettings.FRIENDLY_FIST, enabled);
    }

    public static boolean isSwoop(EntityPlayer player) {
        return isEnabled(player, DBCSettings.DODGE_ENABLED);
    }

    public static void setSwoop(EntityPlayer player, boolean enabled) {
        setEnabled(player, DBCSettings.DODGE_ENABLED, enabled);
    }

    public static boolean isKiProtection(EntityPlayer player) {
        return isEnabled(player, DBCSettings.KI_PROTECTION);
    }

    public static void setKiProtection(EntityPlayer player, boolean enabled) {
        setEnabled(player, DBCSettings.KI_PROTECTION, enabled);
    }

    public static boolean isKiFist(EntityPlayer player) {
        return isEnabled(player, DBCSettings.KI_FIST);
    }

    public static void setKiFist(EntityPlayer player, boolean enabled) {
        setEnabled(player, DBCSettings.KI_FIST, enabled);
    }

    public static boolean hasKiWeapon(EntityPlayer player) {
        return isEnabled(player, DBCSettings.KI_WEAPON_TOGGLE);
    }

    public static int getKiWeapon(EntityPlayer player) {
        if (!hasKiWeapon(player)) return -1;

        return get(player, DBCSettings.KI_WEAPON_TOGGLE);
    }

    public static void setKiWeapon(EntityPlayer player, boolean enabled) {
        setKiWeapon(player, enabled, 0);
    }

    public static void setKiWeapon(EntityPlayer player, boolean enabled, int mode) {
        mode = Math.max(-1, Math.min(1, mode));

        if (!enabled) {
            removeSetting(player, DBCSettings.KI_WEAPON_TOGGLE);
            return;
        }

        if (mode == 0) {
            setEnabled(player, DBCSettings.KI_WEAPON_TOGGLE, true);
        } else if (mode == 1) {
            set(player, DBCSettings.KI_WEAPON_TOGGLE, 1);
        } else {
            removeSetting(player, DBCSettings.KI_WEAPON_TOGGLE);
        }
    }

    public static void setKiWeaponMode(EntityPlayer player, int mode) {
        mode = Math.max(-1, Math.min(1, mode));

        if (!hasKiWeapon(player))
            return;

        if (mode == -1) {
            setKiWeapon(player, false);
            return;
        }

        set(player, DBCSettings.KI_WEAPON_TOGGLE, mode);
    }

    public static int getITRangeMode(EntityPlayer player) {
        return get(player, DBCSettings.INSTANT_TRANSMISSION_SHORT_RANGE);
    }

    public static void setITRangeMode(EntityPlayer player, int mode) {
        mode = Math.max(-1, Math.min(1, mode));

        if (mode == -1) {
            removeSetting(player, DBCSettings.INSTANT_TRANSMISSION_SHORT_RANGE);
            return;
        }

        set(player, DBCSettings.INSTANT_TRANSMISSION_SHORT_RANGE, mode);
    }

    public static int getITSurroundMode(EntityPlayer player) {
        return get(player, DBCSettings.INSTANT_TRANSMISSION_LONG_RANGE);
    }

    public static void setITSurroundMode(EntityPlayer player, int mode) {
        mode = Math.max(-1, Math.min(1, mode));

        if (mode == -1) {
            removeSetting(player, DBCSettings.INSTANT_TRANSMISSION_LONG_RANGE);
            return;
        }

        set(player, DBCSettings.INSTANT_TRANSMISSION_LONG_RANGE, mode);
    }
}
