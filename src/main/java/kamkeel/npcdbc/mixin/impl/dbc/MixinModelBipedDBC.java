package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModelBipedDBC.class, remap = false)
public class MixinModelBipedDBC extends ModelBipedBody {

    @ModifyVariable(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At("HEAD"), ordinal = 0, name = "s", argsOnly = true)
    private int renderHairsV2(int s) {
        if (Utility.getFormDataClient().isInCustomForm()) {
            CustomForm f = Utility.getFormDataClient().getCurrentForm();
            if (f.hairType.equals("base"))
                s = 0;
            else if (f.hairType.equals("ssj"))
                s = 1;
            else if (f.hairType.equals("ssj2"))
                s = 5;
        }

        return s;
    }

    @Inject(method = "renderHairs(FLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    public void disableFaceRendering(float par1, String hair, String anim, CallbackInfoReturnable<String> ci) {
        if (Utility.getFormDataClient().isInCustomForm()) {
            CustomForm f = Utility.getFormDataClient().getCurrentForm();
            if (f.hairType.equals("ssj4")) {
                if (hair.contains("FACENOSE") || hair.contains("EYEBASE") || hair.contains("FACEMOUTH") || hair.contains("EYEBROW") || hair.contains("EYEBASE") || hair.contains("EYELEFT") || hair.contains("EYERIGHT"))
                    ci.setReturnValue("");
            } else if (f.hairType.equals("ssj3"))
                if (hair.contains("EYEBROW")) {
                    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("jinryuumodscore", "cc/ssj3eyebrow/" : "") + (gen == 1 ? "f" : "") + "humw" + eyes + ".png"));
                   // ci.setReturnValue("");
                }

        }
    }


}
