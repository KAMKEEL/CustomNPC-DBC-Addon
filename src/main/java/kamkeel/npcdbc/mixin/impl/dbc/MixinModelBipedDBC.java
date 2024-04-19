package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModelBipedDBC.class, remap = false)
public class MixinModelBipedDBC extends ModelBipedBody {

    @ModifyVariable(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At("HEAD"), ordinal = 0, name = "s", argsOnly = true)
    private int renderHairsS(int s) {
        if (ClientEventHandler.renderingPlayer != null) {
            CustomForm form = Utility.getFormClient((AbstractClientPlayer) ClientEventHandler.renderingPlayer);
            if (form != null) {
                if (form.hairType.equals("base"))
                    s = 0;
                else if (form.hairType.equals("ssj"))
                    s = 1;
                else if (form.hairType.equals("ssj2"))
                    s = 5;
            }
        }
        return s;
    }

    @Inject(method = "renderHairs(FLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    public void renderFormFace(float par1, String hair, String anim, CallbackInfoReturnable<String> ci) {
        if (ClientEventHandler.renderingPlayer != null) {
            CustomForm form = Utility.getFormClient((AbstractClientPlayer) ClientEventHandler.renderingPlayer);
            if (form != null) {
                DBCData dbcData = DBCData.get(ClientEventHandler.renderingPlayer);

                if (form.hairType.equals("ssj4")) { //completely disable face rendering when ssj4, so I could render my own on top of a blank slate
                    disableFace(hair, ci);
                    disableHairPresets(false, hair, ci);
                    if (hair.contains("SJT")) { //render tail color
                        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("jinryuudragonbc:gui/allw.png"));
                        RenderPlayerJBRA.glColor3f(form.furColor);
                    }
                } else if (hair.contains("EYELEFT") || hair.contains("EYERIGHT"))  //eye colors for ALL forms except ssj4
                    RenderPlayerJBRA.glColor3f(form.eyeColor);

                if (form.hairType.equals("ssj3")) {
                    disableHairPresets(true, hair, ci);
                    if (hair.contains("EYEBROW")) { //bind ssj3 eyebrow texture to ssj3 hair type
                        int gen = JRMCoreH.dnsGender(dbcData.DNS);
                        int eyes = JRMCoreH.dnsEyes(dbcData.DNS);
                        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("jinryuumodscore", "cc/ssj3eyebrow/" + (gen == 1 ? "f" : "") + "humw" + eyes + ".png"));
                    }
                }
            }
        }
    }

    @Inject(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At("HEAD"), cancellable = true)
    public void disableHairRendering(float par1, String h, float hl, int s, int rg, int pl, int rc, RenderPlayerJBRA rp, AbstractClientPlayer abstractClientPlayer, CallbackInfo ci) {
        if (ClientEventHandler.renderingPlayer != null) {
            CustomForm form = Utility.getFormClient((AbstractClientPlayer) ClientEventHandler.renderingPlayer);
            if (form != null) {
                //disable other hair rendering when ssj4 hair type
                boolean isCorrectHair = h.equals(form.hairCode) || h.startsWith("373852546750347428545480");
                if ((form.hairType.equals("ssj4") && !isCorrectHair) || form.hairType.equals("ssj3"))
                    ci.cancel();

                RenderPlayerJBRA.glColor3f(form.hairColor);

            }
        }
    }

    @Unique
    public void disableFace(String faceType, CallbackInfoReturnable<String> ci) {
        if (faceType.contains("FACENOSE") || faceType.contains("FACEMOUTH") || faceType.contains("EYEBROW") || faceType.contains("EYEBASE") || faceType.contains("EYELEFT") || faceType.contains("EYERIGHT"))
            ci.setReturnValue("");
    }

    @Unique
    public void disableHairPresets(boolean customSSJ3, String faceType, CallbackInfoReturnable<String> ci) {
        if (faceType.startsWith("A") || faceType.startsWith("B") || faceType.startsWith("C") || faceType.contains("12") || (!customSSJ3 && faceType.startsWith("D"))) {
            ci.setReturnValue("");
        }
    }
}
