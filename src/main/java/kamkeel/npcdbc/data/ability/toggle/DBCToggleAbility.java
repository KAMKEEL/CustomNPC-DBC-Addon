package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.enums.UserType;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Concrete toggle ability parameterized by {@link DBCToggle} enum.
 * Immutable built-in — each instance maps to exactly one DBC setting.
 * <p>
 * All DBC state changes go through {@link #onToggle} which delegates
 * to {@link DBCToggle#applyState}. This handles both simple (on/off) and
 * multi-state (cycling) toggles uniformly.
 */
public class DBCToggleAbility extends Ability {
    private final DBCToggle toggle;

    public DBCToggleAbility(DBCToggle toggle) {
        configureAsBuiltIn("npcdbc:" + toggle.key);
        this.name = toggle.displayName.replace(" ", "_");
        this.displayName = toggle.displayName;
        this.toggleStates = toggle.getToggleStates();
        this.telegraphType = TelegraphType.NONE;
        this.showTelegraph = false;
        this.allowedBy = UserType.PLAYER_ONLY;
        this.toggle = toggle;

        // Set state labels for multi-state toggles
        if (toggle.stateLabels != null) {
            this.setToggleStateLabels(toggle.stateLabels);
        }

        // Default icon via the base Ability layer system
        this.defaultIconWidth = toggle.width;
        this.defaultIconHeight = toggle.height;
        this.defaultIconLayers = new DefaultIconLayer[]{
            new DefaultIconLayer(toggle.iconTexture, toggle.stateIconTextures, null)
        };
    }

    public DBCToggle getToggle() {
        return toggle;
    }

    @Override
    public boolean hasDamage() {
        return false;
    }

    @Override
    public boolean isConcurrentCapable() {
        return true;
    }

    // ═══════════════════════════════════════════════════════════════════
    // TOGGLE — all state changes routed through onToggle
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public void onToggle(EntityLivingBase caster, int oldState, int newState) {
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
}
