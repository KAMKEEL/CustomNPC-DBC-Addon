package kamkeel.npcdbc.mixin.impl;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = JRMCoreH.class, remap = false)
public class MixinJRMCoreH {



    @ModifyVariable(method = "getPlayerAttribute(Lnet/minecraft/entity/player/EntityPlayer;[IIIIILjava/lang/String;IIZZZZZZI[Ljava/lang/String;ZLjava/lang/String;)I", at = @At(value = "LOAD"), ordinal = 0, remap = false)
    private static double beforeFormMasteryMulti(double original, EntityPlayer player, int[] currAttributes, int attribute, int st, int st2, int race, String SklX, int currRelease, int arcRel, boolean legendOn, boolean majinOn, boolean kaiokenOn, boolean mysticOn, boolean uiOn, boolean GoDOn, int powerType, String[] Skls, boolean isFused, String majinAbs,){


        // RUN THIS CODE WHEN first DOUBLE is assigned


        return original;
    }

    @Inject(method = "getPlayerAttribute(Lnet/minecraft/entity/player/EntityPlayer;[IIIIILjava/lang/String;IIZZZZZZI[Ljava/lang/String;ZLjava/lang/String;)I", at = @At("RETURN"), remap = false, cancellable = true)
    private static void onGetPlayerAttributeReturn(EntityPlayer player, int[] currAttributes, int attribute, int st, int st2, int race, String SklX, int currRelease, int arcRel, boolean legendOn, boolean majinOn, boolean kaiokenOn, boolean mysticOn, boolean uiOn, boolean GoDOn, int powerType, String[] Skls, boolean isFused, String majinAbs, CallbackInfoReturnable<Integer> info) {
        int result = info.getReturnValue();

        // Inject your code here to manipulate the result variable as needed
        // Set the manipulated result back to the return value
        PlayerCustomFormData formData = Utility.isServer() ? Utility.getFormData(player) : Utility.getFormDataClient();

        if (formData != null && formData.isInCustomForm()) {
            float[] multis = formData.getCurrentForm().getAllMulti();

            if (attribute == 0) //str
                result *= multis[0];
            if (attribute == 1) //dex
                result *= multis[1];
            if (attribute == 3) //will
                result *= multis[2];
        }
        result = (int) ((double) result > Double.MAX_VALUE ? Double.MAX_VALUE : (double) result);
        info.setReturnValue(result);
    }
}
