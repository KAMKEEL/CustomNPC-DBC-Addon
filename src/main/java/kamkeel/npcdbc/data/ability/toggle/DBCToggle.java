package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.constants.DBCSettings;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.util.function.*;

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
    FRIENDLY_FIST(new DBCToggleBuilder("friendly_fist", DBCSettings.FRIENDLY_FIST)
        .setDisplayName("Friendly Fist")
        .setIconTexture("npcdbc:textures/gui/ability/friendly_fist_off.png")
        .setStateIconTextures("npcdbc:textures/gui/ability/friendly_fist_on.png")
    ),
    SWOOP(new DBCToggleBuilder("swoop", DBCSettings.DODGE_ENABLED)
        .setDisplayName("Swoop")
        .setIconTexture("npcdbc:textures/gui/ability/swoop.png")
    ),
    KAIOKEN(new DBCToggleBuilder("kaioken", DBCSettings.KAIOKEN_ENABLED)
        .setDisplayName("Kaioken")
        .setIconTexture("npcdbc:textures/gui/ability/kaioken.png")
        .onStateChanged((p, i) ->  {
            DBCSettingsUtil.setKaioken(p, i > 0);
            DBCSettingsUtil.setUI(p, false);
            DBCSettingsUtil.setGOD(p, false);
            DBCSettingsUtil.setPotentialUnleashed(p, false);
        })
    ),
    FUSION(new DBCToggleBuilder("fusion", DBCSettings.FUSION_ENABLED)
        .setDisplayName("Fusion")
        .setIconTexture("npcdbc:textures/gui/ability/fusion.png")
        .setWidth(64)
    ),
    KI_FIST(new DBCToggleBuilder("ki_fist", DBCSettings.KI_FIST)
        .setDisplayName("Ki Fist")
        .setIconTexture("npcdbc:textures/gui/ability/ki_fist.png")
    ),
    KI_PROTECTION(new DBCToggleBuilder("ki_protection", DBCSettings.KI_PROTECTION)
        .setDisplayName("Ki Protection")
        .setIconTexture("npcdbc:textures/gui/ability/ki_protection.png")
    ),
    KI_WEAPON(new DBCToggleBuilder("ki_weapon", DBCSettings.KI_WEAPON_TOGGLE)
        .setDisplayName("Ki Weapon")
        .setIconTexture("npcdbc:textures/gui/ability/ki_weapon_blade.png")
        .setModes(0, 1)
        .setStateLabels("Blade", "Scythe")
        .setStateIconTextures("npcdbc:textures/gui/ability/ki_weapon_blade.png", "npcdbc:textures/gui/ability/ki_weapon_scythe.png")
        .onStateChanged((p, s) -> {
            if (s <= 0) {
                DBCSettingsUtil.setKiWeapon(p, false);
            } else {
                int[] modes = {0, 1};
                int modeIndex = s - 1;

                if (modeIndex < modes.length) {
                    DBCSettingsUtil.setKiWeapon(p, true, modes[modeIndex]);
                }
            }
        })
    ),
    POTENTIAL_UNLEASHED(new DBCToggleBuilder("potential_unleashed", DBCSettings.POTENTIAL_UNLEASHED)
        .setDisplayName("Potential Unleashed")
        .setIconTexture("npcdbc:textures/gui/ability/potential_unleashed.png")
        .onStateChanged((p, i) ->  {
            DBCSettingsUtil.setPotentialUnleashed(p, i > 0);
            DBCSettingsUtil.setKaioken(p, false);
            DBCSettingsUtil.setUI(p, false);
            DBCSettingsUtil.setGOD(p, false);
        })
    ),
    ULTRA_INSTINCT(new DBCToggleBuilder("ultra_instinct", DBCSettings.ULTRA_INSTINCT)
        .setDisplayName("Ultra Instinct")
        .setIconTexture("npcdbc:textures/gui/ability/ultra_instinct.png")
        .onStateChanged((p, i) ->  {
            DBCSettingsUtil.setUI(p, i > 0);
            DBCSettingsUtil.setPotentialUnleashed(p, false);
            DBCSettingsUtil.setKaioken(p, false);
            DBCSettingsUtil.setGOD(p, false);
        })
    ),
    GOD_OF_DESTRUCTION(new DBCToggleBuilder("god_of_destruction", DBCSettings.GOD_OF_DESTRUCTION)
        .setDisplayName("God Of Destruction")
        .setIconTexture("npcdbc:textures/gui/ability/god_of_destruction.png")
        .onStateChanged((p, i) ->  {
            DBCSettingsUtil.setGOD(p, i > 0);
            DBCSettingsUtil.setUI(p, false);
            DBCSettingsUtil.setPotentialUnleashed(p, false);
            DBCSettingsUtil.setKaioken(p, false);
        })
    );

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

    public final BiConsumer<EntityPlayer, Integer> onStateChanged;

    DBCToggle(DBCToggleBuilder b) {
        this.key = b.key;
        this.displayName = b.displayName;
        this.setting = b.setting;
        this.iconTexture = b.iconTexture;
        this.width = b.width;
        this.height = b.height;
        this.modes = b.modes;
        this.stateLabels = b.stateLabels;
        this.stateIconTextures = b.stateIconTextures;
        this.onStateChanged = b.onStateChanged;
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
        if (onStateChanged == null) {
            DBCSettingsUtil.setEnabled(player, setting, state > 0);
            return;
        }

        onStateChanged.accept(player, state);
    }
}
