package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.data.ability.AbilityIconData;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.UserType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

/**
 * Base class for DBC toggle abilities (Ki Fist, Swoop, etc.).
 * Pure on/off toggles for players — no execution, no ticking, no phases.
 */
public abstract class DBCToggleAbility extends Ability {

    public DBCToggleAbility(String registryKey) {
        configureAsBuiltIn(registryKey);
        this.toggleable = true;
        this.allowedBy = UserType.PLAYER_ONLY;
        this.setIconTexture(CustomNpcPlusDBC.ID + ":textures/gui/ability_icons.png");
        this.setIconWidth(48);
        this.setIconHeight(48);
        this.setIconScale(1.5f);
    }

    @Override
    public boolean hasDamage() {
        return false;
    }

    // ═══════════════════════════════════════════════════════════════════
    // TOGGLE
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public void onToggleOn(EntityLivingBase caster) {
        if (caster instanceof EntityPlayer)
            onToggle((EntityPlayer) caster, true);
    }

    @Override
    public void onToggleOff(EntityLivingBase caster) {
        if (caster instanceof EntityPlayer)
            onToggle((EntityPlayer) caster, false);
    }

    protected abstract void onToggle(EntityPlayer player, boolean newState);

    public abstract boolean isActive(EntityPlayer player);

    // ═══════════════════════════════════════════════════════════════════
    // EXECUTION — blocked; toggle abilities never execute
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public void start(EntityLivingBase target) {}

    @Override
    public void onExecute(EntityLivingBase caster, EntityLivingBase target) {}

    @Override
    public void onActiveTick(EntityLivingBase caster, EntityLivingBase target, int tick) {}

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {}

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {}

    // ═══════════════════════════════════════════════════════════════════
    // ICON HELPERS
    // ═══════════════════════════════════════════════════════════════════

    private NBTTagCompound iconComp() {
        if (!customData.hasKey(AbilityIconData.NBT_KEY, Constants.NBT.TAG_COMPOUND))
            customData.setTag(AbilityIconData.NBT_KEY, new NBTTagCompound());
        return customData.getCompoundTag(AbilityIconData.NBT_KEY);
    }

    public String getIconTexture()       { return iconComp().getString("Texture"); }
    public void setIconTexture(String t)  { iconComp().setString("Texture", t); }

    public int getIconX()                 { return iconComp().getInteger("IconX"); }
    public void setIconX(int x)           { iconComp().setInteger("IconX", x); }

    public int getIconY()                 { return iconComp().getInteger("IconY"); }
    public void setIconY(int y)           { iconComp().setInteger("IconY", y); }

    public int getIconWidth()             { return iconComp().getInteger("Width"); }
    public void setIconWidth(int w)       { iconComp().setInteger("Width", Math.max(32, w)); }

    public int getIconHeight()            { return iconComp().getInteger("Height"); }
    public void setIconHeight(int h)      { iconComp().setInteger("Height", Math.max(32, h)); }

    public float getIconScale()           { return iconComp().getFloat("Scale"); }
    public void setIconScale(float s)     { iconComp().setFloat("Scale", s <= 0 ? 1.0f : s); }
}
