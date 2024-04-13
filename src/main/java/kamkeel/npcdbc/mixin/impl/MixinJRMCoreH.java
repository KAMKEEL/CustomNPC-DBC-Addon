package kamkeel.npcdbc.mixin.impl;

import JinRyuu.JRMCore.JRMCoreH;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = JRMCoreH.class, remap = false)
public class MixinJRMCoreH {

    @Inject(method = "getPlayerAttribute", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void getPlayerAttribute(EntityPlayer player, int[] currAttributes, int attribute, int st, int st2, int race, String SklX, int currRelease, int arcRel, boolean legendOn, boolean majinOn, boolean kaiokenOn, boolean mysticOn, boolean uiOn, boolean GoDOn, int powerType, String[] Skls, boolean isFused, String majinAbs, CallbackInfoReturnable<Integer> cir, float result) {
        System.out.println("injected " + result);
    }
}
