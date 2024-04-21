package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModelBipedDBC.class, remap = false)
public class MixinModelBipedDBC extends ModelBipedBody {
    /**
     * there's a bug where if you go ssj2 then revert to ssj1 then try to go ssj2 again,
     * the ssj1 hair won't animate into ssj2. Fixed it for custom forms, but it still happens for DBC forms
     */
    @Inject(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 0, shift = At.Shift.BEFORE))
    public void fixSSJtoSSJ2RageAnim(float par1, String h, float hl, int s, int rg, int pl, int rc, RenderPlayerJBRA rp, AbstractClientPlayer abstractClientPlayer, CallbackInfo ci, @Local(name = "trTime") LocalIntRef trTime) {
        if (ClientEventHandler.renderingPlayer != null) {
            String playerName = JRMCoreH.plyrs[pl];
            CustomForm form = Utility.getFormClient(ClientEventHandler.renderingPlayer);
            if (form != null && rp.getState(playerName) != s) {
                rp.setState(s, playerName);
            }
        }
    }

    @Inject(method = "renderHairs(FLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    public void renderFormFace(float par1, String hair, String anim, CallbackInfoReturnable<String> ci) {
        if (ClientEventHandler.renderingPlayer != null) {
            CustomForm form = Utility.getFormClient(ClientEventHandler.renderingPlayer);
            if (form != null) {
                DBCData dbcData = DBCData.get(ClientEventHandler.renderingPlayer);
                boolean fur = form.hairType.equals("ssj4") || form.hairType.equals("oozaru");
                if (form.hairType.equals("ssj4")) { //completely disable face rendering when ssj4, so I could render my own on top of a blank slate
                    disableFace(true, false, hair, ci);
                    disableHairPresets(false, hair, ci);
                } else if (form.hairType.equals("ssj3")) {
                    disableHairPresets(true, hair, ci);
                    if (hair.contains("EYEBROW")) { //bind ssj3 eyebrow texture to ssj3 hair type
                        int gen = JRMCoreH.dnsGender(dbcData.DNS);
                        int eyes = JRMCoreH.dnsEyes(dbcData.DNS);
                        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("jinryuumodscore", "cc/ssj3eyebrow/" + (gen == 1 ? "f" : "") + "humw" + eyes + ".png"));
                    } else if (hair.contains("D") && form.hairColor != -1)
                        RenderPlayerJBRA.glColor3f(form.hairColor);

                } else if (form.hairType.equals("oozaru")) {
                    disableFace(false, true, hair, ci);
                    disableHairPresets(false, hair, ci);
                }

                if (form.hairColor != -1 && hair.contains("EYEBROW"))
                    RenderPlayerJBRA.glColor3f(form.hairColor);
                if (form.eyeColor != -1 && !form.hairType.equals("ssj4") && (hair.contains("EYELEFT") || hair.contains("EYERIGHT")))  //eye colors for ALL forms except ssj4
                    RenderPlayerJBRA.glColor3f(form.eyeColor);
                if (hair.contains("SJT")) { // Tail Color
                    int color = fur ? form.furColor : form.hairColor;
                    if (color != -1) {
                        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("jinryuudragonbc:gui/allw.png"));
                        RenderPlayerJBRA.glColor3f(color);
                    }
                }

            }
        }
    }

    @Inject(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At("HEAD"), cancellable = true)
    public void disableHairRendering(float par1, String h, float hl, int s, int rg, int pl, int rc, RenderPlayerJBRA rp, AbstractClientPlayer abstractClientPlayer, CallbackInfo ci, @Local(ordinal = 0) LocalRef<String> hair, @Local(ordinal = 0) LocalIntRef st) {
        if (ClientEventHandler.renderingPlayer != null) {
            CustomForm form = Utility.getFormClient(ClientEventHandler.renderingPlayer);
            if (form != null) {

                if (form.hairType.equals("base"))
                    st.set(0);
                else if (form.hairType.equals("ssj"))
                    st.set(4);
                else if (form.hairType.equals("ssj2"))
                    st.set(5);

                if (form.hairColor != -1)
                    RenderPlayerJBRA.glColor3f(form.hairColor);

                if (form.hairCode.length() > 5)
                    hair.set(form.hairCode);

                //disable other hair rendering when ssj4 hair type
                boolean isCorrectHair = h.equals(form.hairCode) || h.startsWith("373852546750347428545480");
                if ((form.hairType.equals("ssj4") && !isCorrectHair) || form.hairType.equals("ssj3") || form.hairType.equals("oozaru"))
                    ci.cancel();


            }
        }
    }

    @Unique
    public void disableFace(boolean customSSJ4, boolean customOozaru, String faceType, CallbackInfoReturnable<String> ci) {
        if ((faceType.contains("FACENOSE") && !customSSJ4) || faceType.contains("FACEMOUTH") || faceType.contains("EYEBROW") || (faceType.contains("EYEBASE") && !Utility.stackTraceContains("renderOozaru")) || faceType.contains("EYELEFT") || faceType.contains("EYERIGHT"))
            ci.setReturnValue("");
    }

    @Unique
    public void disableHairPresets(boolean customSSJ3, String faceType, CallbackInfoReturnable<String> ci) {
        if (faceType.startsWith("A") || faceType.startsWith("B") || faceType.startsWith("C") || faceType.contains("12") || (!customSSJ3 && faceType.startsWith("D"))) {
            ci.setReturnValue("");
        }
    }
}
