package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreEH;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.constants.DBCDamageSource;
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
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = JRMCoreEH.class, remap = false)
public class MixinJRMCoreEH {
    @Inject(method = "Sd35MR", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreEH;damageEntity(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;F)V", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    public void NPCDamaged(LivingHurtEvent event, CallbackInfo ci, @Local(name = "dam") LocalFloatRef dam) {
        if (event.entity instanceof EntityNPCInterface) {
            Form form = PlayerDataUtil.getForm(event.entity);
            if (form != null) {
                float formLevel = PlayerDataUtil.getFormLevel(event.entity);
                if (form.mastery.hasDamageNegation()) {
                    float damage = dam.get();
                    float damageNegation = form.mastery.damageNegation * form.mastery.calculateMulti("damageNegation", formLevel);
                    float newDamage = damage * (100 - damageNegation) / 100;
                    dam.set(newDamage);
                }
            }
        }

        if (event.entity.isEntityInvulnerable()){
            return;
        }

        Entity entity = event.source.getSourceOfDamage();
        if (entity instanceof EntityPlayer == false) {
            return; // Only handle player attacks
        }

        EntityPlayer player = (EntityPlayer) entity;
        DBCPlayerEvent.AttackCreatureEvent attackCreatureEvent = new DBCPlayerEvent.AttackCreatureEvent(player, (EntityLivingBase) event.entity, dam.get(), event.source);
        if (DBCEventHooks.onAttackCreatureEvent(attackCreatureEvent)) {
            ci.cancel();
        }

        dam.set(attackCreatureEvent.getDamage());
    }

    @Inject(method = "Sd35MR", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;a1t3(Lnet/minecraft/entity/player/EntityPlayer;)V", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    public void dbcAttackFromPlayer(LivingHurtEvent event, CallbackInfo ci, @Local(name = "dam") LocalFloatRef dam, @Local(name = "targetPlayer") LocalRef<EntityPlayer> targetPlayer, @Local(name = "source") LocalRef<DamageSource> damageSource) {
        int damage = DBCUtils.calculateDBCDamageFromSource(targetPlayer.get(), dam.get(), damageSource.get());
        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(targetPlayer.get(), damage, damageSource.get(), DBCDamageSource.KIATTACK);
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            ci.cancel();
        }

        // Last Set Damage
        DBCUtils.lastSetDamage = (int) damagedEvent.damage;
    }

    @Inject(method = "Sd35MR", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;a1t3(Lnet/minecraft/entity/player/EntityPlayer;)V", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    public void dbcAttackFromNonPlayer(LivingHurtEvent event, CallbackInfo ci, @Local(name = "amount") LocalFloatRef dam, @Local(name = "targetPlayer") LocalRef<EntityPlayer> targetPlayer, @Local(name = "source") LocalRef<DamageSource> damageSource) {
        int damage = DBCUtils.calculateDBCDamageFromSource(targetPlayer.get(), dam.get(), damageSource.get());
        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(targetPlayer.get(), damage, damageSource.get(), DBCDamageSource.KIATTACK);
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            ci.cancel();
        }

        // Last Set Damage
        DBCUtils.lastSetDamage = (int) damagedEvent.damage;
    }


}
