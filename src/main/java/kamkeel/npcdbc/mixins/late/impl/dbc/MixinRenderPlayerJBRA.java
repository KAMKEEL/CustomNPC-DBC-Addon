package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHDBC;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.gui.hud.formWheel.HUDFormWheel;
import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.KiWeaponData;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.items.ItemPotara;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.client.ClientEventHandler;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = RenderPlayerJBRA.class, remap = false)
public abstract class MixinRenderPlayerJBRA extends RenderPlayer {
    @Shadow
    private static float age;
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

    @Shadow
    public static float r;

    @Shadow
    public static float b;

    @Shadow
    public static float g;

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 0, shift = At.Shift.BEFORE))
    public void customKaiokenTint(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci) {
        DBCData data = DBCData.get(par1AbstractClientPlayer);
        if (JRMCoreH.DBC() && kk2) {
            Aura aura = data.getAura();
            if (aura != null && aura.display.kaiokenColor != -1) {
                Color tint = new Color(aura.display.kaiokenColor);
                r = tint.getRedF() * kk / 15.0F;
                g = tint.getGreenF() * kk / 15.0F;
                b = tint.getBlueF() * kk / 15.0F;
            }
        }
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

    @Redirect(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glScalef(FFF)V", ordinal = 1), remap = true)
    protected void setDamage(float x, float y, float z, @Local(ordinal = 0) LocalRef<AbstractClientPlayer> player) {
        if (mc.theWorld == null)
            return;
        DBCData.get(player.get()).XZSize = x;
        DBCData.get(player.get()).YSize = y;
        if (HUDFormWheel.renderingPlayer && y > 0.8f) {
            float f = ConfigDBCClient.AlteranteSelectionWheelTexture ? 6f : 4f;
            GL11.glTranslatef(0, y / f, 0);
        }

        GL11.glScalef(x, y, z);
    }

    @Inject(method = "glColor3f(I)V", at = @At("HEAD"), cancellable = true)
    private static void mixAuraColor(int c, CallbackInfo ci) {
        EntityPlayer player = ClientEventHandler.renderingPlayer;

        if (ClientEventHandler.renderingPlayer == null)
            return;

        DBCData data = DBCData.get(player);
        if (data == null)
            return;

        if (JRMCoreH.DBC() && kk2) {
            Aura aura = data.getAura();
            if (aura != null && aura.display.kaiokenColor != -1) {
                Color.lerpRGB(c, aura.display.kaiokenColor, kk / 15f).glColor();
                ci.cancel();
                return;
            }
        }

        EntityAura aura = data.auraEntity;
        if (!HUDFormWheel.renderingPlayer && aura != null && aura.color1 != -1 && aura.shouldRender()) {

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


        if (JRMCoreH.DBC() && kk2) {
            Aura aura = data.getAura();
            if (aura != null && aura.display.kaiokenColor != -1) {
                Color col = new Color(r.get(), g.get(), b.get(), -1);
                col.lerpRGB(aura.display.kaiokenColor, kk / 40f).glColor();
                ci.cancel();
                return;
            }
        }
    }

    @Redirect(method = "func_130009_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isSneaking()Z"))
    private boolean isSneaking(AbstractClientPlayer instance) {
        if (HUDFormWheel.renderingPlayer)
            return false;
        return instance.isSneaking();
    }

    @Redirect(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;data2:[Ljava/lang/String;"), remap = true)
    private String[] changeFormDAata(@Local(name = "pl") int pl, @Local(ordinal = 0) AbstractClientPlayer player) {
        DBCData data = DBCData.get(player);
        if (data.State > 0) {
            Form form = data.getForm();
            if (form != null && !form.stackable.vanillaStackable)
                data.State = 0;
        }
        if (!ClientProxy.isRenderingWorld() || CustomNpcPlusDBC.proxy.isRenderingGUI())
            ClientProxy.lastRendererGUIPlayerID = pl;
        else
            ClientProxy.lastRendererGUIPlayerID = -1;
        JRMCoreH.data2[pl] = data.State + ";" + data.State2;
        return JRMCoreH.data2;
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "FIELD", target = "LJinRyuu/JBRA/RenderPlayerJBRA;kk2:Z", ordinal = 1))
    private void changeFormDAata4(AbstractClientPlayer p, float p_77041_2_, CallbackInfo ci) {
        DBCData data = DBCData.get(p);
        if (HUDFormWheel.renderingPlayer) {
            kk2 = data.renderKK;
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
        if (HUDFormWheel.renderingPlayer) {
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
            if (form.display.hasColor("bodycm"))
                bodyCM.set(form.display.bodyCM);
            if (form.display.hasColor("bodyC1"))
                bodyC1.set(form.display.bodyC1);
            if (form.display.hasColor("bodyC2"))
                bodyC2.set(form.display.bodyC2);
            if (form.display.hasColor("bodyC3"))
                bodyC3.set(form.display.bodyC3);

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

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;HHWHO:Z", ordinal = 1, shift = At.Shift.BEFORE))
    private void renderSaiyanStates(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci, @Local(name = "hc") LocalIntRef hairCol, @Local(name = "pl") LocalIntRef pl, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "hairback") LocalIntRef hairback, @Local(name = "race") LocalIntRef race, @Local(name = "gen") LocalIntRef gender, @Local(name = "facen") LocalIntRef nose) {
        Form form = DBCData.getForm(par1AbstractClientPlayer);
        DBCData data = DBCData.get(par1AbstractClientPlayer);

        /**
         * @INFO: This fixes base hair color.
         */
        data.renderingHairColor = hairCol.get();

        if (form != null) {
            HD = ConfigDBCClient.EnableHDTextures;
            //only saiyans
            if (race.get() == 1 || race.get() == 2) {
                boolean isSSJ4 = form.display.hairType.equals("ssj4");
                if (form.display.hasBodyFur() || isSSJ4)
                    renderBodyFur(form, gender.get(), bodyCM.get(), data, data.skinType);

                //renders all ssj4
                if (isSSJ4) {
                    if (form.display.hasEyebrows && data.skinType != 0)
                        renderSSJ4Face(form, gender.get(), nose.get(), bodyCM.get(), data.renderingHairColor, age, data.DNS, data);
                    if (hairback.get() != 12)
                        this.modelMain.renderHairsV2(0.0625F, "", 0.0F, 0, 0, pl.get(), race.get(), (RenderPlayerJBRA) (Object) this, par1AbstractClientPlayer);
                    //all oozaru rendering
                } else if (form.display.hairType.equals("oozaru")) {
                    renderOozaru(bodyCM.get(), form.display.eyeColor, form.display.getFurColor(data));
                    //ssj3 hair rendering
                } else if (form.display.hairType.equals("ssj3") || form.display.hairType.equals("raditz")) {
                    String hairTexture = "normall.png";
                    TextureManager texMan = Minecraft.getMinecraft().renderEngine;
                    texMan.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + hairTexture));
                    String hair = form.display.hairType.equals("raditz") ? "D" : "D01";
                    this.modelMain.renderHairs(0.0625F, hair);
                }
            }
        }
    }

    @Redirect(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;onGround:Z", remap = true), remap = true)
    public boolean fixP2otaraHair(AbstractClientPlayer instance) {
        if (HUDFormWheel.renderingPlayer)
            return true;
        return instance.onGround;
    }

    @Redirect(method = "renderEquippedItemsJBRA", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;HHWHO:Z", opcode = Opcodes.GETSTATIC))
    public boolean fixPotaraHair(@Local(name = "abstractClientPlayer") AbstractClientPlayer abstractClientPlayer) {
        ItemStack item = abstractClientPlayer.getCurrentArmor(3);

        if (item != null && item.getItem() instanceof ItemPotara)
            return false;

        return JRMCoreConfig.HHWHO;
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "LJinRyuu/JBRA/ModelBipedDBC;renderHairs(FLjava/lang/String;)V", ordinal = 34, shift = At.Shift.BEFORE))
    public void beforeMajinSE(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci) {
        ClientProxy.renderingMajinSE = true;
    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "LJinRyuu/JBRA/ModelBipedDBC;renderHairs(FLjava/lang/String;)V", ordinal = 34, shift = At.Shift.AFTER))
    public void afterMajinSE(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci) {
        ClientProxy.renderingMajinSE = false;
    }

    @Unique
    private void renderBodyFur(Form form, int gender, int bodyCM, DBCData data, int skintype) {
        if (skintype != 0) {
            String bodyTexture = (gender == 1 ? "f" : "") + "hum.png";
            this.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:cc/") + bodyTexture));
            RenderPlayerJBRA.glColor3f(bodyCM);
            this.modelMain.renderBody(0.0625F);
        }

        String fur = "ss4" + (skintype == 0 ? "a" : "b") + ".png";
        this.bindTexture(new ResourceLocation(HD ? HDDir + "ssj4/" + fur : "jinryuudragonbc:cc/" + fur));
        RenderPlayerJBRA.glColor3f(form.display.getFurColor(data));
        this.modelMain.renderBody(0.0625F);
    }

    @Unique
    private void renderSSJ4Face(Form form, int gender, int nose, int bodyCM, int defaultHairColor, float age, String dns, DBCData data) {
        if (ConfigDBCClient.EnableHDTextures) {
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4eyewhite.png"));
            this.modelMain.bipedHead.render(1F / 16F);

            if (!form.display.isBerserk) {
                RenderPlayerJBRA.glColor3f(form.display.eyeColor == -1 ? 0xF3C807 : form.display.eyeColor);
                this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4pupils.png"));
                this.modelMain.bipedHead.render(0.0625F);
            }
            RenderPlayerJBRA.glColor3f(form.display.getFurColor(data));
            this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4brows.png"));
            this.modelMain.bipedHead.render(1F / 16F);

            int hairColor = form.display.getHairColor(data);
            RenderPlayerJBRA.glColor3f(hairColor < 0 ? defaultHairColor : hairColor, age);
            this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4brows2.png"));
            this.modelMain.bipedHead.render(1F / 16F);
            RenderPlayerJBRA.glColor3f(bodyCM);
            this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4mouth0.png"));
            this.modelMain.bipedHead.render(1F / 16F);
            this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4shade.png"));
            this.modelMain.bipedHead.render(0.0625F);
        }

        RenderPlayerJBRA.glColor3f(bodyCM);
        String noseTexture = (gender == 1 ? "f" : "") + "humn" + nose + ".png";
        this.bindTexture(new ResourceLocation((HD ? HDDir + "base/nose/" : "jinryuumodscore:cc/") + noseTexture));
        this.modelMain.renderHairs(0.0625F, "FACENOSE");
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

    @Inject(method = "renderFirstPersonArm", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;DBC()Z", ordinal = 0, shift = At.Shift.AFTER), remap = true)
    private void changeFormArmData(EntityPlayer par1EntityPlayer, CallbackInfo ci, @Local(name = "race") LocalIntRef race, @Local(name = "State") LocalIntRef st, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "bodyc1") LocalIntRef bodyC1, @Local(name = "bodyc2") LocalIntRef bodyC2, @Local(name = "bodyc3") LocalIntRef bodyC3) {
        Form form = DBCData.getForm(par1EntityPlayer);
        ClientEventHandler.renderingPlayer = par1EntityPlayer;
        if (form != null) {

            //arm colors
            if (form.display.hasColor("bodycm"))
                bodyCM.set(form.display.bodyCM);
            if (form.display.hasColor("bodyC1"))
                bodyC1.set(form.display.bodyC1);
            if (form.display.hasColor("bodyC2"))
                bodyC2.set(form.display.bodyC2);
            if (form.display.hasColor("bodyC3"))
                bodyC3.set(form.display.bodyC3);

            //arm bodytype for arcosian
            if (race.get() == 4) {
                if (form.display.bodyType.equals("firstform"))
                    st.set(0);
                else if (form.display.bodyType.equals("secondform"))
                    st.set(2);
                else if (form.display.bodyType.equals("thirdform"))
                    st.set(3);
                else if (form.display.bodyType.equals("finalform") || form.display.bodyType.equals("golden"))
                    st.set(4);
                else if (form.display.bodyType.equals("ultimatecooler"))
                    st.set(5);
            }
        }
    }

    @Inject(method = "renderFirstPersonArm", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;dnsGender(Ljava/lang/String;)I", ordinal = 0, shift = At.Shift.BEFORE, remap = false), remap = true)
    private void stopMonkeeArmInWrongForm(EntityPlayer par1EntityPlayer, CallbackInfo ci, @Local(name = "saiOozar") LocalBooleanRef isOozaru, @Local(name = "race") int race) {
        Form form = DBCData.getForm(par1EntityPlayer);

        if (form != null && (race == 1 || race == 2)) {
            if (!form.stackable.vanillaStackable)
                isOozaru.set(false);
        }
    }

    @Inject(method = "renderFirstPersonArm", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/client/config/jrmc/JGConfigClientSettings;CLIENT_DA19:Z", ordinal = 0, shift = At.Shift.BEFORE), remap = true)
    private void renderSaiyanArm(EntityPlayer par1EntityPlayer, CallbackInfo ci, @Local(name = "race") LocalIntRef race, @Local(name = "id") LocalIntRef id, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "gen") LocalIntRef gender) {

        Form form = DBCData.getForm(par1EntityPlayer);
        if (form != null && (race.get() == 1 || race.get() == 2)) {
            if (this.renderManager != null && this.renderManager.renderEngine != null) {
                if (form.display.hasBodyFur || form.display.hairType.equals("ssj4")) {
                    renderSSJ4Arm(form, par1EntityPlayer, id.get(), gender.get(), bodyCM.get(), DBCData.get(par1EntityPlayer));
                } else if (form.display.hairType.equals("oozaru")) {
                    renderOozaruArm(form, par1EntityPlayer, id.get(), bodyCM.get(), DBCData.get(par1EntityPlayer));
                }
            }
        }
    }

    @Unique
    private void renderSSJ4Arm(Form form, EntityPlayer player, int id, int gender, int bodyCM, DBCData data) {
        if (data.skinType != 0) {
            String bodyTexture = (gender == 1 ? "f" : "") + "hum.png";
            this.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:cc/") + bodyTexture));
            RenderPlayerJBRA.glColor3f(bodyCM);
            renderArm(id, player);
        }

        String fur = "ss4" + (data.skinType == 0 ? "a" : "b") + ".png";
        this.bindTexture(new ResourceLocation(HD ? HDDir + "ssj4/" + fur : "jinryuudragonbc:cc/" + fur));
        RenderPlayerJBRA.glColor3f(form.display.getFurColor(data));
        renderArm(id, player);
    }

    @Unique
    private void renderOozaruArm(Form form, EntityPlayer player, int id, int bodyCM, DBCData data) {
        ResourceLocation bdyskn = new ResourceLocation((HD ? HDDir + "oozaru/" : "jinryuudragonbc:cc/") + "oozaru1.png");
        this.bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(bodyCM);
        renderArm(id, player);

        bdyskn = new ResourceLocation((HD ? HDDir + "oozaru/" : "jinryuudragonbc:cc/") + "oozaru2.png");
        this.bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(form.display.getFurColor(data));
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

    @Shadow
    private static boolean kk2;

    @Shadow
    public static int kk;

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

    @Inject(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;DBCsizeBasedOnRace(IIZ)F", shift = At.Shift.BEFORE), remap = true)
    public void setCurrentlyRenderedJRMCTickPlayer(AbstractClientPlayer p, float p_77041_2_, CallbackInfo ci) {
        CommonProxy.setCurrentJRMCTickPlayer(p);
    }

    @Inject(method = "kss", at = @At("HEAD"), remap = false)
    private static void disableLightMapForKiBlade(Entity e, boolean b, int id, int kf, int ki, CallbackInfo ci) {
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
    }

    @Inject(method = "kss", at = @At("RETURN"), remap = false)
    private static void reEnableLightMapAfterKiBlade(Entity e, boolean b, int id, int kf, int ki, CallbackInfo ci) {
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
    }

    @Redirect(method = "kss", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;getPlayerColor2(IIIIIZZZZ)I"))
    private static int fixKiBladeColor(int t, int d, int p, int r, int s, boolean v, boolean y, boolean ui, boolean gd, @Local(name = "e") Entity entity) {
        if (!(entity instanceof EntityPlayer))
            return JRMCoreHDBC.getPlayerColor2(t, d, p, r, s, v, y, ui, gd);

        DBCData dbcData = DBCData.get((EntityPlayer) entity);
        if (dbcData.getForm() != null) {
            FormDisplay formDisplay = dbcData.getForm().display;
            if (formDisplay.auraColor != -1) {
                return formDisplay.auraColor;
            }
            if (formDisplay.getAura() != null) {
                AuraDisplay formAuraDisplay = ((AuraDisplay) formDisplay.getAura().getDisplay());
                if (formAuraDisplay.color1 != -1)
                    return formAuraDisplay.color1;
            }
        }
        if (dbcData.getAura() != null) {
            AuraDisplay auraDisplay = dbcData.getAura().display;
            boolean overrideDBC = (auraDisplay.overrideDBCAura && !auraDisplay.copyDBCSuperformColors) || (dbcData.getRace() == 4 ? dbcData.State <= 4 : dbcData.State == 0);
            if (overrideDBC) {
                return KiWeaponData.getColorByAuraType(auraDisplay);
            }
        }

        return JRMCoreHDBC.getPlayerColor2(t, d, p, r, s, v, y, ui, gd);
    }
}
