package kamkeel.npcdbc.mixins.early.impl.client.optifine;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.client.ClientConstants;
import kamkeel.npcdbc.client.OptifineHelper;
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

    @Inject(method = "renderWorld", at = @At(value = "HEAD"))
    private void startRenderingWorld(float p_78471_1_, long p_78471_2_, CallbackInfo ci){
        ClientConstants.renderingWorld = true;
    }

    @Inject(method = "renderWorld", at = @At(value = "RETURN"))
    private void endRenderingWorld(float p_78471_1_, long p_78471_2_, CallbackInfo ci){
        ClientConstants.renderingWorld = false;
    }

    @Inject(method = "renderWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;debugViewDirection:I", ordinal = 0, shift = At.Shift.BEFORE))
    private void captureDefaultMatricesOptifine(float partialTick, long idk, CallbackInfo info) {
        glGetFloat(GL_MODELVIEW_MATRIX, PostProcessing.DEFAULT_MODELVIEW);
        glGetFloat(GL_PROJECTION_MATRIX, PostProcessing.DEFAULT_PROJECTION);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;enableStandardItemLighting()V", ordinal = 1, shift = At.Shift.AFTER))
    private void secondRendPassOptifine(float partialTick, long idk, CallbackInfo info, @Local(name = "var14") LocalRef<Frustrum> frustrum) {
        mc.mcProfiler.endStartSection("NPCDBCEntities");
        ForgeHooksClient.setRenderPass(ClientConstants.MiddleRenderPass);
        this.mc.renderGlobal.renderEntities(this.mc.renderViewEntity, frustrum.get(), partialTick);
        ForgeHooksClient.setRenderPass(-1);
    }

    @Inject(method = "renderHand(FIZZZ)V", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;enableLightmap(D)V", remap = true), cancellable = true)
    public void renderItemEventOptifine(float par1, int par2, boolean renderItem, boolean renderOverlay, boolean renderTranslucent, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new DBCPlayerEvent.RenderArmEvent.Item(mc.thePlayer, null, mc.timer.renderPartialTicks))) {
            ci.cancel();
            glPopMatrix();
        }
        PostProcessing.bloom(1.5f, true);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lshadersmod/client/Shaders;endRender()V", shift = At.Shift.AFTER))
    private void processEntities(float partialTick, long idk, CallbackInfo info) {
        OptifineHelper.process();
    }


    @Inject(method = "updateCameraAndRender", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/OpenGlHelper;shadersSupported:Z", shift = At.Shift.BEFORE))
    private void post(float p_78480_1_, CallbackInfo ci) {
        PostProcessing.postProcess();
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawScreen(IIF)V", shift = At.Shift.BEFORE))
    private void preGUIRenderOptifine(float p_78480_1_, CallbackInfo ci) {
        ClientConstants.renderingGUI = true;
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawScreen(IIF)V", shift = At.Shift.AFTER))
    private void postGUIRenderOptifine(float p_78480_1_, CallbackInfo ci) {
        ClientConstants.renderingGUI = false;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V", shift = At.Shift.BEFORE))
    private void preArmRenderOptifine(float p_78471_1_, long p_78471_2_, CallbackInfo ci) {
        ClientConstants.renderingArm = true;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V", shift = At.Shift.AFTER))
    private void postArmRenderOptifine(float p_78471_1_, long p_78471_2_, CallbackInfo ci) {
        ClientConstants.renderingArm = false;
    }
}
