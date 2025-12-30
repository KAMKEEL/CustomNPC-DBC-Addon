package kamkeel.npcdbc.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class DBCSettingsUtil {
    private static final String KEY = "jrmcSettings";
    private static final String[] invertedIDs = new String[]{"D", "P", "F"};

    private static NBTTagCompound nbt(EntityPlayer player) {
        return player.getEntityData().getCompoundTag("PlayerPersisted");
    }

    private static String settings(EntityPlayer player) {
        return nbt(player).getString(KEY);
    }

    private static void set(EntityPlayer player, String s) {
        nbt(player).setString(KEY, s);
    }

    private static boolean isInvertedID(String id) {
        for (int i = 0; i < invertedIDs.length; i++) {
            if (id.contains(invertedIDs[i]))
                return true;
        }

        return false;
    }

    private static void addSetting(EntityPlayer player, String setting) {
        String s = settings(player) + setting;

        set(player, s);
    }

    private static void removeSetting(EntityPlayer player, String setting) {
        replaceSetting(player, setting, "");
    }

    private static void replaceSetting(EntityPlayer player, String setting, String newSetting) {
        String s = settings(player).replace(setting, newSetting);

        if (s.isEmpty())
            s = " ";

        set(player, s);
    }

    private static boolean isEnabled(EntityPlayer player, String setting) {
        String s = settings(player);
        boolean enabled = s.contains(setting);
        boolean inverted = isInvertedID(setting);

        return inverted ? !enabled : enabled;
    }

    private static void setEnabled(EntityPlayer player, String setting, boolean enabled) {
        boolean inverted = isInvertedID(setting);

        if (!isEnabled(player, setting) && enabled) {
            if (inverted) {
                removeSetting(player, setting);
            } else {
                addSetting(player, setting);
            }
        }

        if (isEnabled(player, setting) && !enabled) {
            if (inverted) {
                addSetting(player, setting);
            } else {
                removeSetting(player, setting);
            }
        }
    }

    private static int getSettingsGroup(EntityPlayer player, String[] settings) {
        String s = settings(player);

        for (int i = 0; i < settings.length; i++) {
            if (s.contains(settings[i])) {
                return i + 1;
            }
        }

        return 0;
    }

    private static void setSettingsGroup(EntityPlayer player, String[] settings, int group) {
        group = Math.max(0, Math.min(settings.length, group));

        int currentGroup = getSettingsGroup(player, settings);
        String setting = settings[0].charAt(0) + "";

        if (group == 0 && currentGroup == 0) {
            return;
        }

        if (currentGroup == 0) {
            addSetting(player, setting + (group - 1));
            return;
        }

        if (group == 0) {
            removeSetting(player, setting + (currentGroup - 1));
            return;
        }

        replaceSetting(player, setting + (currentGroup - 1), setting + (group -1));
    }

    public static int getFormGroup(EntityPlayer player) {
        return getSettingsGroup(player, new String[]{"R0", "R1", "R2", "R3"});
    }

    public static void setFormGroup(EntityPlayer player, int group) {
        setSettingsGroup(player, new String[]{"R0", "R1", "R2", "R3"}, group);
    }

    public static boolean isFusion(EntityPlayer player) {
        return isEnabled(player, "Z0");
    }

    public static void setFusion(EntityPlayer player, boolean enabled) {
        setEnabled(player, "Z0", enabled);
    }

    public static boolean isPotentialUnleashed(EntityPlayer player) {
        return isEnabled(player, "M0");
    }

    public static void setPotentialUnleashed(EntityPlayer player, boolean enabled) {
        setEnabled(player, "M0", enabled);
    }

    public static boolean isKaioken(EntityPlayer player) {
        return isEnabled(player, "K0");
    }

    public static void setKaioken(EntityPlayer player, boolean enabled) {
        setEnabled(player, "K0", enabled);
    }

    public static boolean isUI(EntityPlayer player) {
        return isEnabled(player, "U0");
    }

    public static void setUI(EntityPlayer player, boolean enabled) {
        setEnabled(player, "U0", enabled);
    }

    public static boolean isGOD(EntityPlayer player) {
        return isEnabled(player, "H0");
    }

    public static void setGOD(EntityPlayer player, boolean enabled) {
        setEnabled(player, "H0", enabled);
    }

    public static boolean isFriendlyFist(EntityPlayer player) {
        return isEnabled(player, "I0");
    }

    public static void setFriendlyFist(EntityPlayer player, boolean enabled) {
        setEnabled(player, "I0", enabled);
    }

    public static boolean isSwoop(EntityPlayer player) {
        return isEnabled(player, "D0");
    }

    public static void setSwoop(EntityPlayer player, boolean enabled) {
        setEnabled(player, "D0", enabled);
    }

    public static boolean isKiProtection(EntityPlayer player) {
        return isEnabled(player, "P0");
    }

    public static void setKiProtection(EntityPlayer player, boolean enabled) {
        setEnabled(player, "P0", enabled);
    }

    public static boolean isKiFist(EntityPlayer player) {
        return isEnabled(player, "F0");
    }

    public static void setKiFist(EntityPlayer player, boolean enabled) {
        setEnabled(player, "F0", enabled);
    }

    public static boolean hasKiWeapon(EntityPlayer player) {
        return isEnabled(player, "S");
    }

    public static int getKiWeapon(EntityPlayer player) {
        if (!hasKiWeapon(player)) return -1;

        String s = settings(player);
        int index = s.indexOf("S");
        String group = s.substring(index, 2);

        try {
            return Integer.parseInt(group.charAt(1) + "");
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void setKiWeapon(EntityPlayer player, boolean enabled) {
        setKiWeapon(player, enabled, 0);
    }

    public static void setKiWeapon(EntityPlayer player, boolean enabled, int mode) {
        mode = Math.max(0, Math.min(1, mode));

        setEnabled(player, "S" + mode, enabled);
    }

    public static void setKiWeaponMode(EntityPlayer player, int mode) {
        mode = Math.max(-1, Math.min(1, mode));

        if (!hasKiWeapon(player))
            return;

        if (mode == -1) {
            setKiWeapon(player, false);
        }

        replaceSetting(player, "S" + getKiWeapon(player), "S" + mode);
    }

    public static int getITRangeMode(EntityPlayer player) {
        return getSettingsGroup(player, new String[]{"A0", "A1"});
    }

    public static void setITRangeMode(EntityPlayer player, int mode) {
        setSettingsGroup(player, new String[]{"A0", "A1"}, mode);
    }

    public static int getITSurroundMode(EntityPlayer player) {
        return getSettingsGroup(player, new String[]{"C0", "C1"});
    }

    public static void setITSurroundMode(EntityPlayer player, int mode) {
        setSettingsGroup(player, new String[]{"C0", "C1"}, mode);
    }
}
