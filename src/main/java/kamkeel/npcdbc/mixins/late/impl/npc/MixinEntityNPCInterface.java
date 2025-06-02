package kamkeel.npcdbc.mixins.late.impl.npc;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import kamkeel.npcdbc.config.ConfigDBCGeneral;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.mixins.late.impl.dbc.MixinJRMCoreEH;
import kamkeel.npcdbc.util.DBCUtils;
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
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.event.NpcEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityNPCInterface.class)
public abstract class MixinEntityNPCInterface extends EntityCreature implements IEntityAdditionalSpawnData, ICommandSender, IRangedAttackMob, IBossDisplayData {

    @Unique
    private boolean dbcAltered; //if DamagedEvent's damage was altered by a DBC player

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
    @Inject(method = "attackEntityFrom", at = @At(value = "FIELD", target = "Lnoppes/npcs/entity/EntityNPCInterface;wrappedNPC:Lnoppes/npcs/api/entity/ICustomNpc;", shift = At.Shift.BEFORE))
    public void fixDamagedEventDBCDamage(DamageSource damagesource, float amount, CallbackInfoReturnable<Boolean> cir, @Local(name = "i") LocalFloatRef dam) {
        Entity attackerEntity = NoppesUtilServer.GetDamageSource(damagesource);

        if (attackerEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attackerEntity;
            DBCData data = DBCData.get(player);
            if (dbcAltered = data.Powertype == 1) {
                // Apply Attributes and Resistances to Modified Damage
                float modifiedDamage = DBCUtils.calculateAttackStat(player, dam.get(), damagesource);
                // Apply Attributes
                EntityNPCInterface npcInterface = (EntityNPCInterface) (Object) this;
                modifiedDamage = AttributeAttackUtil.calculateDamagePlayerToNPC(player, npcInterface, modifiedDamage);

                // Apply Resistances
                if(ConfigDBCGeneral.ALLOW_DBC_DAMAGE_RESISTANCE){
                    modifiedDamage = npcInterface.stats.resistances.applyResistance(damagesource, modifiedDamage);
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
    @Redirect(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnoppes/npcs/scripted/event/NpcEvent$DamagedEvent;getDamage()F"))
    public float fixDamagedEventDBCDamage(NpcEvent.DamagedEvent instance) {
        if (dbcAltered && DBCUtils.npcLastSetDamage == null && !instance.isCancelled()) {
            DBCUtils.npcLastSetDamage = instance.getDamage();
        }
        dbcAltered = false;
        return instance.getDamage();
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    public void resetDamageEntityCalled(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        DBCUtils.damageEntityCalled = false;
    }

    @Inject(method = "attackEntityFrom", at = @At("RETURN"))
    public void clearNPCDamageIfNeeded(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!DBCUtils.damageEntityCalled) {
            DBCUtils.npcLastSetDamage = null;
        }
    }
}
