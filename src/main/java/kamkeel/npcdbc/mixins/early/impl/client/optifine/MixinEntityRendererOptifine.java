package kamkeel.npcdbc.mixins.early.impl.client.optifine;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.shader.PostProcessing;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11.*;

@Mixin(EntityRenderer.class)
public class MixinEntityRendererOptifine {

    @Shadow
    public Minecraft mc;

    @Inject(method = "renderWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;debugViewDirection:I", ordinal = 0, shift = At.Shift.BEFORE))
    private void captureDefaultMatrices(float partialTick, long idk, CallbackInfo info) {
        glGetFloat(GL_MODELVIEW_MATRIX, PostProcessing.DEFAULT_MODELVIEW);
        glGetFloat(GL_PROJECTION_MATRIX, PostProcessing.DEFAULT_PROJECTION);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;enableStandardItemLighting()V",ordinal = 1, shift = At.Shift.AFTER))
    private void secondRendPassOptifine(float partialTick, long idk, CallbackInfo info, @Local(name = "var14") LocalRef<Frustrum> frustrum) {
        mc.mcProfiler.endStartSection("NPCDBCEntities");
        ForgeHooksClient.setRenderPass(ClientProxy.MiddleRenderPass);
        this.mc.renderGlobal.renderEntities(this.mc.renderViewEntity, frustrum.get(), partialTick);
        ForgeHooksClient.setRenderPass(-1);
    }

    @Inject(method = "renderHand(FIZZZ)V", remap = false, at = @At(value="INVOKE", target="Lnet/minecraft/client/renderer/EntityRenderer;enableLightmap(D)V", remap = true), cancellable = true)
    public void renderItemEventOptifine(float par1, int par2, boolean renderItem, boolean renderOverlay, boolean renderTranslucent, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new DBCPlayerEvent.RenderArmEvent.Item(mc.thePlayer, null, mc.timer.renderPartialTicks))) {
            ci.cancel();
            glPopMatrix();
        }
    }

}
