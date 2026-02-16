package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.data.ability.AbilityIconData;
import kamkeel.npcs.controllers.data.ability.AbilityPhase;
import kamkeel.npcs.controllers.data.ability.BuiltInAbility;
import kamkeel.npcs.controllers.data.ability.UserType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * Base class for DBC toggle abilities (Ki Fist, Swoop, etc.).
 * These are PLAYER ONLY abilities that instantly toggle a DBC setting.
 * They have no windup, no active duration - just flip the toggle and complete.
 */
public abstract class DBCToggleAbility extends BuiltInAbility {

    public DBCToggleAbility(String registryKey) {
        super(registryKey);
        this.windUpTicks = 0;
        this.cooldownTicks = 0;
        this.showTelegraph = false;
        this.allowedBy = UserType.PLAYER_ONLY;
        this.setIconTexture(CustomNpcPlusDBC.ID + ":textures/gui/ability_icons.png");
        this.setIconWidth(48);
        this.setIconHeight(48);
        this.setIconScale(1.5f);
    }

    @Override
    public int getActiveDurationTicks() {
        return 0; // Instant - toggle and complete
    }

    @Override
    public boolean hasDamage() {
        return false;
    }

    @Override
    public void onExecute(EntityLivingBase caster, EntityLivingBase target) {
        if (caster instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) caster;
            onToggle(player);
        }
        // Immediately complete
        this.phase = AbilityPhase.IDLE;
    }

    @Override
    public void onActiveTick(EntityLivingBase caster, EntityLivingBase target, int tick) {
        // Nothing - toggle abilities complete instantly
    }

    /**
     * Called when the ability is activated. Subclasses should toggle their setting here.
     * @param player The player using the ability
     */
    protected abstract void onToggle(EntityPlayer player);

    /**
     * Public entry point for toggling from external callers (e.g. ability wheel).
     * Directly toggles without going through the ability execution system.
     */
    public void toggle(EntityPlayer player) {
        onToggle(player);
    }

    /**
     * Check if this toggle is currently active for the player.
     * @param player The player to check
     * @return true if the toggle is currently enabled
     */
    public abstract boolean isActive(EntityPlayer player);

    private NBTTagCompound iconComp() {
        if (!customData.hasKey(AbilityIconData.NBT_KEY, Constants.NBT.TAG_COMPOUND))
            customData.setTag(AbilityIconData.NBT_KEY, new NBTTagCompound());

        return customData.getCompoundTag(AbilityIconData.NBT_KEY);
    }

    public String getIconTexture() {
        return iconComp().getString("Texture");
    }

    public void setIconTexture(String texture) {
        iconComp().setString("Texture", texture);
    }

    public int getIconX() {
        return iconComp().getInteger("IconX");
    }

    public void setIconX(int x) {
        iconComp().setInteger("IconX", x);
    }

    public int getIconY() {
        return iconComp().getInteger("IconY");
    }

    public void setIconY(int y) {
        iconComp().setInteger("IconY", y);
    }

    public int getIconWidth() {
        return iconComp().getInteger("Width");
    }

    public void setIconWidth(int width) {
        iconComp().setInteger("Width", Math.max(32, width));
    }

    public int getIconHeight() {
        return iconComp().getInteger("Height");
    }

    public void setIconHeight(int height) {
        iconComp().setInteger("Height", Math.max(32, height));
    }

    public float getIconScale() {
        return iconComp().getFloat("Scale");
    }

    public void setIconScale(float scale) {
        if (scale <= 0) scale = 1.0f;
        iconComp().setFloat("Scale", scale);
    }
}
