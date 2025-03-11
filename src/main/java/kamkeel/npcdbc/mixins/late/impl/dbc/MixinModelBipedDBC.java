package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ClientConstants;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.model.part.hair.DBCHair;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModelBipedDBC.class, remap = false)
public class MixinModelBipedDBC extends ModelBipedBody {
    @Shadow
    public ModelRenderer SaiT1;
    @Shadow
    public ModelRenderer FroB;
    @Shadow
    public ModelRenderer hairc71;
    @Unique
    boolean HD;
    @Unique
    String SDDir = CustomNpcPlusDBC.ID + ":textures/sd/";
    @Unique
    String HDDir = CustomNpcPlusDBC.ID + ":textures/hd/";

    @Redirect(method = "renderHairs(FLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V", ordinal = 10, remap = true))
    public void fixTailAnimNotSyncingSai(ModelRenderer instance, float i) {
    }

    @Inject(method = "renderHairs(FLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", ordinal = 3))
    public void fixTailAnimNotSyncingSai2(float par1, String hair, String anim, CallbackInfoReturnable<String> ci) {
        SaiT1.render(par1);
    }

    @Redirect(method = "renderHairs(FLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V", ordinal = 6, remap = true))
    public void fixTailAnimNotSyncingArco(ModelRenderer instance, float i) {

    }

    @Inject(method = "renderHairs(FLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", ordinal = 2))
    public void fixTailAnimNotSyncingArco2(float par1, String hair, String anim, CallbackInfoReturnable<String> ci) {
        FroB.render(par1);
    }

    /**
     * there's a bug where if you go ssj2 then revert to ssj1 then try to go ssj2 again,
     * the ssj1 hair won't animate into ssj2. Fixed it for custom forms, but it still happens for DBC forms
     */
    @Inject(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 0, shift = At.Shift.BEFORE))
    public void fixSSJtoSSJ2RageAnim(float par1, String h, float hl, int s, int rg, int pl, int rc, RenderPlayerJBRA rp, AbstractClientPlayer abstractClientPlayer, CallbackInfo ci, @Local(name = "trTime") LocalIntRef trTime) {
        if (ClientEventHandler.renderingPlayer != null) {
            String playerName = JRMCoreH.plyrs[pl];
            Form form = DBCData.getForm(ClientEventHandler.renderingPlayer);
            if (form != null && rp.getState(playerName) != s)
                rp.setState(s, playerName);

            //fixes human/majin hair not going from ssj state "B" to base state "A"
            int hairState = rc == 5 ? 0 : s;
            if (!JRMCoreH.HairsT(rp.getState(playerName), "A") && JRMCoreH.HairsT(hairState, "A")) {
                if ((!JRMCoreH.HairsT(rp.getState(playerName), hairState) || rg == 0) && rp.getStateChange(playerName) > 0) {
                    rp.setStateChange(rp.getStateChange(playerName) - trTime.get(), playerName);
                }

                if (rp.getStateChange(playerName) <= 0) {
                    rp.setStateChange(0, playerName);
                    rp.setState(hairState, playerName);
                }
            }
        }
    }

    @Inject(method = "renderHairs(FLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At("TAIL"), cancellable = true)
    public void tailStencil(float par1, String hair, String anim, CallbackInfoReturnable<String> ci) {
        if (!ClientConstants.renderingOutline && (hair.contains("SJT") || hair.contains("FR") || hair.equals("N")))
            RenderEventHandler.enableStencilWriting(ClientEventHandler.renderingPlayer.getEntityId() % 256);
    }

    @Inject(method = "renderHairs(FLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    public void formRendering(float par1, String hair, String anim, CallbackInfoReturnable<String> ci, @Local(ordinal = 0) LocalRef<String> Hair) {
        if (!ClientConstants.renderingOutline && (hair.contains("SJT") || hair.contains("FR") || hair.equals("N")))
            RenderEventHandler.enableStencilWriting((ClientEventHandler.renderingPlayer.getEntityId() + RenderEventHandler.TAIL_STENCIL_ID) % 256);


        if (ClientEventHandler.renderingPlayer != null) {
            Form form = DBCData.getForm(ClientEventHandler.renderingPlayer);
            if (form != null) {
                hair = Hair.get();
                DBCData dbcData = DBCData.get(ClientEventHandler.renderingPlayer);
                boolean isMonke = form.display.hasBodyFur || form.display.hairType.equals("ssj4") || form.display.hairType.equals("oozaru");
                HD = ConfigDBCClient.EnableHDTextures;
                boolean isSaiyan = dbcData.Race == 1 || dbcData.Race == 2;

                FormDisplay.BodyColor playerColors = dbcData.currentCustomizedColors;

                //eye colors for ALL forms except ssj4
                if ((hair.contains("EYELEFT") || hair.contains("EYERIGHT"))) {
                    if (form.display.isBerserk && !ClientConstants.renderingMajinSE)
                        ci.cancel();

                    int eyeColor = playerColors.getProperColor(form.display, "eye");
                    if (form.display.hairType.equals("ssj4") && isSaiyan && HD && form.display.hasEyebrows) {
                    } else if (eyeColor != -1)
                        RenderPlayerJBRA.glColor3f(eyeColor);
                }

                //majin effect check
                if (dbcData.Race == 5 && !form.display.effectMajinHair)
                    return;

                if (isSaiyan) {
                    //completely disable face rendering when ssj4, so I could render my own on top of a blank slate
                    if (form.display.hairType.equals("ssj4")) {
                        if (HD && form.display.hasEyebrows && !ClientConstants.renderingMajinSE)
                            disableFace(hair, ci);
                        if (isHairPreset(hair))
                            ci.cancel();
                    } else if (form.display.hairType.equals("oozaru")) {
                        disableFace(hair, ci);
                        if (isHairPreset(hair))
                            ci.cancel();
                    }
                }
                boolean isSSJ3 = false;
                if (form.display.hairType.equals("ssj3") || form.display.hairType.equals("raditz")) {
                    if (isHairPreset(hair) && !hair.startsWith("D")) {
                        String hair1 = form.display.hairType.equals("raditz") ? "D" : "D01";
                        Hair.set(hair1);
                    }
                    isSSJ3 = form.display.hairType.equals("ssj3") ? true : false;
                }
                //render brows
                if (hair.contains("EYEBROW") && dbcData.Race != 3 && (isSSJ3 || !form.display.hasEyebrows)) { //bind ssj3 eyebrow texture to ssj3 hair type
                    int gen = JRMCoreH.dnsGender(dbcData.DNS);
                    int eyes = JRMCoreH.dnsEyes(dbcData.DNS);
                    Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("jinryuumodscore", "cc/ssj3eyebrow/" + (gen == 1 ? "f" : "") + "humw" + eyes + ".png"));
                }
                //hair color for all forms
                if ((isHairPreset(hair) || hair.contains("EYEBROW"))) {
                    if (!playerColors.hasHairColor(dbcData, form.display)) {
                        if (dbcData.Race != DBCRace.NAMEKIAN) {
                            ColorMode.glColorInt(dbcData.renderingHairColor, 1f);
                        }
                    } else
                        RenderPlayerJBRA.glColor3f(playerColors.getProperColor(form.display.getHairColor(dbcData), "hair"));
                }


                //sets hairstates for default presets
                if (isHairPreset(hair)) {
                    String oldHair = Hair.get();
                    if (form.display.hairType.equals("base"))
                        oldHair = oldHair.replace(oldHair.charAt(0), 'A');
                    else if (form.display.hairType.equals("ssj"))
                        oldHair = oldHair.replace(oldHair.charAt(0), 'B');
                    else if (form.display.hairType.equals("ssj2"))
                        oldHair = oldHair.replace(oldHair.charAt(0), 'C');
                    Hair.set(oldHair);
                }
                // Tail Color
                if (hair.contains("SJT")) {
                    int color = isMonke ? playerColors.getFurColor(form.display, dbcData) : playerColors.getProperColor(form.display, "hair");
                    if (color != -1) {
                        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("jinryuudragonbc:gui/allw.png"));
                        RenderPlayerJBRA.glColor3f(color);
                    }
                    if (isMonke) {
                        String tailTexture = "allw.png";
                        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuudragonbc:gui/") + tailTexture));
                    }
                }


                if (isHairPreset(hair)) {
                    if (!form.display.hairType.equalsIgnoreCase("bald")) {
                        if (form.display.hairCode.length() > 5 || form.display.hairType.equals("ssj4")) {//if valid hair
                            RenderPlayerJBRA renderer = (RenderPlayerJBRA) RenderManager.instance.getEntityRenderObject(ClientEventHandler.renderingPlayer);
                            renderHairsV2(0.0625F, form.display.hairCode, 0.0F, 0, 0, dbcData.stats.getJRMCPlayerID(), (int) dbcData.Race, renderer, (AbstractClientPlayer) ClientEventHandler.renderingPlayer);
                            ci.cancel();
                        }
                    }
                }
            }
        }
    }

    @Shadow
    public void renderHairsV2(float par1, String h, float hl, int s, int rg, int pl, int rc, RenderPlayerJBRA rp, AbstractClientPlayer abstractClientPlayer) {

    }

    @Inject(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At(value = "INVOKE_ASSIGN", target = "LJinRyuu/JRMCore/JRMCoreH;StusEfctsClient(II)Z", ordinal = 3, shift = At.Shift.AFTER), cancellable = true)
    public void disableHairAnimGUI(float par1, String h, float hl, int s, int rg, int pl, int rc, RenderPlayerJBRA rp, AbstractClientPlayer abstractClientPlayer, CallbackInfo ci, @Local(name = "playerName") LocalRef<String> playerName, @Local(name = "aura") LocalBooleanRef aura, @Local(name = "trbo") LocalBooleanRef trbo, @Local(name = "kken") LocalBooleanRef kken, @Local(name = "trty") LocalBooleanRef trty) {
        if (RenderEventHandler.renderingPlayerInGUI) {
            playerName.set("HUDFormWheelPlayer");
            aura.set(false);
            trbo.set(false);
            kken.set(false);
            trty.set(false);
        }
    }

    @Inject(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At("HEAD"), cancellable = true)
    public void DNSHairRendering(float par1, String h, float hl, int s, int rg, int pl, int rc, RenderPlayerJBRA rp, AbstractClientPlayer abstractClientPlayer, CallbackInfo ci, @Local(ordinal = 0) LocalRef<String> hair, @Local(ordinal = 0) LocalIntRef st, @Local(ordinal = 3) LocalIntRef race) {
        if (ClientEventHandler.renderingPlayer != null) {
            Form form = DBCData.getForm(ClientEventHandler.renderingPlayer);

            //set texture for non saiyan CH, animate it when ascending
            if (rc != 1 && rc != 2) {
                if (TransformController.ascending && !TransformController.transformed)
                    race.set(1);
                else
                    race.set(rc);
                String hairTexture = "normall.png";
                TextureManager texMan = Minecraft.getMinecraft().renderEngine;
                texMan.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + hairTexture));
            }

            if (form != null) {
                DBCData data = DBCData.get(abstractClientPlayer);
                FormDisplay.BodyColor playerColors = data.currentCustomizedColors;
                HD = ConfigDBCClient.EnableHDTextures;
                boolean isSaiyan = rc == 1 || rc == 2;
                //remove CH if SSJ3/Oozaru
                if (form.display.hairType.equals("ssj3") || form.display.hairType.equals("raditz") || form.display.hairType.equals("oozaru") && (isSaiyan))
                    ci.cancel();

                String hairTexture = "normall.png";
                TextureManager texMan = Minecraft.getMinecraft().renderEngine;
                texMan.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + hairTexture));

                //majin effect check
                if (rc == 5 && !form.display.effectMajinHair) {
                    int bodyCMColor = playerColors.getProperColor(form.display, "bodyCM");
                    if (bodyCMColor != -1)
                        RenderPlayerJBRA.glColor3f(bodyCMColor);
                    return;
                }

                //hairstates with texture
                if (form.display.hairType.equals("base")) {
                    st.set(0);
                    race.set(1);
                    texMan.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + hairTexture));
                } else if (form.display.hairType.equals("ssj")) {
                    st.set(4);
                    race.set(1);
                    texMan.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + hairTexture));
                } else if (form.display.hairType.equals("ssj2")) {
                    st.set(5);
                    race.set(1);
                    texMan.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + hairTexture));
                } else if (form.display.hairType.equals("ssj4")) {
                    texMan.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + hairTexture));
                } else if (form.display.hairType.equals("oozaru")) {
                    st.set(0);
                    race.set(1);
                }

                //color CH
                if (!playerColors.hasHairColor(data, form.display))
                    RenderPlayerJBRA.glColor3f(data.renderingHairColor);
                else
                    RenderPlayerJBRA.glColor3f(playerColors.getProperColor(form.display, "hair"));

                //if bald or invalid CH, remove. Set SSJ4 to default if invalid
                if (form.display.hairType.equalsIgnoreCase("bald"))
                    hair.set("");
                else {
                    if (form.display.hairCode.length() > 5) //if valid hair
                        hair.set(form.display.hairCode);
                    else if (form.display.hairCode.length() < 5 && form.display.hairType.equals("ssj4"))  //if hairCode empty && ssj4, set to default ssj4 hair
                        hair.set(DBCHair.SSJ4_HAIR);
                }
            }
        }
    }

    @Unique
    public void disableFace(String faceType, CallbackInfoReturnable<String> ci) {
        if ((faceType.contains("FACENOSE") && !Utility.stackTraceContains("renderSSJ4Face")) || faceType.contains("FACEMOUTH") || faceType.contains("EYEBROW") || (faceType.contains("EYEBASE") && !Utility.stackTraceContains("renderOozaru")) || faceType.contains("EYELEFT") || faceType.contains("EYERIGHT"))
            ci.setReturnValue("");
    }

    @Unique
    public boolean isHairPreset(String hair) {
        return hair.startsWith("A0") || hair.startsWith("A1") || hair.startsWith("B0") || hair.startsWith("C0") || hair.contains("12") || hair.startsWith("D");
    }
}

