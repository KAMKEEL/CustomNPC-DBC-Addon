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
    FRIENDLY_FIST("friendly_fist", "Friendly Fist", DBCSettings.FRIENDLY_FIST,
        "npcdbc:textures/gui/ability/friendly_fist.png"),
    SWOOP("swoop", "Swoop", DBCSettings.DODGE_ENABLED,
        "npcdbc:textures/gui/ability/swoop.png"),
    KAIOKEN("kaioken", "Kaioken", DBCSettings.KAIOKEN_ENABLED,
        "npcdbc:textures/gui/ability/kaioken.png"),
    FUSION("fusion", "Fusion", DBCSettings.FUSION_ENABLED,
        "npcdbc:textures/gui/ability/fusion.png", 64, 48),
    KI_FIST("ki_fist", "Ki Fist", DBCSettings.KI_FIST,
        "npcdbc:textures/gui/ability/ki_fist.png"),
    KI_PROTECTION("ki_protection", "Ki Protection", DBCSettings.KI_PROTECTION,
        "npcdbc:textures/gui/ability/ki_protection.png"),
    KI_WEAPON("ki_weapon", "Ki Weapon", DBCSettings.KI_WEAPON_TOGGLE,
        "npcdbc:textures/gui/ability/ki_weapon.png", 48, 48,
        new int[]{0, 1}, new String[]{"Ki Blade", "Ki Scythe"},
        new String[]{"npcdbc:textures/gui/ability/ki_weapon_blade.png", "npcdbc:textures/gui/ability/ki_weapon_scythe.png"}),
    POTENTIAL_UNLEASHED("potential_unleashed", "Potential Unleashed", DBCSettings.POTENTIAL_UNLEASHED,
        "npcdbc:textures/gui/ability/potential_unleashed.png"),
    ULTRA_INSTINCT("ultra_instinct", "Ultra Instinct", DBCSettings.ULTRA_INSTINCT,
        "npcdbc:textures/gui/ability/ultra_instinct.png"),
    GOD_OF_DESTRUCTION("god_of_destruction", "God of Destruction", DBCSettings.GOD_OF_DESTRUCTION,
        "npcdbc:textures/gui/ability/god_of_destruction.png");

    public final String key;
    public final String displayName;
    public final int setting;
    public final String iconTexture;
    public final int width;
    public final int height;

    /** DBC modes per state (null for simple binary toggles). */
    public final int[] modes;

    /** Display labels per state (null for simple binary toggles). */
    public final String[] stateLabels;

    /** Per-state icon textures (null for simple toggles). */
    public final String[] stateIconTextures;

    /** Simple binary toggle with default 48x48 icon. */
    DBCToggle(String key, String displayName, int setting, String iconTexture) {
        this(key, displayName, setting, iconTexture, 48, 48, null, null, null);
    }

    /** Simple binary toggle with custom icon dimensions. */
    DBCToggle(String key, String displayName, int setting, String iconTexture, int width, int height) {
        this(key, displayName, setting, iconTexture, width, height, null, null, null);
    }

    /** Multi-state toggle with per-state DBC modes, labels, and icon textures. */
    DBCToggle(String key, String displayName, int setting, String iconTexture, int width, int height,
              int[] modes, String[] stateLabels, String[] stateIconTextures) {
        this.key = key;
        this.displayName = displayName;
        this.setting = setting;
        this.iconTexture = iconTexture;
        this.width = width;
        this.height = height;
        this.modes = modes;
        this.stateLabels = stateLabels;
        this.stateIconTextures = stateIconTextures;
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
