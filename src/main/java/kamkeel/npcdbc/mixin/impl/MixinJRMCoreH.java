package kamkeel.npcdbc.mixin.impl;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.SyncedData.CustomFormData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = JRMCoreH.class, remap = false)
public class MixinJRMCoreH {

    @Inject(method = "getPlayerAttribute(Lnet/minecraft/entity/player/EntityPlayer;[IIIIILjava/lang/String;IIZZZZZZI[Ljava/lang/String;ZLjava/lang/String;)I", at = @At("RETURN"), remap = false, cancellable = true)
    private static void onGetPlayerAttributeReturn(EntityPlayer player, int[] currAttributes, int attribute, int st, int st2, int race, String SklX, int currRelease, int arcRel, boolean legendOn, boolean majinOn, boolean kaiokenOn, boolean mysticOn, boolean uiOn, boolean GoDOn, int powerType, String[] Skls, boolean isFused, String majinAbs, CallbackInfoReturnable<Integer> info) {
        int result = info.getReturnValue();

        // Inject your code here to manipulate the result variable as needed
        // Set the manipulated result back to the return value
        CustomFormData c = Utility.isServer() ? CustomFormData.get(player) : CustomFormData.getClient();
        //System.out.println("hi " + result);
        if (c != null && c.isInCustomForm()) {
            //  System.out.println("hi2 " + c.getCurrentForm().getAllMulti());
            result *= c.getCurrentForm().getAllMulti();
            // System.out.println("Res " + result);
        }
        result = (int) ((double) result > Double.MAX_VALUE ? Double.MAX_VALUE : (double) result);
        info.setReturnValue(result);
    }
}
