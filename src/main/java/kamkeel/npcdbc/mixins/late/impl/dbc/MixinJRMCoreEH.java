package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreEH;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.data.DBCDamageCalc;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCoreEH.class, remap = false)
public class MixinJRMCoreEH {
    @Inject(method = "damageEntity(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;F)V", at = @At("HEAD"), cancellable = true)
    public void NPCDamaged(EntityLivingBase targetEntity, DamageSource source, float amount, CallbackInfo ci, @Local(ordinal = 0) LocalFloatRef dam) {
        if (targetEntity instanceof EntityNPCInterface) {

            if (DBCUtils.npcLastSetDamage != null) {
                dam.set(DBCUtils.npcLastSetDamage); // THIS GETS DEDUCTED FROM NPC HEALTH
                DBCUtils.npcLastSetDamage = null;
            }

            Form form = PlayerDataUtil.getForm(targetEntity);
            if (form != null) {
                float formLevel = PlayerDataUtil.getFormLevel(targetEntity);
                if (form.mastery.hasDamageNegation()) {
                    float damage = dam.get();
                    float damageNegation = form.mastery.damageNegation * form.mastery.calculateMulti("damageNegation", formLevel);
                    float newDamage = damage * (100 - damageNegation) / 100;
                    dam.set(newDamage);
                }
            }
        }
    }

    @Inject(method = "Sd35MR", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;a1t3(Lnet/minecraft/entity/player/EntityPlayer;)V", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    public void dbcAttackFromPlayer(LivingHurtEvent event, CallbackInfo ci, @Local(name = "dam") LocalFloatRef dam, @Local(name = "targetPlayer") LocalRef<EntityPlayer> targetPlayer, @Local(name = "source") LocalRef<DamageSource> damageSource) {
        DBCDamageCalc damageCalc = DBCUtils.calculateDBCDamageFromSource(targetPlayer.get(), dam.get(), damageSource.get());
        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(targetPlayer.get(), damageCalc.damage, damageSource.get(), DBCDamageSource.KIATTACK);
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            ci.cancel();
            return;
        }

        // Last Set Damage
        DBCUtils.lastSetDamage = (int) damagedEvent.damage;
        damageCalc.processExtras();
    }

    @Inject(method = "Sd35MR", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;a1t3(Lnet/minecraft/entity/player/EntityPlayer;)V", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    public void dbcAttackFromNonPlayer(LivingHurtEvent event, CallbackInfo ci, @Local(name = "amount") LocalFloatRef dam, @Local(name = "targetPlayer") LocalRef<EntityPlayer> targetPlayer, @Local(name = "source") LocalRef<DamageSource> damageSource) {
        DBCDamageCalc damageCalc = DBCUtils.calculateDBCDamageFromSource(targetPlayer.get(), dam.get(), damageSource.get());
        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(targetPlayer.get(), damageCalc.damage, damageSource.get(), DBCDamageSource.KIATTACK);
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            ci.cancel();
            return;
        }

        // Last Set Damage
        DBCUtils.lastSetDamage = (int) damagedEvent.damage;
        damageCalc.processExtras();
    }
}
