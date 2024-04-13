package kamkeel.npcdbc.mixin.impl;

import JinRyuu.JRMCore.JRMCoreH;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCoreH.class, remap = false)
public class MixinJRMCoreH {

    @Inject(at = @At("HEAD"), method = "init()V")
    private static void getPlayerAttribute(CallbackInfo info) {

    }
}
