package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.entity.EntityCusPar;
import JinRyuu.JRMCore.entity.RenderCusPar;
import kamkeel.npcdbc.client.ClientConstants;
import kamkeel.npcdbc.mixins.late.IEntityCusPar;
import kamkeel.npcdbc.mixins.late.IRenderCusPar;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderCusPar.class)
public class MixinRenderCusPar implements IRenderCusPar {

    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    private void disableRendering(Entity par1Entity, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci) {
        IEntityCusPar particle = (IEntityCusPar) par1Entity;
        if (particle.isEnhancedRendering())
            ci.cancel();
    }

    @Unique
    public void renderParticle(EntityCusPar particle, float partialTicks) {
        double interPosX = (particle.lastTickPosX + (particle.posX - particle.lastTickPosX) * (double) partialTicks) - (ClientConstants.renderingGUI ? -0 : RenderManager.renderPosX);
        double interPosY = (particle.lastTickPosY + (particle.posY - particle.lastTickPosY) * (double) partialTicks) - (ClientConstants.renderingGUI ? -0.2f : RenderManager.renderPosY);
        double interPosZ = (particle.lastTickPosZ + (particle.posZ - particle.lastTickPosZ) * (double) partialTicks) - (ClientConstants.renderingGUI ? -0 : RenderManager.renderPosZ);
        float interYaw = particle.getEnt().prevRotationYaw + (particle.getEnt().rotationYaw - particle.getEnt().prevRotationYaw) * partialTicks;


        ((RenderCusPar) (Object) this).renderAura(particle, interPosX, interPosY, interPosZ, interYaw, partialTicks);

    }



}
