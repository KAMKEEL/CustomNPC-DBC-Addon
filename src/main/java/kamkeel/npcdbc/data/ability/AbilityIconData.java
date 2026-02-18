package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.ChainedAbility;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Icon data for abilities, stored in the ability's customData NBT.
 * Used to render ability icons in the Ability Wheel and Hotbar.
 */
public class AbilityIconData {
    public static final String NBT_KEY = "AbilityIcon";

    private final NBTTagCompound customData;

    // Icon properties
    public String texture = "";
    public int iconX = 0;
    public int iconY = 0;
    public int width = 32;
    public int height = 32;
    public float scale = 1.0f;

    private AbilityIconData(NBTTagCompound customData) {
        this.customData = customData;
    }

    /**
     * Create an AbilityIconData instance from a raw customData compound.
     * Reads existing values if present, otherwise uses defaults.
     */
    public static AbilityIconData fromCustomData(NBTTagCompound customData) {
        AbilityIconData icon = new AbilityIconData(customData);
        if (customData.hasKey(NBT_KEY)) {
            icon.readFromNBT(customData.getCompoundTag(NBT_KEY));
        }
        return icon;
    }

    /**
     * Create an AbilityIconData instance from an ability's customData.
     */
    public static AbilityIconData fromAbility(Ability ability) {
        return fromCustomData(ability.getCustomData());
    }

    /**
     * Create an AbilityIconData instance from a chained ability's customData.
     */
    public static AbilityIconData fromChainedAbility(ChainedAbility chain) {
        return fromCustomData(chain.getCustomData());
    }

    /**
     * Write current state back to the customData NBT.
     */
    public void save() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        customData.setTag(NBT_KEY, tag);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("Texture", texture);
        nbt.setInteger("IconX", iconX);
        nbt.setInteger("IconY", iconY);
        nbt.setInteger("Width", width);
        nbt.setInteger("Height", height);
        nbt.setFloat("Scale", scale);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        texture = nbt.getString("Texture");
        iconX = nbt.getInteger("IconX");
        iconY = nbt.getInteger("IconY");
        width = nbt.getInteger("Width");
        if (width <= 0) width = 32;
        height = nbt.getInteger("Height");
        if (height <= 0) height = 32;
        scale = nbt.getFloat("Scale");
        if (scale <= 0) scale = 1.0f;
    }

    /**
     * Check if this icon has a valid texture configured.
     */
    public boolean hasTexture() {
        return texture != null && !texture.isEmpty();
    }

    // ═══════════════════════════════════════════════════════════════════
    // GETTERS AND SETTERS (with auto-save)
    // ═══════════════════════════════════════════════════════════════════

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture != null ? texture : "";
        save();
    }

    public int getIconX() {
        return iconX;
    }

    public void setIconX(int iconX) {
        this.iconX = Math.max(0, iconX);
        save();
    }

    public int getIconY() {
        return iconY;
    }

    public void setIconY(int iconY) {
        this.iconY = Math.max(0, iconY);
        save();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = Math.max(1, width);
        save();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = Math.max(1, height);
        save();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = Math.max(0.1f, scale);
        save();
    }
}
