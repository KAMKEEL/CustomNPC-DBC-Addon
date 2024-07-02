package kamkeel.npcdbc.mixins.early.impl.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11.*;

@Mixin(value = EntityRenderer.class)
public class MixinEntityRenderer {
    @Shadow
    public Minecraft mc;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;disableStandardItemLighting()V",ordinal = 3, shift = At.Shift.BEFORE))
    private void secondRendPassOptifine(float partialTick, long idk, CallbackInfo info, @Local(name = "var14") LocalRef<Frustrum> frustrum) {
        System.out.println("Middle render pass here we go!!!");
        ForgeHooksClient.setRenderPass(ClientProxy.MiddleRenderPass);
        this.mc.renderGlobal.renderEntities(this.mc.renderViewEntity, frustrum.get(), partialTick);
        ForgeHooksClient.setRenderPass(-1);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;disableStandardItemLighting()V",ordinal = 1, shift = At.Shift.BEFORE))
    private void secondRendPass(float partialTick, long idk, CallbackInfo info, @Local(name = "frustrum") LocalRef<Frustrum> frustrum) {
        ForgeHooksClient.setRenderPass(ClientProxy.MiddleRenderPass);
        this.mc.renderGlobal.renderEntities(this.mc.renderViewEntity, frustrum.get(), partialTick);
        ForgeHooksClient.setRenderPass(-1);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;renderFirstPersonHand(Lnet/minecraft/client/renderer/RenderGlobal;FI)Z", shift = At.Shift.BEFORE))
    private void setFPMatrics(float partialTick, long idk, CallbackInfo info, @Local(name = "frustrum") LocalRef<Frustrum> frustrum) {
        glGetFloat(GL_MODELVIEW_MATRIX, RenderEventHandler.FP_MODELVIEW);
        glGetFloat(GL_PROJECTION_MATRIX, RenderEventHandler.FP_PROJECTION);
    }

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemInFirstPerson(F)V", shift = At.Shift.BEFORE), cancellable = true)
    public void renderItemEvent(float p_78476_1_, int p_78476_2_, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new DBCPlayerEvent.RenderArmEvent.Item(mc.thePlayer, null, mc.timer.renderPartialTicks)))
            ci.cancel();
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/OpenGlHelper;shadersSupported:Z", shift = At.Shift.BEFORE))
    private void post(float p_78480_1_, CallbackInfo ci) {
        // PostProcessing.bloom(1f);
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawScreen(IIF)V", shift = At.Shift.BEFORE))
    private void preGUIRender(float p_78480_1_, CallbackInfo ci) {
        ClientProxy.renderingGUI = true;
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawScreen(IIF)V", shift = At.Shift.AFTER))
    private void postGUIRender(float p_78480_1_, CallbackInfo ci) {
        ClientProxy.renderingGUI = false;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V", shift = At.Shift.BEFORE))
    private void preArmRender(float p_78471_1_, long p_78471_2_, CallbackInfo ci) {
        ClientProxy.renderingArm = true;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V", shift = At.Shift.AFTER))
    private void postArmRender(float p_78471_1_, long p_78471_2_, CallbackInfo ci) {
        ClientProxy.renderingArm = false;
    }
}
