package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityEnergyAtt.class, remap = false)
public abstract class MixinEntityEnergyAttNaN {

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void killOnInvalidPosition(CallbackInfo ci) {
        EntityEnergyAtt self = (EntityEnergyAtt) (Object) this;
        if (Double.isNaN(self.posX) || Double.isNaN(self.posY) || Double.isNaN(self.posZ) ||
            Double.isInfinite(self.posX) || Double.isInfinite(self.posY) || Double.isInfinite(self.posZ) ||
            Double.isNaN(self.motionX) || Double.isNaN(self.motionY) || Double.isNaN(self.motionZ) ||
            Double.isInfinite(self.motionX) || Double.isInfinite(self.motionY) || Double.isInfinite(self.motionZ)) {
            self.setDead();
            ci.cancel();
        }
    }
}
