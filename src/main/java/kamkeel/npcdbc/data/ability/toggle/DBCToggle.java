package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.constants.DBCSettings;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Enum registry of all DBC toggle ability definitions.
 * Each entry defines a single immutable toggle that maps to one DBC setting.
 * <p>
 * Simple toggles have {@code toggleStates=1} (binary on/off).
 * Multi-state toggles (e.g. KI_WEAPON) cycle through multiple modes.
 * <p>
 * Adding a new toggle = adding one enum entry + one register line in DBCAbilities.
 */
public enum DBCToggle {
    FRIENDLY_FIST("friendly_fist", "Friendly Fist", DBCSettings.FRIENDLY_FIST, 0, 0),
    SWOOP("swoop", "Swoop", DBCSettings.DODGE_ENABLED, 48, 0),
    KAIOKEN("kaioken", "Kaioken", DBCSettings.KAIOKEN_ENABLED, 0, 48),
    FUSION("fusion", "Fusion", DBCSettings.FUSION_ENABLED, 144, 0, 64, 48),
    KI_FIST("ki_fist", "Ki Fist", DBCSettings.KI_FIST, 192, 0),
    KI_PROTECTION("ki_protection", "Ki Protection", DBCSettings.KI_PROTECTION, 240, 0),
    KI_WEAPON("ki_weapon", "Ki Weapon", DBCSettings.KI_WEAPON_TOGGLE, 288, 0, 48, 48,
        new int[]{0, 1}, new String[]{"Ki Blade", "Ki Scythe"},
        new int[][]{{288, 0}, {336, 0}}),
    POTENTIAL_UNLEASHED("potential_unleashed", "Potential Unleashed", DBCSettings.POTENTIAL_UNLEASHED, 48, 48),
    ULTRA_INSTINCT("ultra_instinct", "Ultra Instinct", DBCSettings.ULTRA_INSTINCT, 96, 48),
    GOD_OF_DESTRUCTION("god_of_destruction", "God of Destruction", DBCSettings.GOD_OF_DESTRUCTION, 144, 48);

    public final String key;
    public final String displayName;
    public final int setting;
    public final int iconX;
    public final int iconY;
    public final int width;
    public final int height;

    /** DBC modes per state (null for simple binary toggles). */
    public final int[] modes;

    /** Display labels per state (null for simple binary toggles). */
    public final String[] stateLabels;

    /** Per-state icon UV overrides as {iconX, iconY} pairs (null for simple toggles). */
    public final int[][] stateIcons;

    DBCToggle(String key, String displayName, int setting, int iconX, int iconY) {
        this(key, displayName, setting, iconX, iconY, 48, 48, null, null, null);
    }

    /** Simple binary toggle (1 state = on/off). */
    DBCToggle(String key, String displayName, int setting, int iconX, int iconY, int width, int height) {
        this(key, displayName, setting, iconX, iconY, width, height, null, null, null);
    }

    /** Multi-state toggle with per-state DBC modes, labels, and icon overrides. */
    DBCToggle(String key, String displayName, int setting, int iconX, int iconY, int width, int height,
              int[] modes, String[] stateLabels, int[][] stateIcons) {
        this.key = key;
        this.displayName = displayName;
        this.setting = setting;
        this.iconX = iconX;
        this.iconY = iconY;
        this.width = width;
        this.height = height;
        this.modes = modes;
        this.stateLabels = stateLabels;
        this.stateIcons = stateIcons;
    }

    /**
     * Number of toggle states. 1 = binary on/off, 2+ = multi-state cycling.
     */
    public int getToggleStates() {
        return modes != null ? modes.length : 1;
    }

    /**
     * Apply a toggle state to the player's DBC settings.
     * @param state 0 = off, 1+ = active state (1-indexed)
     */
    public void applyState(EntityPlayer player, int state) {
        if (state <= 0) {
            // Turn off
            if (modes != null) {
                DBCSettingsUtil.setKiWeapon(player, false);
            } else {
                DBCSettingsUtil.setEnabled(player, setting, false);
            }
        } else if (modes != null) {
            // Multi-state: map 1-indexed state to DBC mode
            int modeIndex = state - 1;
            if (modeIndex < modes.length) {
                DBCSettingsUtil.setKiWeapon(player, true, modes[modeIndex]);
            }
        } else {
            // Simple toggle: turn on
            DBCSettingsUtil.setEnabled(player, setting, true);
        }
    }

    /**
     * Get the current active state from the player's DBC settings.
     * @return 0 if off, 1+ for active state (1-indexed)
     */
    public int getActiveState(EntityPlayer player) {
        if (modes != null) {
            int currentMode = DBCSettingsUtil.getKiWeapon(player);
            if (currentMode < 0) return 0;
            for (int i = 0; i < modes.length; i++) {
                if (modes[i] == currentMode) return i + 1;
            }
            return 0;
        }
        return DBCSettingsUtil.isEnabled(player, setting) ? 1 : 0;
    }

    public boolean isActive(EntityPlayer player) {
        return getActiveState(player) > 0;
    }
}
