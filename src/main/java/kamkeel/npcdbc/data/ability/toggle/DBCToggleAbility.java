package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.data.ability.AbilityIconData;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.UserType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/**
 * Concrete toggle ability parameterized by {@link DBCToggle} enum.
 * Immutable built-in — each instance maps to exactly one DBC setting.
 * <p>
 * All DBC state changes go through {@link #onToggleStateChanged} which delegates
 * to {@link DBCToggle#applyState}. This handles both simple (on/off) and
 * multi-state (cycling) toggles uniformly.
 */
public class DBCToggleAbility extends Ability {
    private final DBCToggle toggle;

    public DBCToggleAbility(DBCToggle toggle) {
        configureAsBuiltIn("npcdbc:" + toggle.key);
        this.name = toggle.displayName;
        this.toggleStates = toggle.getToggleStates();
        this.allowedBy = UserType.PLAYER_ONLY;
        this.toggle = toggle;

        // Set state labels for multi-state toggles
        if (toggle.stateLabels != null) {
            this.setToggleStateLabels(toggle.stateLabels);
        }

        this.setIconTexture(CustomNpcPlusDBC.ID + ":textures/gui/ability_icons.png");
        this.setIconWidth(48);
        this.setIconHeight(48);
        this.setIconScale(1.5f);
        this.setIconX(toggle.iconX);
        this.setIconY(toggle.iconY);

        // Set per-state icon overrides for multi-state toggles
        if (toggle.stateIcons != null) {
            setStateIcons(toggle.stateIcons);
        }
    }

    public DBCToggle getToggle() {
        return toggle;
    }

    @Override
    public boolean hasDamage() {
        return false;
    }

    // ═══════════════════════════════════════════════════════════════════
    // TOGGLE — all state changes routed through onToggleStateChanged
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public void onToggleStateChanged(EntityLivingBase caster, int oldState, int newState) {
        if (caster instanceof EntityPlayer)
            toggle.applyState((EntityPlayer) caster, newState);
    }

    // ═══════════════════════════════════════════════════════════════════
    // EXECUTION — blocked; toggle abilities never execute
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public void start(EntityLivingBase target) {
    }

    @Override
    public void onExecute(EntityLivingBase caster, EntityLivingBase target) {
    }

    @Override
    public void onActiveTick(EntityLivingBase caster, EntityLivingBase target, int tick) {
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
    }

    // ═══════════════════════════════════════════════════════════════════
    // ICON HELPERS
    // ═══════════════════════════════════════════════════════════════════

    private NBTTagCompound iconComp() {
        if (!customData.hasKey(AbilityIconData.NBT_KEY, Constants.NBT.TAG_COMPOUND))
            customData.setTag(AbilityIconData.NBT_KEY, new NBTTagCompound());
        return customData.getCompoundTag(AbilityIconData.NBT_KEY);
    }

    public String getIconTexture() {
        return iconComp().getString("Texture");
    }

    public void setIconTexture(String t) {
        iconComp().setString("Texture", t);
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

    public void setIconWidth(int w) {
        iconComp().setInteger("Width", Math.max(32, w));
    }

    public int getIconHeight() {
        return iconComp().getInteger("Height");
    }

    public void setIconHeight(int h) {
        iconComp().setInteger("Height", Math.max(32, h));
    }

    public float getIconScale() {
        return iconComp().getFloat("Scale");
    }

    public void setIconScale(float s) {
        iconComp().setFloat("Scale", s <= 0 ? 1.0f : s);
    }

    public void setStateIcons(int[][] stateIcons) {
        NBTTagCompound ic = iconComp();
        if (stateIcons != null && stateIcons.length > 0) {
            NBTTagList list = new NBTTagList();
            for (int[] pair : stateIcons) {
                NBTTagCompound comp = new NBTTagCompound();
                comp.setInteger("IconX", pair[0]);
                comp.setInteger("IconY", pair[1]);
                list.appendTag(comp);
            }
            ic.setTag("StateIcons", list);
        }
    }
}
