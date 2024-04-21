package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreH;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderPlayerJBRA.class, remap = false)
public abstract class MixinRenderPlayerJBRA extends RenderPlayer {

    @Shadow
    public ModelBipedDBC modelMain;

    @Inject(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;DBCsizeBasedOnRace(IIZ)F", shift = At.Shift.BEFORE))
    public void setCurrentlyRenderedJRMCTickPlayer(AbstractClientPlayer p, float p_77041_2_, CallbackInfo ci) {
        CommonProxy.CurrentJRMCTickPlayer = p;

    }


    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 0, shift = At.Shift.AFTER))
    private void changeFormData(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci, @Local(name = "rg") LocalIntRef rg, @Local(name = "st") LocalIntRef st, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "bodyc1") LocalIntRef bodyC1, @Local(name = "bodyc2") LocalIntRef bodyC2, @Local(name = "bodyc3") LocalIntRef bodyC3) {

        CustomForm form = Utility.getFormClient(par1AbstractClientPlayer);
        if (TransformController.rage > 0) {
            if (TransformController.transformedInto != null) { //if just transformed into ssj type, dont display ssj2 hair
                if (TransformController.transformedInto.hairType.equals("ssj"))
                    rg.set(0);
            } else {
                rg.set((int) TransformController.rage);
            }


        }
        if (form != null) {
            st.set(0);
            if (form.hasBodyCM)
                bodyCM.set(form.bodyCM);
            if (form.hasBodyC1)
                bodyC1.set(form.bodyC1);
            if (form.hasBodyC2)
                bodyC2.set(form.bodyC2);
            if (form.hasBodyC3)
                bodyC3.set(form.bodyC3);


        }
    }


    @Inject(method = "renderFirstPersonArm", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;DBC()Z", ordinal = 0, shift = At.Shift.AFTER))
    private void changeFormArmData(EntityPlayer par1EntityPlayer, CallbackInfo ci, @Local(name = "State") LocalIntRef st, @Local(name = "bodycm") LocalIntRef bodyCM, @Local(name = "bodyc1") LocalIntRef bodyC1, @Local(name = "bodyc2") LocalIntRef bodyC2, @Local(name = "bodyc3") LocalIntRef bodyC3) {
        CustomForm form = Utility.getFormClient((AbstractClientPlayer) par1EntityPlayer);
        if (form != null) {
            st.set(0);
            if (form.hasBodyCM)
                bodyCM.set(form.bodyCM);
            if (form.hasBodyC1)
                bodyC1.set(form.bodyC1);
            if (form.hasBodyC2)
                bodyC2.set(form.bodyC2);
            if (form.hasBodyC3)
                bodyC3.set(form.bodyC3);
        }

    }

    @Inject(method = "renderEquippedItemsJBRA", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;HHWHO:Z", ordinal = 1, shift = At.Shift.BEFORE))
    private void renderSaiyanStates(AbstractClientPlayer par1AbstractClientPlayer, float par2, CallbackInfo ci, @Local(name = "pl") LocalIntRef pl, @Local(name = "bodycm")
    LocalIntRef bodyCM, @Local(name = "race") LocalIntRef race, @Local(name = "gen") LocalIntRef gender, @Local(name = "facen") LocalIntRef nose) {
        CustomForm form = Utility.getFormClient(par1AbstractClientPlayer);
        if (form != null) {
            if (form.hairType.equals("ssj4")) {
                renderSSJ4Face(form.hairColor, form.furColor, form.eyeColor, bodyCM.get(), gender.get(), nose.get());
                renderSSJ4Hair(form.hairColor, form.hairCode, race.get(), pl.get());
                renderSSJ4Fur(form.furColor);
            } else if (form.hairType.equals("oozaru")) {
                renderOozaru(bodyCM.get(), form.eyeColor, form.furColor);
            } else if (form.hairType.equals("ssj3"))
                this.modelMain.renderHairs(0.0625F, "" + JRMCoreH.HairsT[6] + JRMCoreH.Hairs[0]);

        }
    }

    @Inject(method = "renderFirstPersonArm", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/client/config/jrmc/JGConfigClientSettings;CLIENT_DA19:Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void renderSaiyanArm(EntityPlayer par1EntityPlayer, CallbackInfo ci, @Local(name = "id") LocalIntRef id, @Local(name = "bodycm") LocalIntRef bodyCM) {

        CustomForm form = Utility.getFormClient((AbstractClientPlayer) par1EntityPlayer);
        if (form != null) {
            if (form.hairType.equals("ssj4")) {
                renderSSJ4Arm(form.furColor, par1EntityPlayer, id.get());
            } else if (form.hairType.equals("oozaru")) {
                renderOozaruArm(bodyCM.get(), form.furColor, par1EntityPlayer, id.get());
            }
        }
    }

    @Unique
    private void renderSSJ4Face(int hairColor, int furColor, int eyeColor, int bodyCM, int gender, int nose) {
        if(ConfigDBCClient.EnableHDTextures){
            GL11.glColor3f(1.0F + getR(), 1.0F + getG(), 1.0F + getB());
            this.bindTexture(new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/hd/faces/ssj4/ssj4eyewhite.png"));
            this.modelMain.renderBody(1F / 16F);
            RenderPlayerJBRA.glColor3f(eyeColor);
            this.bindTexture(new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/hd/faces/ssj4/ssj4pupils.png"));
            this.modelMain.renderBody(0.0625F);
            RenderPlayerJBRA.glColor3f(furColor);
            this.bindTexture(new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/hd/faces/ssj4/ssj4brows.png"));
            this.modelMain.renderBody(1F / 16F);
            RenderPlayerJBRA.glColor3f(hairColor);
            this.bindTexture(new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/hd/faces/ssj4/ssj4brows2.png"));
            this.modelMain.renderBody(1F / 16F);
            RenderPlayerJBRA.glColor3f(bodyCM);
            this.bindTexture(new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/hd/faces/ssj4/ssj4mouth0.png"));
            this.modelMain.renderBody(1F / 16F);
            this.bindTexture(new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/hd/faces/ssj4/ssj4shade.png"));
            this.modelMain.renderBody(0.0625F);
        }
        else {

        }
        this.bindTexture(new ResourceLocation("jinryuumodscore", "cc/" + (gender == 1 ? "f" : "") + "humn" + nose + ".png"));
        this.modelMain.renderHairs(0.0625F, "FACENOSE");
    }

    @Unique
    private void renderSSJ4Hair(int hairCol, String hairCode, int race, int pl) {
        RenderPlayerJBRA.glColor3f(hairCol);
        this.bindTexture(new ResourceLocation("jinryuumodscore:gui/superall.png"));
        hairCode = !hairCode.isEmpty() ? hairCode : "373852546750347428545480193462285654801934283647478050340147507467501848505072675018255250726750183760656580501822475071675018255050716750189730327158501802475071675018973225673850189765616160501820414547655019545654216550195754542165501920475027655019943669346576193161503065231900475030655019406534276538199465393460501997654138655019976345453950189760494941501897615252415018976354563850189763494736501897614949395018976152523950189763525234501897584749395018976150493850189760545234501897585250415018885445474550189754475041501897545250435018885454523950185143607861501897415874585018514369196150185147768078391865525680565018974356806150188843567861501868396374615018975056805650189750568056501885582374615018975823726150187149568054501877495680565018774950785650189163236961501820";
        this.modelMain.renderHairsV2(0.0625F, hairCode, 0.0F, 0, 0, pl, race, (RenderPlayerJBRA) (Object) this);
    }

    @Unique
    private void renderSSJ4Fur(int furColor) {
        this.bindTexture(new ResourceLocation("jinryuudragonbc:cc/ss4b.png"));
        RenderPlayerJBRA.glColor3f(furColor);
        this.modelMain.renderBody(0.0625F);
        this.bindTexture(new ResourceLocation("jinryuudragonbc:cc/ss4b.png"));
        this.modelMain.renderBody(0.0625F);
    }


    @Unique
    private void renderSSJ4Arm(int furColor, EntityPlayer p, int id) {
        JRMCoreClient.mc.getTextureManager().bindTexture(new ResourceLocation("jinryuudragonbc:cc/ss4b.png"));
        RenderPlayerJBRA.glColor3f(furColor);
        this.modelMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, p);
        if (id == -1) {
            this.modelMain.RA.render(0.0625F);
        } else {
            this.func_aam(this.modelMain.RA, this.modelMain.LA, id, true);
            this.func_aam2(this.modelMain.RA, this.modelMain.LA, id, true);
        }
    }


    @Unique
    private void renderOozaru(int bodyCM, int eyeColor, int furColor) {
        ResourceLocation bdyskn = new ResourceLocation("jinryuudragonbc:cc/oozaru1.png"); //human hairless face
        this.bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(bodyCM);
        this.modelMain.renderBody(0.0625F);
        bdyskn = new ResourceLocation("jinryuudragonbc:cc/oozaru2.png"); //the fur
        this.bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(furColor);
        this.modelMain.renderBody(0.0625F);
        if(ConfigDBCClient.EnableHDTextures){
            this.bindTexture(new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/hd/faces/oozaru/oozarueyes.png")); //eyes
            RenderPlayerJBRA.glColor3f(eyeColor);
            this.modelMain.renderHairs(0.0625F, "EYEBASE");
            RenderPlayerJBRA.glColor3f(bodyCM); //
            this.modelMain.renderHairs(0.0625F, "OOZARU");
        } else {

        }
    }


    @Unique
    private void renderOozaruArm(int bodyCM, int furColor, EntityPlayer player, int id) {
        ResourceLocation bdyskn = new ResourceLocation("jinryuudragonbc:cc/oozaru1.png");
        JRMCoreClient.mc.getTextureManager().bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(bodyCM);
        this.modelMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
        if (id == -1) {
            this.modelMain.RA.render(0.0625F);
        } else {
            this.func_aam(this.modelMain.RA, this.modelMain.LA, id, true);
            this.func_aam2(this.modelMain.RA, this.modelMain.LA, id, true);
        }

        bdyskn = new ResourceLocation("jinryuudragonbc:cc/oozaru2.png");
        JRMCoreClient.mc.getTextureManager().bindTexture(bdyskn);
        RenderPlayerJBRA.glColor3f(furColor);
        this.modelMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
        if (id == -1) {
            this.modelMain.RA.render(0.0625F);
        } else {
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


    @Shadow
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


}
