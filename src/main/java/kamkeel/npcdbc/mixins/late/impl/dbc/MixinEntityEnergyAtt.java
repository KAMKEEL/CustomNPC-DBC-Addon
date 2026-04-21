package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityEnergyAtt.class, remap = false)
public abstract class MixinEntityEnergyAtt {

    @Shadow
    private float size;

    @Shadow
    private float explevel;

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void target(Entity entity, CallbackInfo ci) {
        EntityEnergyAtt ki = (EntityEnergyAtt) (Object) this;
        if (ki.shootingEntity == entity && ki.shootingEntity instanceof EntityNPCInterface && ki.ticksExisted < 60)
            ci.cancel();
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityLivingBase;BBIBBBBBBBII[BBBB)V", at = @At("RETURN"))
    private void npcdbc$capFormSizeKiScaling(EntityLivingBase shooter, byte type, byte speed, int chargeTime, byte effect,
                                             byte color, byte density, byte sincantation, byte sfire, byte smove,
                                             byte perc, int damage, int cost, byte[] sts, byte technum, byte align,
                                             byte relFired, CallbackInfo ci) {
        capFormSizeKiScaling(shooter);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityLivingBase;BBIBBBBBBBBII[BBBB)V", at = @At("RETURN"))
    private void npcdbc$capFormSizeKiScaling2(EntityLivingBase shooter, byte type, byte speed, int chargeTime, byte effect,
                                              byte color, byte density, byte sincantation, byte sfire, byte smove,
                                              byte wave, byte perc, int damage, int cost, byte[] sts, byte technum,
                                              byte align, byte relFired, CallbackInfo ci) {
        capFormSizeKiScaling(shooter);
    }

    private void capFormSizeKiScaling(EntityLivingBase shooter) {
        if (!ConfigDBCGameplay.EnableFormSizeKiAttackLimit || ConfigDBCGameplay.MaxFormSizeKiAttackScale <= 0 || shooter == null)
            return;
        // DBC only multiplies size/explevel by shooter-height when KiAttackScalesWithUser is on
        // (EntityEnergyAtt.java:397). If that's off, there's no scaling to cap — bail out.
        if (!JRMCoreConfig.KiAttackScalesWithUser)
            return;

        float shooterScale = shooter.height / 1.8f;
        float maxScale = ConfigDBCGameplay.MaxFormSizeKiAttackScale;
        if (shooterScale <= maxScale)
            return;

        float divisor = shooterScale / maxScale;
        this.size /= divisor;
        this.explevel /= divisor;
        EntityEnergyAtt self = (EntityEnergyAtt) (Object) this;
        self.width = this.size;
        self.height = this.size;
    }
}
