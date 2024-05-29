package kamkeel.npcdbc.mixins.early.impl.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderer.class)
public class MixinEntityRenderer {
    @Shadow
    public int debugViewDirection;
    @Shadow
    public Minecraft mc;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;dispatchRenderLast(Lnet/minecraft/client/renderer/RenderGlobal;F)V", shift = At.Shift.BEFORE))
    private void secondRendPass(float partialTick, long idk, CallbackInfo info, @Local(name = "frustrum") LocalRef<Frustrum> frustrum) {

        if (this.debugViewDirection == 0) //Only render if render pass 0 happens as well.
        {
            RenderHelper.enableStandardItemLighting();
            this.mc.mcProfiler.endStartSection("entities");
            ForgeHooksClient.setRenderPass(ClientProxy.SecondRenderPass);
            this.mc.renderGlobal.renderEntities(this.mc.renderViewEntity, frustrum.get(), partialTick);
            ForgeHooksClient.setRenderPass(-1);
            RenderHelper.disableStandardItemLighting();
        }
    }
}
