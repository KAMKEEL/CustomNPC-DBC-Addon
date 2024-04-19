package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
        if(ClientEventHandler.renderingPlayer != null){
            PlayerCustomFormData formData = Utility.getFormDataClient((AbstractClientPlayer) ClientEventHandler.renderingPlayer);
            if (formData != null && formData.isInCustomForm()) {
                CustomForm f = Utility.getFormDataClient().getCurrentForm();
                DBCData dbcData = DBCData.getClient();
                if (f.hairType.equals("ssj4")) { //completely disable face rendering when ssj4, so I could render my own on top of a blank slate
                    if (hair.contains("FACENOSE") || hair.contains("EYEBASE") || hair.contains("FACEMOUTH") || hair.contains("EYEBROW") || hair.contains("EYEBASE") || hair.contains("EYELEFT") || hair.contains("EYERIGHT"))
                        ci.setReturnValue("");
                    else if (hair.contains("SJT")) {
                        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("jinryuudragonbc:gui/allw.png"));
                        RenderPlayerJBRA.glColor3f(f.furColor);
                    }

                } else if (f.hairType.equals("ssj3"))
                    if (hair.contains("EYEBROW")) { //bind ssj3 eyebrow texture to ssj3 hair type
                        int gen = JRMCoreH.dnsGender(dbcData.DNS);
                        int eyes = JRMCoreH.dnsEyes(dbcData.DNS);
                        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("jinryuumodscore", "cc/ssj3eyebrow/" + (gen == 1 ? "f" : "") + "humw" + eyes + ".png"));
                    }

            }
        }
    }

    @Inject(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At("HEAD"), cancellable = true)
    public void disableHairRendering(float par1, String h, float hl, int s, int rg, int pl, int rc, RenderPlayerJBRA rp, AbstractClientPlayer abstractClientPlayer, CallbackInfo ci) {
        if (Utility.getFormDataClient().isInCustomForm()) {
            CustomForm f = Utility.getFormDataClient().getCurrentForm();
            //disable other hair rendering when ssj4 hair type
            boolean isCorrectHair = h.equals(f.hairCode) || h.startsWith("373852546750347428545480");
            if (f.hairType.equals("ssj4") && !isCorrectHair) {
                ci.cancel();
            }
        }
    }

}
