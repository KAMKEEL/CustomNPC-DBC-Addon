package kamkeel.npcdbc.mixin.impl.npc.client;

import kamkeel.npcdbc.client.model.ModelNPCDBC;
import net.minecraft.entity.Entity;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.client.model.ModelNPCMale;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModelMPM.class, remap = false)
public class MixinModelMPM extends ModelNPCMale {

    @Shadow
    public boolean isArmor;
    @Unique public ModelNPCDBC customNPC_DBCAddon$modelNPCDBC;

    public MixinModelMPM(float f) {
        super(f);
    }

    public MixinModelMPM(float f, boolean alex) {
        super(f, alex);
    }

    @Inject(method = "render", at = @At(value = "HEAD"), remap = true)
    private void rotationKeep(Entity par1Entity, float p1, float p2, float p3, float p4, float p5, float p6, CallbackInfo ci){
        if(customNPC_DBCAddon$modelNPCDBC == null)
            customNPC_DBCAddon$modelNPCDBC = new ModelNPCDBC((ModelMPM) (Object)this);

        customNPC_DBCAddon$modelNPCDBC.rot1 = p1;
        customNPC_DBCAddon$modelNPCDBC.rot2 = p2;
        customNPC_DBCAddon$modelNPCDBC.rot3 = p3;
        customNPC_DBCAddon$modelNPCDBC.rot4 = p4;
        customNPC_DBCAddon$modelNPCDBC.rot5 = p5;
        customNPC_DBCAddon$modelNPCDBC.rot6 = p6;
    }

    @Inject(method = "renderHead", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", shift = At.Shift.BEFORE))
    private void renderDBCHead(EntityCustomNpc entity, float f, CallbackInfo ci){
        if(customNPC_DBCAddon$modelNPCDBC == null && !isArmor)
            customNPC_DBCAddon$modelNPCDBC = new ModelNPCDBC((ModelMPM) (Object)this);

        if(!isArmor){
            customNPC_DBCAddon$modelNPCDBC.renderHead(entity);
        }
    }
}
