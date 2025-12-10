package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHJYC;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.client.ClientConstants;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.model.ModelDBC;
import kamkeel.npcdbc.client.render.OverlayModelRenderer;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.form.FormFaceData;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.overlay.Overlay.Type;
import kamkeel.npcdbc.data.overlay.OverlayContext;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.client.ClientCacheHandler;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.renderer.ImageData;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = RenderPlayerJBRA.class, remap = false)
public abstract class MixinRenderPlayerJBRA extends RenderPlayer {
//    @Shadow
//    private static float age;
    @Shadow
    public ModelBipedDBC modelMain;
    @Unique
    private Minecraft mc = Minecraft.getMinecraft();
    @Unique
    boolean HD;
    @Unique
    private String SDDir = CustomNpcPlusDBC.ID + ":textures/sd/";
    @Unique
    private String HDDir = CustomNpcPlusDBC.ID + ":textures/hd/";

//    @Shadow
//    private static float r;
//
//    @Shadow
//    private static float b;
//
//    @Shadow
//    private static float g;

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 0, shift = At.Shift.BEFORE))
    public void customKaiokenTint(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci) {
        DBCData data = DBCData.get(par1AbstractClientPlayer);
//        if (JRMCoreH.DBC() && kk2) {
//            Aura aura = data.getAura();
//            if (aura != null && aura.display.kaiokenColor != -1) {
//                Color tint = new Color(aura.display.kaiokenColor);
//                r = tint.getRedF() * kk / 15.0F;
//                g = tint.getGreenF() * kk / 15.0F;
//                b = tint.getBlueF() * kk / 15.0F;
//            }
//        }
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    public void preRender(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new DBCPlayerEvent.RenderEvent.Pre(par1AbstractClientPlayer, (RenderPlayerJBRA) (Object) this, par2)))
            ci.cancel();
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", ordinal = 1, shift = At.Shift.AFTER))
    public void postRender(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new DBCPlayerEvent.RenderEvent.Post(par1AbstractClientPlayer, (RenderPlayerJBRA) (Object) this, par2));
    }

    @Inject(method = "renderFirstPersonArm", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    public void preRenderArm(EntityPlayer par1EntityPlayer, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new DBCPlayerEvent.RenderArmEvent.Pre(par1EntityPlayer, (RenderPlayerJBRA) (Object) this, Minecraft.getMinecraft().timer.renderPartialTicks)))
            ci.cancel();
    }

    @Inject(method = "renderFirstPersonArm", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", ordinal = 11, shift = At.Shift.AFTER))
    public void postRenderArm(EntityPlayer par1EntityPlayer, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new DBCPlayerEvent.RenderArmEvent.Post(par1EntityPlayer, (RenderPlayerJBRA) (Object) this, Minecraft.getMinecraft().timer.renderPartialTicks));
    }

//    @Redirect(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glScalef(FFF)V", ordinal = 1), remap = true)
//    protected void setDamage(float x, float y, float z, @Local(ordinal = 0) LocalRef<AbstractClientPlayer> player) {
//        if (mc.theWorld == null)
//            return;
//        DBCData.get(player.get()).XZSize = x;
//        DBCData.get(player.get()).YSize = y;
//        if (RenderEventHandler.renderingPlayerInGUI && y > 0.8f) {
//            float f = ConfigDBCClient.AlteranteSelectionWheelTexture ? 6f : 4f;
//            GL11.glTranslatef(0, y / f, 0);
//        }
//
//        GL11.glScalef(x, y, z);
//    }

    @Inject(method = "glColor3f(I)V", at = @At("HEAD"), cancellable = true)
    private static void mixAuraColor(int c, CallbackInfo ci) {
        EntityPlayer player = ClientEventHandler.renderingPlayer;

        if (ClientEventHandler.renderingPlayer == null)
            return;

        DBCData data = DBCData.get(player);
        if (data == null)
            return;

//        if (JRMCoreH.DBC() && kk2) {
//            Aura aura = data.getAura();
//            if (aura != null && aura.display.kaiokenColor != -1) {
//                Color.lerpRGB(c, aura.display.kaiokenColor, kk / 15f).glColor();
//                ci.cancel();
//                return;
//            }
//        }

        EntityAura aura = data.auraEntity;
        if (!RenderEventHandler.renderingPlayerInGUI && aura != null && aura.color1 != -1 && aura.shouldRender()) {

            if (aura.fadeOut)
                aura.skinColorAlpha = (float) Math.max(0.0f, aura.skinColorAlpha - Math.pow(aura.fadeFactor, 1 + (aura.alpha / 1) * 7));

            int col = ColorMode.mixColors(c, aura.color1, aura.skinColorAlpha * 0.75f);

            float r = (float) (col >> 16 & 255) / 255.0F;
            float g = (float) (col >> 8 & 255) / 255.0F;
            float b = (float) (col & 255) / 255.0F;

            r = Math.min(1, r + getR());
            g = Math.min(1, g + getG());
            b = Math.min(1, b + getB());
            GL11.glColor3f(r, g, b);
            ci.cancel();
        }
    }

    @Inject(method = "glColor3f(IF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor3f(FFF)V", shift = At.Shift.BEFORE), cancellable = true)
    private static void mixAuraColor2(int c, float a, CallbackInfo ci, @Local(name = "r") LocalFloatRef r, @Local(name = "g") LocalFloatRef g, @Local(name = "b") LocalFloatRef b) {

        EntityPlayer player = ClientEventHandler.renderingPlayer;
        if (ClientEventHandler.renderingPlayer == null)
            return;

        DBCData data = DBCData.get(player);
        if (data == null)
            return;

//        System.out.println();

//        if (JRMCoreH.DBC() && kk2) {
//            Aura aura = data.getAura();
//            if (aura != null && aura.display.kaiokenColor != -1) {
//                Color col = new Color(r.get(), g.get(), b.get(), -1);
//                col.lerpRGB(aura.display.kaiokenColor, kk / 40f).glColor();
//                ci.cancel();
//                return;
//            }
//        }
    }
//
//    @Redirect(method = "func_130009_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isSneaking()Z"))
//    private boolean isSneaking(AbstractClientPlayer instance) {
//        if (RenderEventHandler.renderingPlayerInGUI)
//            return false;
//        return instance.isSneaking();
//    }

//    @Redirect(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;data2:[Ljava/lang/String;"), remap = true)
//    private String[] changeFormDAata(@Local(name = "pl") int pl, @Local(ordinal = 0) AbstractClientPlayer player) {
//        DBCData data = DBCData.get(player);
//        if (data.State > 0) {
//            Form form = data.getForm();
//            if (form != null && !form.stackable.vanillaStackable)
//                data.State = 0;
//        }
//        if (!ClientProxy.isRenderingWorld() || CustomNpcPlusDBC.proxy.isRenderingGUI())
//            ClientProxy.lastRendererGUIPlayerID = pl;
//        else
//            ClientProxy.lastRendererGUIPlayerID = -1;
//        if (JRMCoreH.data2 != null)
//            JRMCoreH.data2[pl] = data.State + ";" + data.State2;
//        return JRMCoreH.data2;
//    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "FIELD", target = "LJinRyuu/JBRA/RenderPlayerJBRA;kk2:Z", ordinal = 1))
    private void changeFormDAata4(AbstractClientPlayer p, float p_77041_2_, CallbackInfo ci) {
        DBCData data = DBCData.get(p);
        if (RenderEventHandler.renderingPlayerInGUI) {
//            kk2 = data.renderKK;
        }
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "Ljava/lang/Integer;parseInt(Ljava/lang/String;)I", ordinal = 1, shift = At.Shift.BEFORE))
    private void changeFormData35(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci, @Local(name = "state") LocalRef<String[]> state) {
        DBCData data = DBCData.get(par1AbstractClientPlayer);
        state.set(new String[]{data.State + "", data.State2 + ""});
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;DBC()Z", ordinal = 2, shift = At.Shift.BEFORE))
    private void changeFormData3(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci, @Local(name = "l") LocalBooleanRef ui, @Local(name = "gd") LocalBooleanRef GoD) {
        DBCData data = DBCData.get(par1AbstractClientPlayer);
        if (RenderEventHandler.renderingPlayerInGUI) {
            ui.set(data.renderUI);
            GoD.set(data.renderGoD);
        }
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 0, shift = At.Shift.AFTER))
    private void changeFormData(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci, @Local(name = "hairback") LocalIntRef hairback, @Local(name = "race") LocalIntRef race, @Local(name = "rg") LocalIntRef rg, @Local(name = "st") LocalIntRef st, @Local(name = "skintype") LocalIntRef skintype, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "bodyc1") LocalIntRef bodyC1, @Local(name = "bodyc2") LocalIntRef bodyC2, @Local(name = "bodyc3") LocalIntRef bodyC3, @Local(name = "msk") LocalBooleanRef mask) {
        Form form = DBCData.getForm(par1AbstractClientPlayer);
        DBCData data = DBCData.get(par1AbstractClientPlayer);
        data.skinType = (byte) skintype.get();
        //this prevents ssj2 hair animating immediately into ssj1 when going into ssj1 type forms from base
        if (TransformController.rage > 0 && data.player == mc.thePlayer) {
            if (TransformController.transformedInto != null) { //if just transformed into ssj type, don't display ssj2 hair
                if (TransformController.transformedInto.display.hairType.equals("ssj"))
                    rg.set(0);
            } else
                rg.set((int) TransformController.rage);
        }
        if (form != null) {
            //set body colors
            FormDisplay.BodyColor playerColors = data.currentCustomizedColors;
            FormDisplay display = form.display;
            if (playerColors.hasAnyColor(display, "bodycm"))
                bodyCM.set(playerColors.getProperColor(display, "bodycm"));
            if (playerColors.hasAnyColor(form.display, "bodyC1"))
                bodyC1.set(playerColors.getProperColor(form.display, "bodyC1"));
            if (playerColors.hasAnyColor(form.display, "bodyC2"))
                bodyC2.set(playerColors.getProperColor(form.display, "bodyC2"));
            if (playerColors.hasAnyColor(form.display, "bodyC3"))
                bodyC3.set(playerColors.getProperColor(form.display, "bodyC3"));


            if (data.Race == DBCRace.NAMEKIAN || data.Race == DBCRace.MAJIN) {
                data.renderingHairColor = bodyCM.get();
            }

            //set bodytypes for arcos
            if (race.get() == 4) {
                switch (form.display.bodyType) {
                    case "firstform":
                        st.set(0);
                        break;
                    case "secondform":
                        st.set(2);
                        break;
                    case "thirdform":
                        st.set(3);
                        break;
                    case "finalform":
                        st.set(4);
                        break;
                    case "ultimatecooler":
                        st.set(5);
                        break;
                    case "golden":
                        st.set(6);
                        break;
                }

                if (form.display.hasArcoMask())
                    mask.set(true);
                else
                    mask.set(false);

                //render ssj3 hair for humans/majins
            } else if ((form.display.hairType.equals("ssj3") || form.display.hairType.equals("raditz")) && (race.get() == 0 || race.get() == 5)) {

                String hairTexture = "normall.png";
                TextureManager texMan = Minecraft.getMinecraft().renderEngine;
                texMan.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + hairTexture));
                String hair = form.display.hairType.equals("raditz") ? "D" : "D01";
                this.modelMain.renderHairs(0.0625F, hair);
            }
        }
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/client/config/jrmc/JGConfigClientSettings;CLIENT_DA19:Z", shift = At.Shift.BEFORE))
    private void renderOverlays(AbstractClientPlayer pl, float par2, CallbackInfo ci, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "eyes") LocalIntRef eyes, @Local(name = "gen") LocalIntRef gender) {
        renderOverlays(DBCData.get(pl));

    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;HHWHO:Z", ordinal = 1, shift = At.Shift.BEFORE))
    private void renderSaiyanStates(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci, @Local(name = "hc") LocalIntRef hairCol, @Local(name = "pl") LocalIntRef pl, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "hairback") LocalIntRef hairback, @Local(name = "race") LocalIntRef race, @Local(name = "gen") LocalIntRef gender, @Local(name = "facen") LocalIntRef nose, @Local(name = "eyes") LocalIntRef eyes) {
        Form form = DBCData.getForm(par1AbstractClientPlayer);
        DBCData data = DBCData.get(par1AbstractClientPlayer);
        boolean usesHumanBody = race.get() == 0 || JRMCoreH.isRaceSaiyan(race.get());

        /**
         * @INFO: This fixes base hair color.
         */
        data.renderingHairColor = hairCol.get();

        if (form != null) {
            FormDisplay.BodyColor playerColors = data.currentCustomizedColors;
            FormDisplay display = form.display;

            HD = ConfigDBCClient.EnableHDTextures;
            int age = 10;
            boolean isSSJ4 = form.display.hairType.equals("ssj4") && JRMCoreH.isRaceSaiyan(race.get());

            //only saiyans
            if (race.get() == 1 || race.get() == 2) {
                if (form.display.hasBodyFur() || isSSJ4)
                    renderBodyFur(form, gender.get(), bodyCM.get(), data, data.skinType);

                //renders all ssj4
                if (isSSJ4) {
                    if (form.display.hasEyebrows && data.skinType != 0) {
                        if (form.display.furType != 2)
                            renderSSJ4Face(par1AbstractClientPlayer, form, gender.get(), nose.get(), eyes.get(), bodyCM.get(), data.renderingHairColor, age, data.DNS, data);
                        else
                            renderSaviorFace(par1AbstractClientPlayer, gender.get(), form, data);
                    }
                    if (hairback.get() != 12)
                        this.modelMain.renderHairsV2(0.0625F, "", 0.0F, 0, 0, pl.get(), race.get(), (RenderPlayerJBRA) (Object) this, par1AbstractClientPlayer);
                    //all oozaru rendering
                } else if (form.display.hairType.equals("oozaru")) {
                    int eyeColor = playerColors.getProperColor(display, "eye");
                    renderOozaru(bodyCM.get(), eyeColor, playerColors.getFurColor(form.display, data));
                    //ssj3 hair rendering
                } else if (form.display.hairType.equals("ssj3") || form.display.hairType.equals("raditz")) {
                    String hairTexture = "normall.png";
                    TextureManager texMan = Minecraft.getMinecraft().renderEngine;
                    texMan.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + hairTexture));
                    String hair = form.display.hairType.equals("raditz") ? "D" : "D01";
                    this.modelMain.renderHairs(0.0625F, hair);
                }
            }

            if (usesHumanBody && (form.display.hairType.equals("ssj3") || !form.display.hasEyebrows) && data.skinType != 0 && display.furType != 2 && HD) {
                renderSSJ3Face(par1AbstractClientPlayer, form, gender.get(), nose.get(), eyes.get(), bodyCM.get(), data.renderingHairColor, age, data.DNS, data);
            }

            if (form.display.hasBodyFur && form.display.furType == 2) {
                renderSaviorFace(par1AbstractClientPlayer, gender.get(), form, data);
            }

            boolean hasInvalidHair = form.display.hairType.equals("ssj4") || form.display.hairType.equals("ssj3") || form.display.hairType.equals("oozaru");

            if (usesHumanBody && !hasInvalidHair && !form.display.isBerserk && form.display.hasPupils){
                renderPupils(form, gender.get(), eyes.get(), data);
            }
        }
    }

//    @Redirect(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;onGround:Z", remap = true), remap = true)
//    public boolean fixP2otaraHair(AbstractClientPlayer instance) {
//        if (RenderEventHandler.renderingPlayerInGUI)
//            return true;
//        return instance.onGround;
//    }

//    @Redirect(method = "renderEquippedItemsJBRA", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;HHWHO:Z", opcode = Opcodes.GETSTATIC))
//    public boolean fixPotaraHair(@Local(name = "abstractClientPlayer") AbstractClientPlayer abstractClientPlayer) {
//        ItemStack item = abstractClientPlayer.getCurrentArmor(3);
//
//        if (item != null && item.getItem() instanceof ItemPotara)
//            return false;
//
//        return JRMCoreConfig.HHWHO;
//    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "LJinRyuu/JBRA/ModelBipedDBC;renderHairs(FLjava/lang/String;)V", ordinal = 34, shift = At.Shift.BEFORE))
    public void beforeMajinSE(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci) {
        ClientConstants.renderingMajinSE = true;
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "LJinRyuu/JBRA/ModelBipedDBC;renderHairs(FLjava/lang/String;)V", ordinal = 34, shift = At.Shift.AFTER))
    public void afterMajinSE(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci) {
        ClientConstants.renderingMajinSE = false;
    }

    @Unique
    private void renderBodyFur(Form form, int gender, int bodyCM, DBCData data, int skintype) {
        if (skintype != 0) {
            String bodyTexture = (gender == 1 ? "f" : "") + "hum.png";
            this.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:cc/") + bodyTexture));
            RenderPlayerJBRA.glColor3f(bodyCM);
            this.modelMain.renderBody(0.0625F);
        }

        FormDisplay.BodyColor playerColors = data.currentCustomizedColors;

        int furType = form.display.furType;
        String fur = "ss4" + (skintype == 0 ? "a" : "b") + furType + ".png";
        String texture = (HD ? HDDir : SDDir) + "ssj4/" + fur;
        this.bindTexture(new ResourceLocation(texture));
        RenderPlayerJBRA.glColor3f(playerColors.getFurColor(form.display, data));
        this.modelMain.renderBody(0.0625F);

        if (form.display.furType == 2) {
            int eyeColor = playerColors.getProperColor(form.display, "eye");
            RenderPlayerJBRA.glColor3f(eyeColor == -1 ? 0xFFFF55 : eyeColor);
            this.bindTexture(new ResourceLocation((HD ? HDDir : SDDir) + "savior/saviorchest.png"));
            this.modelMain.renderBody(0.0625F);
        }
    }

    @Shadow
    private static int gen; //gender (actual gen+1)

    @Shadow
    private static float childScl; //age from 0 to 1 (adult)

    @Shadow
    private static int preg; //pregnant

    @Unique
    private void renderOverlays(DBCData data) {

        OverlayContext ctx = OverlayContext.from(data);
        ctx.model = modelMain;

        List<OverlayChain> chains = ModelDBC.applyOverlayChains(data.getOverlayChains(), ctx);

        for (OverlayChain chain : chains) {
            for (Overlay overlay : chain.overlays) {
                if (!overlay.isEnabled())
                    continue;

                ctx.overlay = overlay;
                Type type = overlay.getType();
                String texture;

                if (type == Overlay.Type.Face) {
                    texture = ((Overlay.Face) overlay).getTexture(ctx.eyeType());
                } else {
                    texture = overlay.getTexture();
                }

                ImageData imageData = ClientCacheHandler.getImageData(texture);
                if (imageData == null || !imageData.imageLoaded())
                    continue;

                //TODO this stuff needs a method
                //int furColor = data.currentCustomizedColors.getProperColor(displayData.getFurColor(data), "fur");
                //int hairColor = data.currentCustomizedColors.getProperColor(displayData.getHairColor(data), "hair");
                //int eyeColor = data.currentCustomizedColors.getProperColor(displayData.getColor("eye"), "eye");

                //int color = overlay.getColorType() == Overlay.ColorType.Body.ordinal() ? bodyCM : overlay.getColorType() == Overlay.ColorType.Eye.ordinal() ? (eyeColor < 0 ? JRMCoreH.dnsEyeC1(data.DNS) : eyeColor) : overlay.getColorType() == Overlay.ColorType.Hair.ordinal() ? (hairColor < 0 ? defaultHairColor : hairColor) : overlay.getColorType() == Overlay.ColorType.Fur.ordinal() ? furColor : overlay.getColor();

                int color = 0xffffff;
                if (!bindImageDataTexture(imageData, color))
                    continue;

                GL11.glPushMatrix();
                boolean glow = overlay.isGlow();
                if (glow) {
                    GL11.glDisable(GL11.GL_LIGHTING);
                    if (!RenderEventHandler.renderingPlayerInGUI) //in-game not in GUI, as lightmap is disabled in GUIs so cant enable it again
                        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
                }

                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.001f);
                GL11.glEnable(GL11.GL_ALPHA_TEST);

                new Color(color, overlay.alpha).glColor();
                OverlayModelRenderer.render(type, ctx);

                if (glow) {
                    GL11.glEnable(GL11.GL_LIGHTING);
                    if (!RenderEventHandler.renderingPlayerInGUI) //in-game not in GUI
                        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
                }

                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        }
    }

    @Unique
    private boolean bindImageDataTexture(ImageData data, int color) {
        ResourceLocation location = data.getLocation();
        if (location != null && !data.invalid()) {
            try {
                this.bindTexture(location);
                return true;
            } catch (Exception exception) {
                return false;
            }
        }

        return false;
    }

    @Unique
    private void renderPupils(Form form, int gender, int eyes, DBCData data) {
        if (ConfigDBCClient.EnableHDTextures) {
            FormDisplay display = form.display;
            FormFaceData faceData = display.faceData;
            FormDisplay.BodyColor playerColors = data.currentCustomizedColors;

            String eyeDir = "base/eyes/pupils/";

            if (!form.display.isBerserk && !faceData.hasLeftEyeRemoved(eyes)) {
                int eyeColor = playerColors.getProperColor(display, "eye");
                RenderPlayerJBRA.glColor3f(eyeColor == -1 ? JRMCoreH.dnsEyeC1(data.DNS) : eyeColor);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "eyeleft" + eyes + ".png"));

                GL11.glPushMatrix();
                float scale = 1.0021f;
                GL11.glScalef(scale, scale, scale);
                this.modelMain.bipedHead.render(0.0625F);
                GL11.glPopMatrix();
            }

            if (!form.display.isBerserk && !faceData.hasRightEyeRemoved(eyes)) {
                int eyeColor = playerColors.getProperColor(display, "eye");
                RenderPlayerJBRA.glColor3f(eyeColor == -1 ? JRMCoreH.dnsEyeC2(data.DNS) : eyeColor);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "eyeright" + eyes + ".png"));

                GL11.glPushMatrix();
                float scale = 1.0021f;
                GL11.glScalef(scale, scale, scale);
                this.modelMain.bipedHead.render(0.0625F);
                GL11.glPopMatrix();
            }
        }
    }

    /*
     * OVERLAYS NOW HAVE THIS CALCULATION BAKED IN SO NO LONGER NEEDED!
     *
     * Used mostly for rendering bipeadHead correctly on female/younger than adult ages.
     * No need in modelMain.renderBody() as it already applies these in its calculations.
     */
    @Unique
    private void applyAgeGenderTransformations(AbstractClientPlayer player, int gender) {
        int g = gender + 1;
        float ageFactor = 3.0F - JRMCoreHJYC.JYCsizeBasedOnAge(player) * 2.0F; //from 0-1 where 0 is child and 1 is full adult
        GL11.glScalef((0.5F + 0.5F / ageFactor) * (g <= 1 ? 1.0F : 0.85F), 0.5F + 0.5F / ageFactor, (0.5F + 0.5F / ageFactor) * (g <= 1 ? 1.0F : 0.85F));
        GL11.glTranslatef(0.0F, (ageFactor - 1.0F) / ageFactor * (2.0F - (ageFactor >= 1.5F && ageFactor <= 2.0F ? (2.0F - ageFactor) / 2.5F : (ageFactor < 1.5F && ageFactor >= 1.0F ? (ageFactor * 2.0F - 2.0F) * 0.2F : 0.0F))), 0.0F);
    }

    @Unique
    private void renderSSJ4Face(AbstractClientPlayer player, Form form, int gender, int nose, int eyes, int bodyCM, int defaultHairColor, float age, String dns, DBCData data) {
        FormDisplay display = form.display;
        FormFaceData faceData = display.faceData;

        GL11.glPushMatrix();
        applyAgeGenderTransformations(player, gender);

        if (ConfigDBCClient.EnableHDTextures) {
            FormDisplay.BodyColor playerColors = data.currentCustomizedColors;
            String eyeDir = (form.display.furType == 1 ? "ssj4d" : "ssj4") + (gender == 1 ? "/female" : "/male") + "/face_" + eyes + "/";

            if (!faceData.hasWhiteRemoved(eyes)) {
                GL11.glColor3f(1.0f, 1.0f, 1.0f);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4eyewhite.png"));
                this.modelMain.bipedHead.render(1F / 16F);
            }

            if (!form.display.isBerserk && !faceData.hasRightEyeRemoved(eyes)) {
                int eyeColor = playerColors.getProperColor(display, "eye");
                RenderPlayerJBRA.glColor3f(eyeColor == -1 ? 0xF3C807 : eyeColor);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4eyeright.png"));
                this.modelMain.bipedHead.render(0.0625F);

                if (form.display.furType == 1) {
                    GL11.glColor3f(1.0f, 1.0f, 1.0f);
                    this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4glowright.png"));
                    this.modelMain.bipedHead.render(0.0625F);
                }
            }

            if (!form.display.isBerserk && !faceData.hasLeftEyeRemoved(eyes)) {
                int eyeColor = playerColors.getProperColor(display, "eye");
                RenderPlayerJBRA.glColor3f(eyeColor == -1 ? 0xF3C807 : eyeColor);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4eyeleft.png"));
                this.modelMain.bipedHead.render(0.0625F);

                if (form.display.furType == 1) {
                    GL11.glColor3f(1.0f, 1.0f, 1.0f);
                    this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4glowleft.png"));
                    this.modelMain.bipedHead.render(0.0625F);
                }
            }

            if (!faceData.hasEyebrowsRemoved(eyes)) {
                RenderPlayerJBRA.glColor3f(playerColors.getFurColor(form.display, data));
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4brows.png"));
                this.modelMain.bipedHead.render(1F / 16F);

                int hairColor = playerColors.getProperColor(form.display.getHairColor(data), "hair");
                RenderPlayerJBRA.glColor3f(hairColor < 0 ? defaultHairColor : hairColor, age);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4brows2.png"));
                this.modelMain.bipedHead.render(1F / 16F);

                RenderPlayerJBRA.glColor3f(bodyCM);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4shade.png"));
                this.modelMain.bipedHead.render(0.0625F);
            }
        }
        GL11.glPopMatrix(); //Pop the age/gender transformations here as finished rendering bipeadHead


        if (!faceData.hasNoseRemoved(eyes)) {
            RenderPlayerJBRA.glColor3f(bodyCM);
            String noseTexture = (gender == 1 ? "f" : "") + "humn" + nose + ".png";
            this.bindTexture(new ResourceLocation((HD ? HDDir + "base/nose/" : "jinryuumodscore:cc/") + noseTexture));
            this.modelMain.renderHairs(0.0625F, "FACENOSE");
        }

        if (!faceData.hasMouthRemoved(eyes)) {
            RenderPlayerJBRA.glColor3f(bodyCM);
            String mouthTexture = (gender == 1 ? "f" : "") + "humm" + JRMCoreH.dnsFaceM(dns) + ".png";
            this.bindTexture(new ResourceLocation((HD ? HDDir + "base/mouth/" : "jinryuumodscore:cc/") + mouthTexture));
            this.modelMain.renderHairs(0.0625F, "FACEMOUTH");
        }
    }

    @Unique
    private void renderOozaru(int bodyCM, int eyeColor, int furColor) {
        ResourceLocation bdyskn = new ResourceLocation(HD ? HDDir + "oozaru/oozaru1.png" : "jinryuudragonbc:cc/oozaru1.png"); //human hairless face
        this.bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(bodyCM);
        this.modelMain.renderBody(0.0625F);
        bdyskn = new ResourceLocation(HD ? HDDir + "oozaru/oozaru2.png" : "jinryuudragonbc:cc/oozaru2.png"); //the fur
        this.bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(furColor);
        this.modelMain.renderBody(0.0625F);
        this.bindTexture(new ResourceLocation((HD ? HDDir : SDDir) + "oozaru/oozarueyes.png")); //eyes
        RenderPlayerJBRA.glColor3f(eyeColor);
        this.modelMain.renderHairs(0.0625F, "EYEBASE");
        RenderPlayerJBRA.glColor3f(bodyCM); //
        this.modelMain.renderHairs(0.0625F, "OOZARU");
    }

    @Unique
    private void renderSSJ3Face(AbstractClientPlayer player, Form form, int gender, int nose, int eyes, int bodyCM, int defaultHairColor, float age, String dns, DBCData data) {
        FormDisplay display = form.display;
        FormFaceData faceData = display.faceData;

        GL11.glPushMatrix();
        applyAgeGenderTransformations(player, gender);

        if (ConfigDBCClient.EnableHDTextures) {
            FormDisplay.BodyColor playerColors = data.currentCustomizedColors;
            String eyeDir = "ssj3/" + (gender == 1 ? "female" : "male") + "/face_" + eyes + "/";

            if (!faceData.hasWhiteRemoved(eyes)) {
                GL11.glColor3f(1.0f, 1.0f, 1.0f);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj3eyewhite.png"));
                this.modelMain.bipedHead.render(0.0625F);
            }

            if (!form.display.isBerserk && !faceData.hasRightEyeRemoved(eyes)) {
                int eyeColor = playerColors.getProperColor(display, "eye");
                RenderPlayerJBRA.glColor3f(eyeColor == -1 ? 0xF3C807 : eyeColor);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj3eyeright.png"));
                this.modelMain.bipedHead.render(0.0625F);
            }

            if (!form.display.isBerserk && !faceData.hasLeftEyeRemoved(eyes)) {
                int eyeColor = playerColors.getProperColor(display, "eye");
                RenderPlayerJBRA.glColor3f(eyeColor == -1 ? 0xF3C807 : eyeColor);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj3eyeleft.png"));
                this.modelMain.bipedHead.render(0.0625F);
            }

            if (!faceData.hasEyebrowsRemoved(eyes)) {
                int hairColor = playerColors.getProperColor(form.display.getHairColor(data), "hair");
                RenderPlayerJBRA.glColor3f(hairColor < 0 ? defaultHairColor : hairColor, age);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj3brows.png"));
                this.modelMain.bipedHead.render(1F / 16F);

                RenderPlayerJBRA.glColor3f(bodyCM);
                this.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj3shade.png"));
                this.modelMain.bipedHead.render(0.0625F);
            }
        }
        GL11.glPopMatrix(); //Pop the age/gender transformations here as finished rendering bipeadHead

        if (!faceData.hasNoseRemoved(eyes)) {
            RenderPlayerJBRA.glColor3f(bodyCM);
            String noseTexture = (gender == 1 ? "f" : "") + "humn" + nose + ".png";
            this.bindTexture(new ResourceLocation((HD ? HDDir + "base/nose/" : "jinryuumodscore:cc/") + noseTexture));
            this.modelMain.renderHairs(0.0625F, "FACENOSE");
        }

        if (!faceData.hasMouthRemoved(eyes)) {
            RenderPlayerJBRA.glColor3f(bodyCM);
            String mouthTexture = (gender == 1 ? "f" : "") + "humm" + JRMCoreH.dnsFaceM(dns) + ".png";
            this.bindTexture(new ResourceLocation((HD ? HDDir + "base/mouth/" : "jinryuumodscore:cc/") + mouthTexture));
            this.modelMain.renderHairs(0.0625F, "FACEMOUTH");
        }
    }

    @Unique
    private void renderSaviorFace(AbstractClientPlayer player, int gender, Form form, DBCData data) {
        GL11.glPushMatrix();
        applyAgeGenderTransformations(player, gender);

        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        String eyeDir = "savior/";
        this.bindTexture(new ResourceLocation((HD ? HDDir : SDDir) + eyeDir + "saviormouth.png"));
        this.modelMain.bipedHead.render(1F / 16F);

        FormDisplay.BodyColor playerColors = data.currentCustomizedColors;
        FormDisplay display = form.display;


        int eyeColor = playerColors.getProperColor(display, "eye");
        RenderPlayerJBRA.glColor3f(eyeColor == -1 ? 0xFFFF55 : eyeColor);
        this.bindTexture(new ResourceLocation((HD ? HDDir : SDDir) + eyeDir + "savioreyes.png"));
        this.modelMain.bipedHead.render(0.0625F);

        GL11.glPopMatrix();
    }

//    @Inject(method = "renderFirstPersonArm", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;DBC()Z", ordinal = 0, shift = At.Shift.AFTER), remap = true)
//    private void changeFormArmData(EntityPlayer par1EntityPlayer, CallbackInfo ci, @Local(name = "race") LocalIntRef race, @Local(name = "State") LocalIntRef st, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "bodyc1") LocalIntRef bodyC1, @Local(name = "bodyc2") LocalIntRef bodyC2, @Local(name = "bodyc3") LocalIntRef bodyC3, @Local(name = "id") LocalIntRef id) {
//        Form form = DBCData.getForm(par1EntityPlayer);
//        ClientEventHandler.renderingPlayer = par1EntityPlayer;
//        if (form != null) {
//
//            FormDisplay.BodyColor playerColors = DBCData.get(par1EntityPlayer).currentCustomizedColors;
//            FormDisplay display = form.display;
//            if (playerColors.hasAnyColor(display, "bodycm"))
//                bodyCM.set(playerColors.getProperColor(display, "bodycm"));
//            if (playerColors.hasAnyColor(form.display, "bodyC1"))
//                bodyC1.set(playerColors.getProperColor(form.display, "bodyC1"));
//            if (playerColors.hasAnyColor(form.display, "bodyC2"))
//                bodyC2.set(playerColors.getProperColor(form.display, "bodyC2"));
//            if (playerColors.hasAnyColor(form.display, "bodyC3"))
//                bodyC3.set(playerColors.getProperColor(form.display, "bodyC3"));
//
//            //arm bodytype for arcosian
//            if (race.get() == 4) {
//                if (form.display.bodyType.equals("firstform"))
//                    st.set(0);
//                else if (form.display.bodyType.equals("secondform"))
//                    st.set(2);
//                else if (form.display.bodyType.equals("thirdform"))
//                    st.set(3);
//                else if (form.display.bodyType.equals("finalform") || form.display.bodyType.equals("golden"))
//                    st.set(4);
//                else if (form.display.bodyType.equals("ultimatecooler"))
//                    st.set(5);
//            }
//        }
//    }

//    @Inject(method = "renderFirstPersonArm", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;dnsGender(Ljava/lang/String;)I", ordinal = 0, shift = At.Shift.BEFORE, remap = false), remap = true)
//    private void stopMonkeeArmInWrongForm(EntityPlayer par1EntityPlayer, CallbackInfo ci, @Local(name = "saiOozar") LocalBooleanRef isOozaru, @Local(name = "race") int race) {
//        Form form = DBCData.getForm(par1EntityPlayer);
//
//        if (form != null && (race == 1 || race == 2)) {
//            if (!form.stackable.vanillaStackable)
//                isOozaru.set(false);
//        }
//    }
//
//    @Inject(method = "renderFirstPersonArm", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/client/config/jrmc/JGConfigClientSettings;CLIENT_DA19:Z", ordinal = 0, shift = At.Shift.BEFORE), remap = true)
//    private void renderSaiyanArm(EntityPlayer par1EntityPlayer, CallbackInfo ci, @Local(name = "race") LocalIntRef race, @Local(name = "id") LocalIntRef id, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "gen") LocalIntRef gender) {
//        Form form = DBCData.getForm(par1EntityPlayer);
//        if (form != null && (race.get() == 1 || race.get() == 2)) {
//            if (this.renderManager != null && this.renderManager.renderEngine != null) {
//                if (form.display.hasBodyFur || form.display.hairType.equals("ssj4")) {
//                    renderSSJ4Arm(form, par1EntityPlayer, id.get(), gender.get(), bodyCM.get(), DBCData.get(par1EntityPlayer));
//                } else if (form.display.hairType.equals("oozaru")) {
//                    renderOozaruArm(form, par1EntityPlayer, id.get(), bodyCM.get(), DBCData.get(par1EntityPlayer));
//                }
//            }
//        }
//    }

//    @Inject(method = "renderFirstPersonArm", at = @At(value = "INVOKE", target = "LJinRyuu/JBRA/RenderPlayerJBRA;func_aam2(Lnet/minecraft/client/model/ModelRenderer;Lnet/minecraft/client/model/ModelRenderer;IZ)V", ordinal = 11, shift = At.Shift.AFTER), remap = true)
//    private void fixLeftSaiyanFurArm(EntityPlayer par1EntityPlayer, CallbackInfo ci, @Local(name = "race") LocalIntRef race, @Local(name = "id") LocalIntRef id, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "gen") LocalIntRef gender) {
//        int idValue = id.get();
//        if(idValue == -1)
//            return;
//
//        if (this.renderManager != null && this.renderManager.renderEngine != null) {
//            Form form = DBCData.getForm(par1EntityPlayer);
//            if (form != null && (race.get() == 1 || race.get() == 2)) {
//                if (form.display.hasBodyFur || form.display.hairType.equals("ssj4")) {
//                    renderSSJ4Arm(form, par1EntityPlayer, id.get(), gender.get(), bodyCM.get(), DBCData.get(par1EntityPlayer));
//                } else if (form.display.hairType.equals("oozaru")) {
//                    renderOozaruArm(form, par1EntityPlayer, id.get(), bodyCM.get(), DBCData.get(par1EntityPlayer));
//                }
//            }
//        }
//    }

    @Unique
    private void renderSSJ4Arm(Form form, EntityPlayer player, int id, int gender, int bodyCM, DBCData data) {
        if (data.skinType != 0) {
            String bodyTexture = (gender == 1 ? "f" : "") + "hum.png";
            this.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:cc/") + bodyTexture));
            RenderPlayerJBRA.glColor3f(bodyCM);
            renderArm(id, player);
        }
        FormDisplay.BodyColor playerColors = DBCData.get(player).currentCustomizedColors;

        int furType = form.display.furType;
        String fur = "ss4" + (data.skinType == 0 ? "a" : "b") + furType + ".png";
        this.bindTexture(new ResourceLocation((HD ? HDDir : SDDir) + "ssj4/" + fur));
        RenderPlayerJBRA.glColor3f(playerColors.getFurColor(form.display, data));
        renderArm(id, player);
    }

    @Unique
    private void renderOozaruArm(Form form, EntityPlayer player, int id, int bodyCM, DBCData data) {
        ResourceLocation bdyskn = new ResourceLocation((HD ? HDDir + "oozaru/" : "jinryuudragonbc:cc/") + "oozaru1.png");
        this.bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(bodyCM);
        renderArm(id, player);

        FormDisplay.BodyColor playerColors = DBCData.get(player).currentCustomizedColors;


        bdyskn = new ResourceLocation((HD ? HDDir + "oozaru/" : "jinryuudragonbc:cc/") + "oozaru2.png");
        this.bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(playerColors.getFurColor(form.display, data));
        renderArm(id, player);
    }

    @Unique
    private void renderArm(int id, EntityPlayer player) {
        this.modelMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);

        if (id == -1)
            this.modelMain.RA.render(0.0625F);
        else {
            this.func_aam(this.modelMain.RA, this.modelMain.LA, id, true);
            this.func_aam2(this.modelMain.RA, this.modelMain.LA, id, true);
        }
    }

    @Shadow
    private void func_aam(ModelRenderer ra, ModelRenderer lA, int id, boolean c) {
    }

    @Shadow
    private void func_aam2(ModelRenderer ra, ModelRenderer la, int id, boolean c) {
    }

    private static float getR() {
        return 0;
    }

    @Shadow
    private static float getG() {
        return 0;
    }

    @Shadow
    private static float getB() {
        return 0;
    }

    @Shadow
    abstract int i(String n);

//    @Shadow
//    private static boolean kk2;
//
//    @Shadow
//    private static int kk;

    /**
     * Methods Below so we don't need
     * to constantly scan stack traces
     */
    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;getPlayerColor(IIIIIZZZZZ)I", shift = At.Shift.BEFORE))
    private void setFromRenderPlayerJBRA(AbstractClientPlayer abstractClientPlayer, float par2, CallbackInfo ci) {
        ClientCache.fromRenderPlayerJBRA = true;
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;getPlayerColor(IIIIIZZZZZ)I", shift = At.Shift.AFTER))
    private void clearFromRenderPlayerJBRA(AbstractClientPlayer abstractClientPlayer, float par2, CallbackInfo ci) {
        ClientCache.fromRenderPlayerJBRA = false;
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;getPlayerColor2(IIIIIZZZZ)I", shift = At.Shift.BEFORE))
    private void setFromRenderPlayer2JBRA(AbstractClientPlayer abstractClientPlayer, float par2, CallbackInfo ci) {
        ClientCache.fromRenderPlayerJBRA = true;
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;getPlayerColor2(IIIIIZZZZ)I", shift = At.Shift.AFTER))
    private void clearFromRenderPlayer2JBRA(AbstractClientPlayer abstractClientPlayer, float par2, CallbackInfo ci) {
        ClientCache.fromRenderPlayerJBRA = false;
    }

//    @Inject(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;DBCsizeBasedOnRace(IIZ)F", shift = At.Shift.BEFORE), remap = true)
//    public void setCurrentlyRenderedJRMCTickPlayer(AbstractClientPlayer p, float p_77041_2_, CallbackInfo ci) {
//        CommonProxy.setCurrentJRMCTickPlayer(p);
//    }

    @Inject(method = "kss", at = @At("HEAD"), remap = false)
    private static void disableLightMapForKiBlade(Entity e, boolean b, int id, int kf, int ki, CallbackInfo ci) {
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
    }

    @Inject(method = "kss", at = @At("RETURN"), remap = false)
    private static void reEnableLightMapAfterKiBlade(Entity e, boolean b, int id, int kf, int ki, CallbackInfo ci) {
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
    }

//    @Redirect(method = "kss", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;getPlayerColor2(IIIIIZZZZ)I"))
//    private static int fixKiBladeColor(int t, int d, int p, int r, int s, boolean v, boolean y, boolean ui, boolean gd, @Local(name = "e") Entity entity) {
//        if (!(entity instanceof EntityPlayer))
//            return JRMCoreHDBC.getPlayerColor2(t, d, p, r, s, v, y, ui, gd);
//
//        DBCData dbcData = DBCData.get((EntityPlayer) entity);
//        if (dbcData.getForm() != null) {
//            FormDisplay formDisplay = dbcData.getForm().display;
//            if (formDisplay.auraColor != -1) {
//                return formDisplay.auraColor;
//            }
//            if (formDisplay.getAura() != null) {
//                AuraDisplay formAuraDisplay = ((AuraDisplay) formDisplay.getAura().getDisplay());
//                if (formAuraDisplay.color1 != -1)
//                    return formAuraDisplay.color1;
//            }
//        }
//        if (dbcData.getAura() != null) {
//            AuraDisplay auraDisplay = dbcData.getAura().display;
//            boolean overrideDBC = (auraDisplay.overrideDBCAura && !auraDisplay.copyDBCSuperformColors) || (dbcData.getRace() == 4 ? dbcData.State <= 4 : dbcData.State == 0);
//            if (overrideDBC) {
//                return KiWeaponData.getColorByAuraType(auraDisplay);
//            }
//        }
//
//        return JRMCoreHDBC.getPlayerColor2(t, d, p, r, s, v, y, ui, gd);
//    }
}
