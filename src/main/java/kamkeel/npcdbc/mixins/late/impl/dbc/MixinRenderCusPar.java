package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.entity.EntityCusPar;
import JinRyuu.JRMCore.entity.RenderCusPar;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.mixins.late.IEntityCusPar;
import kamkeel.npcdbc.mixins.late.IRenderCusPar;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderCusPar.class, remap = false)
public class MixinRenderCusPar implements IRenderCusPar {

    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true, remap = true)
    private void disableRendering(Entity par1Entity, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci) {
        IEntityCusPar particle = (IEntityCusPar) par1Entity;
        if (particle.isEnhancedRendering())
            ci.cancel();
    }

    @Redirect(method = "fieldPass2", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V", ordinal = 0))
    private void skipGlDepthMask(boolean flag) {
        if (!RenderEventHandler.hi)
            GL11.glDepthMask(false);
        GL11.glAlphaFunc(GL11.GL_GREATER,0.05f);
    }

    @Redirect(method = "fieldPass3", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V", ordinal = 0))
    private void skipGlDepthMask2(boolean flag) {
        if (!RenderEventHandler.hi)
            GL11.glDepthMask(false);
        GL11.glAlphaFunc(GL11.GL_GREATER,0.05f);
    }

    @Unique
    public void renderParticle(EntityCusPar particle, float partialTicks) {
        double interPosX = (particle.lastTickPosX + (particle.posX - particle.lastTickPosX) * (double) partialTicks) - (ClientProxy.renderingGUI ? -0 : RenderManager.renderPosX);
        double interPosY = (particle.lastTickPosY + (particle.posY - particle.lastTickPosY) * (double) partialTicks) - (ClientProxy.renderingGUI ? -0.2f : RenderManager.renderPosY);
        double interPosZ = (particle.lastTickPosZ + (particle.posZ - particle.lastTickPosZ) * (double) partialTicks) - (ClientProxy.renderingGUI ? -0 : RenderManager.renderPosZ);
        float interYaw = particle.getEnt().prevRotationYaw + (particle.getEnt().rotationYaw - particle.getEnt().prevRotationYaw) * partialTicks;


        ((RenderCusPar) (Object) this).renderAura(particle, interPosX, interPosY, interPosZ, interYaw, partialTicks);

    }


}
