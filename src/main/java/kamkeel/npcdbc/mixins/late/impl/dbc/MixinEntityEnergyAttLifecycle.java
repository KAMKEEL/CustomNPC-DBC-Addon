package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityEnergyAtt.class, remap = false)
public abstract class MixinEntityEnergyAttLifecycle {

    @Shadow
    private float strtX;
    @Shadow
    private float strtY;
    @Shadow
    private float strtZ;

    @Inject(method = "onUpdate", at = @At("HEAD"), remap = true, cancellable = true)
    private void npcdbc$checkLifecycle(CallbackInfo ci) {
        EntityEnergyAtt self = (EntityEnergyAtt) (Object) this;

        if (self.worldObj.isRemote)
            return;

        // Max lifetime check
        if (ConfigDBCGameplay.EnableMaxLifetime && ConfigDBCGameplay.MaxLifetimeTicks > 0) {
            if (self.ticksExisted >= ConfigDBCGameplay.MaxLifetimeTicks) {
                self.setDead();
                ci.cancel();
                return;
            }
        }

        // Max distance check
        if (ConfigDBCGameplay.EnableMaxDistance && ConfigDBCGameplay.MaxDistance > 0) {
            double dx = self.posX - strtX;
            double dy = self.posY - strtY;
            double dz = self.posZ - strtZ;
            double distSq = dx * dx + dy * dy + dz * dz;
            double maxDistSq = ConfigDBCGameplay.MaxDistance * ConfigDBCGameplay.MaxDistance;
            if (distSq >= maxDistSq) {
                self.setDead();
                ci.cancel();
            }
        }
    }
}
