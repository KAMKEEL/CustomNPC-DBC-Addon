package kamkeel.npcdbc.mixins.late.impl.npc.client;

import kamkeel.npcdbc.client.model.ModelDBC;
import kamkeel.npcdbc.client.render.OutlineRenderer;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.mixins.early.IEntityMC;
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

import static kamkeel.npcdbc.client.shader.PostProcessing.*;
import static org.lwjgl.opengl.GL11.*;

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
    private void addDBCModel(CallbackInfo ci) {
        NPCDBCModel = new ModelDBC((ModelMPM) (Object) this);
    }

    @Inject(method = "<init>(FI)V", at = @At("RETURN"))
    private void addDBCModelAlex(CallbackInfo ci) {
        NPCDBCModel = new ModelDBC((ModelMPM) (Object) this);
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
        if (!display.enabled)
            return;

        float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;
        mc.entityRenderer.disableLightmap(0);
        ////////////////////////////////////////
        ////////////////////////////////////////
        //Outline
        Outline outline = display.getOutline();
        if (outline != null && ConfigDBCClient.EnableOutlines) {
            startBlooming();
            glStencilFunc(GL_GREATER, entity.getEntityId() % 256, 0xFF);  // Test stencil value
            glStencilMask(0xff);
            OutlineRenderer.renderOutlineNPC((ModelMPM) (Object) this, outline, (EntityCustomNpc) entity, display, partialTicks);
            endBlooming();
        }
//        else if (outline == null && ((IEntityMC) entity).getRenderPassTampered())
//            ((IEntityMC) entity).setRenderPass(0);

        ////////////////////////////////////////
        ////////////////////////////////////////
        RenderEventHandler.enableStencilWriting(entity.getEntityId() % 256);
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

    }

    @Inject(method = "renderHead", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", ordinal = 2, shift = At.Shift.BEFORE, remap = true), cancellable = true)
    private void renderDBCHead(EntityCustomNpc entity, float f, CallbackInfo ci) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!isArmor && display.enabled) {
            NPCDBCModel.renderFace(entity, display);
            NPCDBCModel.renderBodySkin(display, bipedHead);
        }
    }

    @Inject(method = "renderBody", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", shift = At.Shift.BEFORE, remap = true), cancellable = true)
    private void renderDBCBody(EntityCustomNpc entity, float f, CallbackInfo ci) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!isArmor && display.enabled) {
            NPCDBCModel.renderBodySkin(display, bipedBody);
        }
    }

    @Inject(method = "renderLegs", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/part/ModelLegs;render(F)V", shift = At.Shift.BEFORE, remap = true), cancellable = true)
    private void renderDBCLegs(EntityCustomNpc entity, float f, CallbackInfo ci) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!isArmor && display.enabled)
            NPCDBCModel.renderBodySkin(display, legs);
    }

    @Inject(method = "renderArms", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", ordinal = 0, shift = At.Shift.BEFORE, remap = true), cancellable = true)
    private void renderDBCLeftArm(EntityCustomNpc entity, float f, boolean bo, CallbackInfo ci) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!isArmor && display.enabled)
            NPCDBCModel.renderBodySkin(display, bipedLeftArm);
    }

    @Inject(method = "renderArms", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", ordinal = 1, shift = At.Shift.BEFORE, remap = true), cancellable = true)
    private void renderDBCRightArm(EntityCustomNpc entity, float f, boolean bo, CallbackInfo ci) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!isArmor && display.enabled)
            NPCDBCModel.renderBodySkin(display, bipedRightArm);
    }

    @Inject(method = "setRotationAngles", at = @At("TAIL"))
    public void rotationAndAngle(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity, CallbackInfo ci) {
        if (!isArmor) {
            NPCDBCModel.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
        }
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
