package kamkeel.npcdbc.mixins.late.impl.npc.client;

import kamkeel.npcdbc.client.model.ModelDBC;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
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

@Mixin(value = ModelMPM.class, remap = false)
public abstract class MixinModelMPM extends ModelNPCMale {

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
        if(!isArmor)
            this.NPCDBCModel.setHurt(entity);
        if(isArmor)
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
        if (!isArmor && display.enabled){
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

}
