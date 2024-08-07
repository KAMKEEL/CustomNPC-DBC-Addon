package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.items.ItemPotara;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
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

    @Inject(method = "renderFirstPersonArm", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V",  ordinal = 11, shift = At.Shift.AFTER))
    public void postRenderArm(EntityPlayer par1EntityPlayer, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new DBCPlayerEvent.RenderArmEvent.Post(par1EntityPlayer, (RenderPlayerJBRA) (Object) this, Minecraft.getMinecraft().timer.renderPartialTicks));
    }

    @ModifyArgs(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glScalef(FFF)V", ordinal = 1), remap = true)
    protected void setDamage(Args args, @Local(ordinal = 0) LocalRef<AbstractClientPlayer> player) {
        if (mc.theWorld == null)
            return;
        DBCData.get(player.get()).XZSize = args.get(0);
        DBCData.get(player.get()).YSize = args.get(1);
    }

    @Inject(method = "glColor3f(I)V", at = @At("HEAD"), cancellable = true)
    private static void mixAuraColor(int c, CallbackInfo ci) {
        EntityPlayer player = ClientEventHandler.renderingPlayer;
        if (ClientEventHandler.renderingPlayer == null || DBCData.get(player) == null)
            return;
        EntityAura aura = DBCData.get(player).auraEntity;
        if (aura == null || aura.color1 == -1 || !aura.shouldRender())
            return;

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

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 0, shift = At.Shift.AFTER))
    private void changeFormData(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci, @Local(name = "hairback") LocalIntRef hairback, @Local(name = "race") LocalIntRef race, @Local(name = "rg") LocalIntRef rg, @Local(name = "st") LocalIntRef st, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "bodyc1") LocalIntRef bodyC1, @Local(name = "bodyc2") LocalIntRef bodyC2, @Local(name = "bodyc3") LocalIntRef bodyC3, @Local(name = "msk") LocalBooleanRef mask) {
        Form form = DBCData.getForm(par1AbstractClientPlayer);
        //this prevents ssj2 hair animating immediately into ssj1 when going into ssj1 type forms from base
        if (TransformController.rage > 0) {
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
                if (form.display.bodyType.equals("firstform"))
                    st.set(0);
                else if (form.display.bodyType.equals("secondform"))
                    st.set(2);
                else if (form.display.bodyType.equals("thirdform"))
                    st.set(3);
                else if (form.display.bodyType.equals("finalform"))
                    st.set(4);
                else if (form.display.bodyType.equals("ultimatecooler"))
                    st.set(5);

                if (form.display.hasArcoMask())
                    mask.set(true);
                else
                    mask.set(false);

                //render ssj3 hair for humans/majins
            } else if (form.display.hairType.equals("ssj3") && (race.get() == 0 || race.get() == 5)) {

                String hairTexture = "normall.png";
                TextureManager texMan = Minecraft.getMinecraft().renderEngine;
                texMan.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + hairTexture));
                this.modelMain.renderHairs(0.0625F, "D01");
            }
        }
    }


    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;HHWHO:Z", ordinal = 1, shift = At.Shift.BEFORE))
    private void renderSaiyanStates(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo
            ci, @Local(name = "pl") LocalIntRef pl, @Local(name = "bodycm") LocalIntRef
                                            bodyCM, @Local(name = "hairback") LocalIntRef hairback, @Local(name = "race") LocalIntRef
                                            race, @Local(name = "gen") LocalIntRef gender, @Local(name = "facen") LocalIntRef nose) {
        Form form = DBCData.getForm(par1AbstractClientPlayer);
        DBCData data = DBCData.get(par1AbstractClientPlayer);
        if (form != null) {
            HD = ConfigDBCClient.EnableHDTextures;
            //only saiyans
            if (race.get() == 1 || race.get() == 2) {
                if(form.display.hasBodyFur())
                    renderBodyFur(form, gender.get(), bodyCM.get(), data.DNS);

                //renders all ssj4
                if (form.display.hairType.equals("ssj4")) {
                    renderSSJ4Face(form, gender.get(), nose.get(), bodyCM.get(), data.renderingHairColor, data.age,  data.DNS);
                    if (hairback.get() != 12)
                        this.modelMain.renderHairsV2(0.0625F, "", 0.0F, 0, 0, pl.get(), race.get(), (RenderPlayerJBRA) (Object) this, par1AbstractClientPlayer);
                    //all oozaru rendering
                } else if (form.display.hairType.equals("oozaru")) {
                    renderOozaru(bodyCM.get(), form.display.eyeColor, form.display.getFurColor(data.DNS));
                    //ssj3 hair rendering
                } else if (form.display.hairType.equals("ssj3")) {
                    String hairTexture = "normall.png";
                    TextureManager texMan = Minecraft.getMinecraft().renderEngine;
                    texMan.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:gui/") + hairTexture));
                    this.modelMain.renderHairs(0.0625F, "" + JRMCoreH.HairsT[6] + JRMCoreH.Hairs[0]);
                }

            }
        }

    }

    @Redirect(method = "renderEquippedItemsJBRA", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;HHWHO:Z", opcode = Opcodes.GETSTATIC))
    public boolean fixPotaraHair(@Local(name = "abstractClientPlayer") AbstractClientPlayer abstractClientPlayer) {
        ItemStack item = abstractClientPlayer.getCurrentArmor(3);

        if (item != null && item.getItem() instanceof ItemPotara)
            return false;

        return JRMCoreConfig.HHWHO;
    }


    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;HHWHO:Z", ordinal = 1, shift = At.Shift.AFTER))
    public void captureDefaultHairColor(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci, @Local(name = "hc") LocalIntRef hairCol) {
        DBCData data = DBCData.get(par1AbstractClientPlayer);
        data.renderingHairColor = hairCol.get();
        data.age = age;
    }

    @Unique
    private void renderBodyFur(Form form, int gender, int bodyCM, String dns) {
        String bodyTexture = (gender == 1 ? "f" : "") + "hum.png";
        this.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:cc/") + bodyTexture));
        RenderPlayerJBRA.glColor3f(bodyCM);
        this.modelMain.renderBody(0.0625F);

        this.bindTexture(new ResourceLocation(HD ? HDDir + "ssj4/ss4b.png" : "jinryuudragonbc:cc/ss4b.png"));
        RenderPlayerJBRA.glColor3f(form.display.getFurColor(dns));
        this.modelMain.renderBody(0.0625F);
    }

    @Unique
    private void renderSSJ4Face(Form form, int gender, int nose, int bodyCM, int defaultHairColor, float age, String dns) {
        if (ConfigDBCClient.EnableHDTextures) {
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4eyewhite.png"));
            this.modelMain.renderBody(1F / 16F);
            RenderPlayerJBRA.glColor3f(form.display.eyeColor);
            this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4pupils.png"));
            this.modelMain.renderBody(0.0625F);
            RenderPlayerJBRA.glColor3f(form.display.getFurColor(dns));
            this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4brows.png"));
            this.modelMain.renderBody(1F / 16F);

            int hairColor = form.display.hairColor;
            RenderPlayerJBRA.glColor3f(hairColor < 0 ? defaultHairColor : hairColor, age);
            this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4brows2.png"));
            this.modelMain.renderBody(1F / 16F);
            RenderPlayerJBRA.glColor3f(bodyCM);
            this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4mouth0.png"));
            this.modelMain.renderBody(1F / 16F);
            this.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4shade.png"));
            this.modelMain.renderBody(0.0625F);
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
    private void changeFormArmData(EntityPlayer par1EntityPlayer, CallbackInfo
            ci, @Local(name = "race") LocalIntRef race, @Local(name = "State") LocalIntRef
                                           st, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "bodyc1") LocalIntRef
                                           bodyC1, @Local(name = "bodyc2") LocalIntRef bodyC2, @Local(name = "bodyc3") LocalIntRef bodyC3) {
        Form form = DBCData.getForm(par1EntityPlayer);
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
                else if (form.display.bodyType.equals("finalform"))
                    st.set(4);
                else if (form.display.bodyType.equals("ultimatecooler"))
                    st.set(5);

            }
        }
    }

    @Inject(method = "renderFirstPersonArm", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/client/config/jrmc/JGConfigClientSettings;CLIENT_DA19:Z", ordinal = 0, shift = At.Shift.BEFORE), remap = true)
    private void renderSaiyanArm(EntityPlayer par1EntityPlayer, CallbackInfo ci, @Local(name = "race") LocalIntRef
            race, @Local(name = "id") LocalIntRef id, @Local(name = "bodycm") LocalIntRef
                                         bodyCM, @Local(name = "gen") LocalIntRef gender) {

        Form form = DBCData.getForm(par1EntityPlayer);
        if (form != null && (race.get() == 1 || race.get() == 2)) {
            if (this.renderManager != null && this.renderManager.renderEngine != null) {
                String dns = DBCData.get(par1EntityPlayer).DNS;
                if (form.display.hasBodyFur) {
                    renderSSJ4Arm(form, par1EntityPlayer, id.get(), gender.get(), bodyCM.get(), dns);
                } else if (form.display.hairType.equals("oozaru")) {
                    renderOozaruArm(form, par1EntityPlayer, id.get(), bodyCM.get(), dns);
                }
            }
        }

    }


    @Unique
    private void renderSSJ4Arm(Form form, EntityPlayer player, int id, int gender, int bodyCM, String dns) {
        String bodyTexture = (gender == 1 ? "f" : "") + "hum.png";
        this.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:cc/") + bodyTexture));
        RenderPlayerJBRA.glColor3f(bodyCM);
        renderArm(id, player);

        this.bindTexture(new ResourceLocation(HD ? HDDir + "ssj4/ss4b.png" : "jinryuudragonbc:cc/ss4b.png"));
        RenderPlayerJBRA.glColor3f(form.display.getFurColor(dns));
        renderArm(id, player);

    }


    @Unique
    private void renderOozaruArm(Form form, EntityPlayer player, int id, int bodyCM, String dns) {
        ResourceLocation bdyskn = new ResourceLocation((HD ? HDDir + "oozaru/" : "jinryuudragonbc:cc/") + "oozaru1.png");
        this.bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(bodyCM);
        renderArm(id, player);

        bdyskn = new ResourceLocation((HD ? HDDir + "oozaru/" : "jinryuudragonbc:cc/") + "oozaru2.png");
        this.bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(form.display.getFurColor(dns));
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
        CommonProxy.CurrentJRMCTickPlayer = p;
    }

}
