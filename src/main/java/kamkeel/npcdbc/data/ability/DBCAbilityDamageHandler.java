package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.data.DBCDamageCalc;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.IAbilityDamageHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.entity.EntityNPCInterface;

/**
 * Routes ability damage through the DBC damage system when DBC stats
 * are enabled on the ability. Follows the same damage pipeline as
 * {@code MixinDBCAddon.doDBCDamage()} for NPC melee attacks:
 * <ol>
 *   <li>Calculate DBC damage via {@code DBCUtils.calculateDBCStatDamage()} using IDBCStats</li>
 *   <li>Fire {@code DBCPlayerEvent.DamagedEvent} for script hooks</li>
 *   <li>Apply damage via {@code DBCUtils.doDBCDamage()}</li>
 * </ol>
 */
public class DBCAbilityDamageHandler implements IAbilityDamageHandler {

    @Override
    public boolean handleDamage(Ability ability, EntityLivingBase caster, EntityLivingBase target,
                                float damage, float knockback, float knockbackUp,
                                double knockbackDirX, double knockbackDirZ) {
        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);
        if (!stats.isEnabled()) {
            return false; // Not handled, fall through to default damage
        }

        // Build the damage source based on caster type
        DamageSource source;
        if (caster instanceof EntityNPCInterface) {
            source = new NpcDamageSource("mob", (EntityNPCInterface) caster);
        } else if (caster instanceof EntityPlayer) {
            source = DamageSource.causePlayerDamage((EntityPlayer) caster);
        } else {
            source = DamageSource.causeMobDamage(caster);
        }

        if (target instanceof EntityPlayer) {
            // Player target: full DBC damage pipeline (same as MixinDBCAddon.doDBCDamage)
            applyDBCDamageToPlayer((EntityPlayer) target, damage, stats, source);
        } else if (target instanceof EntityNPCInterface) {
            // NPC target: set npcLastSetDamage for the Mixin to pick up
            DBCUtils.npcLastSetDamage = damage;
            target.attackEntityFrom(source, damage);
        } else {
            // Other entities: direct damage
            target.attackEntityFrom(source, damage);
        }

        return true; // Handled
    }

    /**
     * Apply damage to a player through the DBC damage system.
     * Mirrors the exact flow of MixinDBCAddon.doDBCDamage():
     * 1. calculateDBCStatDamage with IDBCStats overrides
     * 2. Fire DamagedEvent for script hooks
     * 3. Apply via doDBCDamage with friendly fist support
     */
    private void applyDBCDamageToPlayer(EntityPlayer player, float damage, DBCAbilityStats stats, DamageSource source) {
        // Step 1: Calculate DBC damage using the ability's stat overrides
        DBCDamageCalc damageCalc = DBCUtils.calculateDBCStatDamage(player, (int) damage, stats, source);

        // Step 2: Fire DamagedEvent for script hooks
        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(
            player, damageCalc, source, DBCDamageSource.NPC
        );
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            return; // Event cancelled
        }

        // Step 3: Apply event modifications
        damageCalc.damage = damagedEvent.damage;
        damageCalc.stamina = damagedEvent.getStaminaReduced();
        damageCalc.ki = damagedEvent.getKiReduced();
        damageCalc.ko = damagedEvent.getFinalKO();

        // Step 4: Store for doDBCDamage to retrieve and process extras
        DBCUtils.lastSetDamage = damageCalc;
        damageCalc.processExtras();

        // Step 5: Apply final damage through DBC's HP system
        DBCUtils.doDBCDamage(player, damageCalc.damage, stats, source);
    }
}
