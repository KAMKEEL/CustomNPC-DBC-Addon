package kamkeel.npcdbc.mixins.late.impl.npc.client;

import kamkeel.npcdbc.client.ClientConstants;
import kamkeel.npcdbc.client.model.ModelDBC;
import kamkeel.npcdbc.client.render.OutlineRenderer;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.RenderingData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.mixins.late.IModelMPM;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.client.model.ModelNPCMale;
import noppes.npcs.client.model.part.ModelLegs;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

import static kamkeel.npcdbc.client.shader.PostProcessing.*;
import static org.lwjgl.opengl.GL11.*;
import static kamkeel.npcdbc.data.form.OverlayManager.Type.*;

@Mixin(value = ModelMPM.class, remap = false)
public abstract class MixinModelMPM extends ModelNPCMale implements IModelMPM {

    @Shadow
    public boolean isArmor;
    @Unique
    public ModelDBC NPCDBCModel;

    @Shadow
    private ModelLegs legs;


    public MixinModelMPM(float f) {
        super(f);
    }

    public MixinModelMPM(float f, boolean alex) {
        super(f, alex);
    }

    @Inject(method = "<init>(FZ)V", at = @At("RETURN"))
    private void addDBCModel(float par1, boolean alex, CallbackInfo ci) {
        NPCDBCModel = new ModelDBC((ModelMPM) (Object) this, alex);
    }

    @Inject(method = "<init>(FI)V", at = @At("RETURN"))
    private void addDBCModelAlex(float par1, int alex, CallbackInfo ci) {
        NPCDBCModel = new ModelDBC((ModelMPM) (Object) this, alex == 1);
    }


    @Inject(method = "setPlayerData", at = @At("RETURN"))
    private void setPartData(EntityCustomNpc entity, CallbackInfo ci) {
        this.NPCDBCModel.setPlayerData(entity);
        if (!isArmor)
            this.NPCDBCModel.setHurt(entity);
        if (isArmor)
            this.NPCDBCModel.clearHurt();
    }

    @Inject(method = "render", at = @At(value = "HEAD"), remap = true)
    private void rotationKeep(Entity par1Entity, float p1, float p2, float p3, float p4, float p5, float p6, CallbackInfo ci) {
        NPCDBCModel.rot1 = p1;
        NPCDBCModel.rot2 = p2;
        NPCDBCModel.rot3 = p3;
        NPCDBCModel.rot4 = p4;
        NPCDBCModel.rot5 = p5;
        NPCDBCModel.rot6 = p6;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/ModelMPM;renderCloak(Lnoppes/npcs/entity/EntityCustomNpc;F)V", shift = At.Shift.AFTER), remap = true)
    private void outline(Entity entity, float p1, float p2, float p3, float p4, float p5, float p6, CallbackInfo ci) {

        EntityCustomNpc npc = (EntityCustomNpc) entity;
        DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
        if (!display.enabled || !display.useStencilBuffer)
            return;

        float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;
        mc.entityRenderer.disableLightmap(0);
        ////////////////////////////////////////
        ////////////////////////////////////////
        //Outline
        Outline outline = (Outline) display.getOutline();
        if (outline != null && ConfigDBCClient.EnableOutlines) {
            startBlooming(ClientConstants.renderingGUI);
            glStencilFunc(GL_GREATER, entity.getEntityId() % 256, 0xFF);  // Test stencil value
            glStencilMask(0xff);
            OutlineRenderer.renderOutlineNPC((ModelMPM) (Object) this, outline, (EntityCustomNpc) entity, display, partialTicks);
            endBlooming();
        }

        ////////////////////////////////////////
        ////////////////////////////////////////
        RenderEventHandler.enableStencilWriting(entity.getEntityId() % 256);
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

    }

    @Unique
    private DBCDisplay display;
    @Unique
    private Form form;
    @Inject(method = "renderHead", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", ordinal = 2, shift = At.Shift.BEFORE, remap = true), cancellable = true)
    private void renderDBCHead(EntityCustomNpc entity, float f, CallbackInfo ci) {
        display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!isArmor && display.enabled) {
            //#TODO:Both lead to face and body overlays, only choose one
            NPCDBCModel.renderFace(entity, display, bipedHead);
            NPCDBCModel.renderBodySkin(display, bipedHead);
        }
    }

    @Inject(method = "renderHead", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", ordinal = 2, shift = At.Shift.AFTER, remap = true))
    private void postRenderDBCHead(EntityCustomNpc entity, float f, CallbackInfo ci) {
        if (!isArmor && display.enabled) {
            form = display.getForm();
            if (form != null && form.display.overlays.enabled) {
                NPCDBCModel.currentRenderingData = RenderingData.from(display);
                NPCDBCModel.DBCHair.isHidden = true; //Hair renders by default with head, so not needed in overlay
                NPCDBCModel.renderFormOverlays(form, display, bipedHead, EnumSet.of(ALL, Face));
                NPCDBCModel.DBCHair.isHidden = false;
            }
        }
    }

    @Inject(method = "renderBody", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", shift = At.Shift.BEFORE, remap = true), cancellable = true)
    private void renderDBCBody(EntityCustomNpc entity, float f, CallbackInfo ci) {
        display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!isArmor && display.enabled) {
            NPCDBCModel.renderBodySkin(display, bipedBody);
        }
    }

    @Inject(method = "renderBody", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", shift = At.Shift.AFTER, remap = true))
    private void postRenderDBCBody(EntityCustomNpc entity, float f, CallbackInfo ci) {
        if (!isArmor && display.enabled) {
            if (form != null && form.display.overlays.enabled) {
                NPCDBCModel.renderFormOverlays(form, display, bipedBody, EnumSet.of(ALL, Chest));
            }
        }
    }


    @Inject(method = "renderLegs", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/part/ModelLegs;render(F)V", shift = At.Shift.BEFORE, remap = true), cancellable = true)
    private void renderDBCLegs(EntityCustomNpc entity, float f, CallbackInfo ci) {
        display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!isArmor && display.enabled)
            NPCDBCModel.renderBodySkin(display, legs);
    }

    @Inject(method = "renderLegs", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/part/ModelLegs;render(F)V", shift = At.Shift.AFTER, remap = true))
    private void postRenderDBCLegs(EntityCustomNpc entity, float f, CallbackInfo ci) {
        if (!isArmor && display.enabled) {
            if (form != null && form.display.overlays.enabled) {
                NPCDBCModel.renderFormOverlays(form, display, legs, EnumSet.of(ALL, Legs));
            }
        }
    }


    @Inject(method = "renderArms", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", ordinal = 0, shift = At.Shift.BEFORE, remap = true), cancellable = true)
    private void renderDBCLeftArm(EntityCustomNpc entity, float f, boolean bo, CallbackInfo ci) {
        display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!isArmor && display.enabled)
            NPCDBCModel.renderBodySkin(display, bipedLeftArm);
    }

    @Inject(method = "renderArms", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", ordinal = 0, shift = At.Shift.AFTER, remap = true))
    private void postRenderDBCLeftArm(EntityCustomNpc entity, float f, boolean bo, CallbackInfo ci) {
        if (!isArmor && display.enabled) {
            if (form != null && form.display.overlays.enabled) {
                NPCDBCModel.renderFormOverlays(form, display, bipedLeftArm, EnumSet.of(ALL, Arms, LeftArm));
            }
        }
    }


    @Inject(method = "renderArms", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", ordinal = 1, shift = At.Shift.BEFORE, remap = true), cancellable = true)
    private void renderDBCRightArm(EntityCustomNpc entity, float f, boolean bo, CallbackInfo ci) {
        display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!isArmor && display.enabled)
            NPCDBCModel.renderBodySkin(display, bipedRightArm);
    }

    @Inject(method = "renderArms", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", ordinal = 1, shift = At.Shift.AFTER, remap = true))
    private void postRenderDBCRightArm(EntityCustomNpc entity, float f, boolean bo, CallbackInfo ci) {
        if (!isArmor && display.enabled) {
            if (form != null && form.display.overlays.enabled) {
                NPCDBCModel.renderFormOverlays(form, display, bipedRightArm, EnumSet.of(ALL, Arms, RightArm));
            }
        }
    }


    @Inject(method = "setRotationAngles", at = @At("TAIL"))
    public void rotationAndAngle(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity, CallbackInfo ci) {
        if (!isArmor) {
            NPCDBCModel.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/ModelMPM;renderCloak(Lnoppes/npcs/entity/EntityCustomNpc;F)V", shift = At.Shift.AFTER, remap = true))
    public void renderKiWeapon(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7, CallbackInfo ci) {
        if (this.isArmor) {
            return;
        }

        EntityCustomNpc npc = (EntityCustomNpc) par1Entity;
        if (npc.modelData.hideArms < 0 || npc.modelData.hideArms > 3) {
            return;
        }

        NPCDBCModel.renderEnabledKiWeapons(par7);
    }

    @Unique
    @Override
    public ModelDBC getDBCModel() {
        return NPCDBCModel;
    }

    @Unique
    @Override
    public ModelBase getMainModel() {
        return null;
    }
}
