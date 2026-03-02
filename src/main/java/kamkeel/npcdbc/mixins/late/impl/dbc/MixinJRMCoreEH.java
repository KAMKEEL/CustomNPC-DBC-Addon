package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreEH;
import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcs.controllers.data.ability.type.AbilityCounter;
import kamkeel.npcs.controllers.data.ability.type.AbilityDefend;
import kamkeel.npcs.controllers.data.ability.type.AbilityDodge;
import kamkeel.npcs.controllers.data.ability.type.AbilityGuard;
import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.data.DBCDamageCalc;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCoreEH.class, remap = false)
public class MixinJRMCoreEH {

    /**
     * Resolves the attacking EntityLivingBase from a DBC DamageSource.
     * For ki attacks, source.getEntity() is EntityEnergyAtt (not EntityLivingBase),
     * so we follow shootingEntity to find the actual attacker.
     */
    private static EntityLivingBase resolveAttacker(DamageSource source) {
        Entity entity = source.getEntity();
        if (entity instanceof EntityLivingBase) {
            return (EntityLivingBase) entity;
        }
        if (entity instanceof EntityEnergyAtt) {
            Entity shooter = ((EntityEnergyAtt) entity).shootingEntity;
            if (shooter instanceof EntityLivingBase) {
                return (EntityLivingBase) shooter;
            }
        }
        return null;
    }
    @Inject(method = "damageEntity(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;F)V", at = @At("HEAD"), cancellable = true)
    public void NPCDamaged(EntityLivingBase targetEntity, DamageSource source, float amount, CallbackInfo ci, @Local(ordinal = 0) LocalFloatRef dam) {
        if (targetEntity instanceof EntityNPCInterface) {

            EntityNPCInterface npc = (EntityNPCInterface) targetEntity;

            DBCUtils.damageEntityCalled = true;

            if (DBCUtils.npcLastSetDamage != null) {
                dam.set(DBCUtils.npcLastSetDamage); // THIS GETS DEDUCTED FROM NPC HEALTH
                DBCUtils.npcLastSetDamage = null;
            }

            Form form = PlayerDataUtil.getForm(npc);
            if (form != null) {
                float formLevel = PlayerDataUtil.getFormLevel(npc);
                if (form.mastery.hasDamageNegation()) {
                    float damage = dam.get();
                    float damageNegation = form.mastery.damageNegation * form.mastery.calculateMulti("damageNegation", formLevel);
                    float newDamage = damage * (100 - damageNegation) / 100;
                    dam.set(newDamage);
                }
            }

            // Defend abilities: only check here when DBC damage bypasses attackEntityFrom.
            // When inside attackEntityFrom, guard/dodge/counter was already applied in fixDamagedEventDBCDamage.
            if (!DBCUtils.insideAttackEntityFrom) {
                AbilityDefend defend = npc.abilities != null ? npc.abilities.getActiveDefend() : null;
                if (defend != null) {
                    EntityLivingBase attacker = resolveAttacker(source);
                    if (attacker != null) {
                        DamageSource meleeSrc = (attacker instanceof EntityPlayer)
                            ? DamageSource.causePlayerDamage((EntityPlayer) attacker)
                            : DamageSource.causeMobDamage(attacker);
                        // Dodge & Counter: cancel DBC damage entirely
                        if (defend instanceof AbilityDodge || defend instanceof AbilityCounter) {
                            float result = defend.onDefend(attacker, meleeSrc, dam.get());
                            if (result != dam.get()) {
                                ci.cancel();
                                return;
                            }
                        } else {
                            // Guard: reduce DBC damage
                            dam.set(defend.onDefend(attacker, meleeSrc, dam.get()));
                        }
                    }
                }
            }

            // DBC bypasses EntityNPCInterface.damageEntity() by calling setHealth() directly,
            // so the NPC's combat handler is never notified. Manually notify it here so that
            // ability interrupts, aggressor tracking, and hit-count conditions work with DBC damage.
            npc.combatHandler.damage(source, dam.get());
        }
    }

    @Inject(method = "Sd35MR", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;a1t3(Lnet/minecraft/entity/player/EntityPlayer;)V", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    public void dbcAttackFromPlayer(LivingHurtEvent event, CallbackInfo ci, @Local(name = "dam") LocalFloatRef dam, @Local(name = "targetPlayer") LocalRef<EntityPlayer> targetPlayer, @Local(name = "source") LocalRef<DamageSource> damageSource) {
        if (DBCUtils.abilityDamageHandled) {
            ci.cancel();
            return;
        }

        // Check for Damage Source Type
        DamageSource source = damageSource.get();
        int dbcDamageSource = DBCDamageSource.UNKNOWN;
        if (source.getEntity() instanceof EntityPlayer) {
            dbcDamageSource = DBCDamageSource.PLAYER;
        } else if (source.getEntity() instanceof EntityEnergyAtt) {
            dbcDamageSource = DBCDamageSource.KIATTACK;
        }

        DBCDamageCalc damageCalc = DBCUtils.calculateDBCDamageFromSource(targetPlayer.get(), dam.get(), source);

        // Guard: reduce DBC damage for players.
        // Use a clean melee DamageSource so AbilityDefend's physical-only filter passes,
        // and resolve the actual attacker through EntityEnergyAtt.shootingEntity for ki attacks.
        PlayerData pData = PlayerData.get(targetPlayer.get());
        if (pData != null && pData.abilityData != null) {
            AbilityDefend defend = pData.abilityData.getActiveDefend();
            if (defend instanceof AbilityGuard) {
                EntityLivingBase attacker = resolveAttacker(source);
                if (attacker != null) {
                    DamageSource meleeSrc = (attacker instanceof EntityPlayer)
                        ? DamageSource.causePlayerDamage((EntityPlayer) attacker)
                        : DamageSource.causeMobDamage(attacker);
                    damageCalc.damage = defend.onDefend(attacker, meleeSrc, damageCalc.damage);
                }
            }
        }

        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(targetPlayer.get(), damageCalc, source, dbcDamageSource);
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            ci.cancel();
            return;
        }

        damageCalc.damage = damagedEvent.damage;
        damageCalc.stamina = damagedEvent.getStaminaReduced();
        damageCalc.ki = damagedEvent.getKiReduced();
        damageCalc.ko = damagedEvent.getFinalKO();
        DBCUtils.lastSetDamage = damageCalc;
        damageCalc.processExtras();
    }

    @Inject(method = "Sd35MR", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;a1t3(Lnet/minecraft/entity/player/EntityPlayer;)V", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    public void dbcAttackFromNonPlayer(LivingHurtEvent event, CallbackInfo ci, @Local(name = "amount") LocalFloatRef dam, @Local(name = "targetPlayer") LocalRef<EntityPlayer> targetPlayer, @Local(name = "source") LocalRef<DamageSource> damageSource) {
        if (DBCUtils.abilityDamageHandled) {
            ci.cancel();
            return;
        }

        // Check for Damage Source Type
        DamageSource source = damageSource.get();
        int dbcDamageSource = DBCDamageSource.UNKNOWN;
        if (source.getEntity() instanceof EntityPlayer) {
            dbcDamageSource = DBCDamageSource.PLAYER;
        } else if (source.getEntity() instanceof EntityEnergyAtt) {
            dbcDamageSource = DBCDamageSource.KIATTACK;
        }

        DBCDamageCalc damageCalc = DBCUtils.calculateDBCDamageFromSource(targetPlayer.get(), dam.get(), source);

        // Guard: reduce DBC damage for players (same approach as dbcAttackFromPlayer above)
        PlayerData pData = PlayerData.get(targetPlayer.get());
        if (pData != null && pData.abilityData != null) {
            AbilityDefend defend = pData.abilityData.getActiveDefend();
            if (defend instanceof AbilityGuard) {
                EntityLivingBase attacker = resolveAttacker(source);
                if (attacker != null) {
                    DamageSource meleeSrc = (attacker instanceof EntityPlayer)
                        ? DamageSource.causePlayerDamage((EntityPlayer) attacker)
                        : DamageSource.causeMobDamage(attacker);
                    damageCalc.damage = defend.onDefend(attacker, meleeSrc, damageCalc.damage);
                }
            }
        }

        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(targetPlayer.get(), damageCalc, source, dbcDamageSource);
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            ci.cancel();
            return;
        }

        damageCalc.damage = damagedEvent.damage;
        damageCalc.stamina = damagedEvent.getStaminaReduced();
        damageCalc.ki = damagedEvent.getKiReduced();
        damageCalc.ko = damagedEvent.getFinalKO();
        DBCUtils.lastSetDamage = damageCalc;
        damageCalc.processExtras();
    }
}
