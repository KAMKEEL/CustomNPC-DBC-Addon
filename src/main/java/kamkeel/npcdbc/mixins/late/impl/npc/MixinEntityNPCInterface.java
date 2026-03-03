package kamkeel.npcdbc.mixins.late.impl.npc;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import kamkeel.npcdbc.config.ConfigDBCGeneral;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.mixins.late.impl.dbc.MixinJRMCoreEH;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcs.controllers.data.ability.type.AbilityDefend;
import kamkeel.npcs.controllers.data.ability.type.AbilityGuard;
import kamkeel.npcs.util.AttributeAttackUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import noppes.npcs.DataInventory;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.event.NpcEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityNPCInterface.class)
public abstract class MixinEntityNPCInterface extends EntityCreature implements IEntityAdditionalSpawnData, ICommandSender, IRangedAttackMob, IBossDisplayData {

    @Shadow
    public DataInventory inventory;
    @Unique
    private boolean dbcAltered; //if DamagedEvent's damage was altered by a DBC player

    @Unique
    private boolean npcdbc$shouldResetHurtTime;

    private MixinEntityNPCInterface(World p_i1602_1_) {
        super(p_i1602_1_);
    }

    @Inject(method = "entityInit", at = @At("RETURN"))
    private void addWatchableObjects(CallbackInfo ci) {
        this.dataWatcher.addObject(31, Integer.valueOf(1));
    }

    /**
     * This method fires on scripting DamagedEvent creation.
     * If npc attacked by a DBC player (powerType ==1), I set dbcAltered  true,
     * then I set the DamagedEvent's damage to the player's DBC attack stat (pure damage player would do without any NPC defense calculations)
     */
    @Inject(method = "attackEntityFrom", at = @At(value = "FIELD", target = "Lnoppes/npcs/entity/EntityNPCInterface;wrappedNPC:Lnoppes/npcs/api/entity/ICustomNpc;", shift = At.Shift.BEFORE, remap = false))
    public void fixDamagedEventDBCDamage(DamageSource damagesource, float amount, CallbackInfoReturnable<Boolean> cir, @Local(name = "i") LocalFloatRef dam) {
        Entity attackerEntity = NoppesUtilServer.GetDamageSource(damagesource);

        if (attackerEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attackerEntity;
            DBCData data = DBCData.get(player);
            if (dbcAltered = data.Powertype == 1) {
                float modifiedDamage;
                if (DBCUtils.npcLastSetDamage != null) {
                    // Ability extender already calculated DBC damage; use it directly
                    modifiedDamage = DBCUtils.npcLastSetDamage;
                } else if (DBCUtils.preCalculatedAttackerDamage != null) {
                    // Reuse the DBC damage pre-calculated at HEAD of attackEntityFrom
                    modifiedDamage = DBCUtils.preCalculatedAttackerDamage;
                } else {
                    // Fallback: calculate from the clean vanilla base
                    modifiedDamage = DBCUtils.calculateAttackStat(player, amount, damagesource);
                }

                // Apply Attributes
                EntityNPCInterface npcInterface = (EntityNPCInterface) (Object) this;
                modifiedDamage = AttributeAttackUtil.calculateDamagePlayerToNPC(player, npcInterface, modifiedDamage);

                // Apply Resistances
                if (ConfigDBCGeneral.ALLOW_DBC_DAMAGE_RESISTANCE) {
                    modifiedDamage = npcInterface.stats.resistances.applyResistance(damagesource, modifiedDamage);
                }

                // Apply Guard to the FULL DBC-calculated damage (not just the vanilla base).
                // Guard already ran earlier in attackEntityFrom() for hitCount tracking,
                // but only reduced the vanilla base — negligible vs DBC stat damage.
                // Per-tick dedup in onDefend() prevents double hitCount/signalCompletion.
                AbilityDefend defend = npcInterface.abilities != null ? npcInterface.abilities.getActiveDefend() : null;
                if (defend instanceof AbilityGuard) {
                    modifiedDamage = defend.onDefend(player, damagesource, modifiedDamage);
                }

                dam.set(modifiedDamage);
            }
        }
    }

    /**
     * This method fires after the Npc's scripting DamagedEvent finishes.
     * If npc was attacked by a DBC player (dbcAltered true):
     * I fetch the new event.getDamage() in case a scripter edited the event's damaged with event.setDamage(damage)
     * (i.e their own custom defense calculations, since the event was fed the pure attack stat above)
     * and store it in npcLastSetDamage.
     * <p>
     * Then in  {@link MixinJRMCoreEH#NPCDamaged(EntityLivingBase, DamageSource, float amount, CallbackInfo, LocalFloatRef)}
     * which always fires after this in the MC LivingHurt, I set the pure damage in the LivingHurtEvent to npcLastSetDamage then I reset it to -1
     */
    @Redirect(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnoppes/npcs/scripted/event/NpcEvent$DamagedEvent;getDamage()F", remap = false))
    public float fixDamagedEventDBCDamage(NpcEvent.DamagedEvent instance) {
        if (dbcAltered && !instance.isCancelled()) {
            // Always propagate the event's damage (including scripter modifications) to npcLastSetDamage
            DBCUtils.npcLastSetDamage = instance.getDamage();
        }
        dbcAltered = false;
        return instance.getDamage();
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    public void resetDamageEntityCalled(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        npcdbc$shouldResetHurtTime = false;
        DBCUtils.preCalculatedAttackerDamage = null;
        Entity attackerEntity = NoppesUtilServer.GetDamageSource(source);
        if (attackerEntity instanceof EntityPlayer) {
            npcdbc$shouldResetHurtTime = true;
            // Pre-calculate the attacker's full DBC damage so Counter/Dodge can use it
            EntityPlayer player = (EntityPlayer) attackerEntity;
            DBCData data = DBCData.get(player);
            if (data.Powertype == 1) {
                DBCUtils.preCalculatedAttackerDamage = DBCUtils.calculateAttackStat(player, amount, source);
            }
        }
        DBCUtils.damageEntityCalled = false;
        DBCUtils.insideAttackEntityFrom = true;
    }

    @Inject(method = "attackEntityFrom", at = @At("RETURN"))
    public void clearNPCDamageIfNeeded(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!DBCUtils.damageEntityCalled) {
            DBCUtils.npcLastSetDamage = null;
        }
        if (ConfigDBCGeneral.MODIFIED_DAMAGE_SPEED && npcdbc$shouldResetHurtTime && cir.getReturnValueZ()) {
            if (this.hurtResistantTime > ConfigDBCGeneral.NPC_MAX_HURT_RESISTANCE) {
                this.hurtResistantTime = ConfigDBCGeneral.NPC_MAX_HURT_RESISTANCE;
            }
        }
        npcdbc$shouldResetHurtTime = false;
        DBCUtils.insideAttackEntityFrom = false;
        DBCUtils.preCalculatedAttackerDamage = null;
    }
}
