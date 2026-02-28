package kamkeel.npcdbc.data.ability.toggle;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.CustomNpcs;

import java.util.function.*;

public class DBCToggleBuilder {
    public final String key;
    public final int setting;
    public String displayName = "DBC Toggle";
    public String iconTexture = "customnpcs:textures/gui/ability_fallback.png";
    public int width = 48;
    public int height = 48;

    /** DBC modes per state (null for simple binary toggles). */
    public int[] modes = null;

    /** Display labels per state (null for simple binary toggles). */
    public String[] stateLabels = null;

    /** Per-state icon textures (null for simple toggles). */
    public String[] stateIconTextures = null;

    public BiConsumer<EntityPlayer, Integer> onStateChanged = null;

    public DBCToggleBuilder(String key, int setting) {
        this.key = key;
        this.setting = setting;
    }

    public DBCToggleBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public DBCToggleBuilder setIconTexture(String iconTexture) {
        this.iconTexture = iconTexture;
        return this;
    }

    public DBCToggleBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    public DBCToggleBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public DBCToggleBuilder setModes(int... modes) {
        this.modes = modes;
        return this;
    }

    public DBCToggleBuilder setStateLabels(String... stateLabels) {
        this.stateLabels = stateLabels;
        return this;
    }

    public DBCToggleBuilder setStateIconTextures(String... stateIconTextures) {
        this.stateIconTextures = stateIconTextures;
        return this;
    }

    public DBCToggleBuilder onStateChanged(BiConsumer<EntityPlayer, Integer> onStateChanged) {
        this.onStateChanged = onStateChanged;
        return this;
    }
}
