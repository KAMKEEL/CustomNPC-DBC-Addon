package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcs.controllers.data.ability.AbilityPhase;
import kamkeel.npcs.controllers.data.ability.BuiltInAbility;
import kamkeel.npcs.controllers.data.ability.UserType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

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
     * Check if this toggle is currently active for the player.
     * @param player The player to check
     * @return true if the toggle is currently enabled
     */
    public abstract boolean isActive(EntityPlayer player);
}
