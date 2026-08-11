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
import kamkeel.npcs.controllers.AttributeController;
import kamkeel.npcs.controllers.data.attribute.tracker.PlayerAttributeTracker;
import kamkeel.npcs.util.AttributeAttackUtil;
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

            boolean isAbilityDamage = DBCUtils.npcLastSetDamage != null;
            if (DBCUtils.npcLastSetDamage != null) {
                dam.set(DBCUtils.npcLastSetDamage); // THIS GETS DEDUCTED FROM NPC HEALTH
                DBCUtils.npcLastSetDamage = null;
            }

            // Apply attribute + magic pipeline for regular player hits (not ability/energy).
            // CNPC+ disables its own attribute handling when DBC Addon is loaded,
            // so we must apply it here for Player→NPC regular attacks.
            if (!isAbilityDamage) {
                EntityLivingBase attacker = resolveAttacker(source);
                if (attacker instanceof EntityPlayer) {
                    dam.set(AttributeAttackUtil.calculateDamagePlayerToNPC(
                        (EntityPlayer) attacker, npc, dam.get()));
                }
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
                        // Pass the original DamageSource so each ability's isValidDamageSource filter
                        // decides correctly: Guard accepts all types, Counter/Dodge reject non-melee.
                        if (defend instanceof AbilityDodge || defend instanceof AbilityCounter) {
                            float result = defend.onDefend(attacker, source, dam.get());
                            if (result != dam.get()) {
                                ci.cancel();
                                return;
                            }
                        } else {
                            // Guard: reduce DBC damage
                            dam.set(defend.onDefend(attacker, source, dam.get()));
                        }
                    }
                }
            }

            // DBC bypasses EntityNPCInterface.damageEntity() by calling setHealth() directly,
            // so the NPC's combat handler is never notified. Manually notify it here so that
            // ability interrupts, aggressor tracking, and hit-count conditions work with DBC damage.
            npc.combatHandler.damage(source, dam.get());
        } else if (!(targetEntity instanceof EntityPlayer)) {
            // Vanilla/modded mobs: apply player's outgoing attribute bonuses.
            // CustomNPC+ disables its own attribute handling when DBC Addon is loaded,
            // so we must apply it here for non-NPC, non-Player targets.
            EntityLivingBase attacker = resolveAttacker(source);
            if (attacker instanceof EntityPlayer) {
                EntityPlayer attackPlayer = (EntityPlayer) attacker;
                if (DBCUtils.entityLastSetDamage != null) {
                    // Use the pre-calculated value (includes crit) from the LivingAttackEvent
                    dam.set(DBCUtils.entityLastSetDamage);
                    DBCUtils.entityLastSetDamage = null;
                } else {
                    // Fallback for attacks that bypass LivingAttackEvent (e.g. ki blasts)
                    float outgoing = AttributeAttackUtil.calculateOutgoing(attackPlayer, dam.get());
                    PlayerAttributeTracker tracker = AttributeController.getTracker(attackPlayer);
                    if (tracker != null)
                        outgoing = AttributeAttackUtil.applyCrit(outgoing, tracker);
                    dam.set(outgoing);
                }
            } else {
                // Non-player attacker: clear any stale value, don't apply attributes
                DBCUtils.entityLastSetDamage = null;
            }
        }
    }

    /**
     * Shared logic for both player and non-player DBC damage to a player target.
     * Calculates DBC damage, applies Guard, fires DamagedEvent, and processes extras.
     *
     * @return true if the event was cancelled (caller should cancel the mixin CI)
     */
    private boolean handleDBCPlayerDamage(EntityPlayer target, float damage, DamageSource source, CallbackInfo ci) {
        if (DBCUtils.abilityDamageHandled) {
            ci.cancel();
            return true;
        }

        int dbcDamageSource = DBCDamageSource.UNKNOWN;
        if (source.getEntity() instanceof EntityPlayer) {
            dbcDamageSource = DBCDamageSource.PLAYER;
        } else if (source.getEntity() instanceof EntityEnergyAtt) {
            dbcDamageSource = DBCDamageSource.KIATTACK;
        }

        DBCDamageCalc damageCalc = DBCUtils.calculateDBCDamageFromSource(target, damage, source);

        // Guard: reduce DBC damage for players
        PlayerData pData = PlayerData.get(target);
        if (pData != null && pData.abilityData != null) {
            AbilityDefend defend = pData.abilityData.getActiveDefend();
            if (defend instanceof AbilityGuard) {
                EntityLivingBase attacker = resolveAttacker(source);
                if (attacker != null) {
                    damageCalc.damage = defend.onDefend(attacker, source, damageCalc.damage);
                }
            }
        }

        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(target, damageCalc, source, dbcDamageSource);
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            ci.cancel();
            return true;
        }

        damageCalc.damage = damagedEvent.damage;
        damageCalc.stamina = damagedEvent.getStaminaReduced();
        damageCalc.ki = damagedEvent.getKiReduced();
        damageCalc.ko = damagedEvent.getFinalKO();
        DBCUtils.lastSetDamage = damageCalc;
        damageCalc.processExtras();
        return false;
    }

    /**
     * fixDamagedEventDBCDamage writes the fully scaled DBC damage into the NPC's damage local before
     * the hurt event is fired, so DBC would compute its ki costs from an already-scaled number. The
     * costs that scale with damage - Ki Infuse on projectiles above all - came out several times too
     * high as a result. The NPC's actual damage comes from npcLastSetDamage in NPCDamaged, so handing
     * DBC the unmodified amount here changes only its own bookkeeping.
     */
    @Inject(method = "Sd35MR", at = @At("HEAD"))
    public void restoreRawAmountForNPCTarget(LivingHurtEvent event, CallbackInfo ci) {
        if (!DBCUtils.insideAttackEntityFrom || DBCUtils.rawIncomingAmount == null)
            return;
        if (!(event.entityLiving instanceof EntityNPCInterface) || DBCUtils.npcLastSetDamage == null)
            return;

        event.ammount = DBCUtils.rawIncomingAmount;
    }

    @Inject(method = "Sd35MR", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;a1t3(Lnet/minecraft/entity/player/EntityPlayer;)V", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    public void dbcAttackFromPlayer(LivingHurtEvent event, CallbackInfo ci, @Local(name = "dam") LocalFloatRef dam, @Local(name = "targetPlayer") LocalRef<EntityPlayer> targetPlayer, @Local(name = "source") LocalRef<DamageSource> damageSource) {
        handleDBCPlayerDamage(targetPlayer.get(), dam.get(), damageSource.get(), ci);
    }

    @Inject(method = "Sd35MR", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;a1t3(Lnet/minecraft/entity/player/EntityPlayer;)V", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    public void dbcAttackFromNonPlayer(LivingHurtEvent event, CallbackInfo ci, @Local(name = "amount") LocalFloatRef dam, @Local(name = "targetPlayer") LocalRef<EntityPlayer> targetPlayer, @Local(name = "source") LocalRef<DamageSource> damageSource) {
        handleDBCPlayerDamage(targetPlayer.get(), dam.get(), damageSource.get(), ci);
    }
}
