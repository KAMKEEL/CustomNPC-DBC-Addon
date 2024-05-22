package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityEnergyAtt.class, remap = false)
public class MixinEntityEnergyAtt {
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void target(Entity entity, CallbackInfo ci) {
        EntityEnergyAtt ki = (EntityEnergyAtt) (Object) this;

        if (ki.shootingEntity == entity && ki.ticksExisted < 60)
            ci.cancel();
    }
}
