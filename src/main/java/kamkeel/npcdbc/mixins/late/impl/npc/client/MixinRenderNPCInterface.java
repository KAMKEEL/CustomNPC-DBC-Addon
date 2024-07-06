package kamkeel.npcdbc.mixins.late.impl.npc.client;

import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderNPCInterface.class,remap = false)
public class MixinRenderNPCInterface {

    @Inject(method = "renderLivingLabel", at = @At("HEAD"))
    private void fixNameTagStenciledOut(EntityNPCInterface npc, double d, double d1, double d2, int i, Object[] obs, CallbackInfo ci) {
        GL11.glStencilMask(0x0);
    }

    @Inject(method = "renderLivingLabel", at = @At("TAIL"))
    private void enableStencil(EntityNPCInterface npc, double d, double d1, double d2, int i, Object[] obs, CallbackInfo ci) {
        GL11.glStencilMask(0xff);
    }


}
