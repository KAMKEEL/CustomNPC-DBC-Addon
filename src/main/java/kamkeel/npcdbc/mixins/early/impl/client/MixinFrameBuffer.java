package kamkeel.npcdbc.mixins.early.impl.client;

import kamkeel.npcdbc.client.shader.PostProcessing;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

@Mixin(value = Framebuffer.class)
public class MixinFrameBuffer {
    @Unique
    public int activeTexture;

    @Inject(method = "framebufferRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/shader/Framebuffer;bindFramebufferTexture()V", shift = At.Shift.AFTER), cancellable = true)
    private void pre(int p_147615_1_, int p_147615_2_, CallbackInfo ci) {
        Framebuffer fb = (Framebuffer) (Object) this;
        PostProcessing.Event.Pre event = new PostProcessing.Event.Pre((Framebuffer) (Object) this, fb.framebufferTexture);
        if (MinecraftForge.EVENT_BUS.post(event))
            ci.cancel();

        glBindTexture(GL_TEXTURE_2D, activeTexture = event.textureID);

    }

    @Inject(method = "framebufferRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/shader/Framebuffer;unbindFramebufferTexture()V", shift = At.Shift.AFTER))
    private void post(int p_147615_1_, int p_147615_2_, CallbackInfo ci) {
        PostProcessing.Event.Post event = new PostProcessing.Event.Post((Framebuffer) (Object) this, activeTexture);
        MinecraftForge.EVENT_BUS.post(event);

    }
}
