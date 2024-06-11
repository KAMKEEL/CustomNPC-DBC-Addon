package kamkeel.npcdbc.mixins.early.impl.client;

import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.shader.PostProcessing;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

import static kamkeel.npcdbc.client.shader.PostProcessing.COLOR_BUFFER_2;
import static org.lwjgl.opengl.GL11.*;

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

    @Inject(method = "createFramebuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/shader/Framebuffer;framebufferClear()V", ordinal = 1, shift = At.Shift.BEFORE))
    private void onCreation(int p_147615_1_, int p_147615_2_, CallbackInfo ci) {
        Framebuffer fb = (Framebuffer) (Object) this;

        COLOR_BUFFER_2 = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, COLOR_BUFFER_2);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, fb.framebufferTextureWidth, fb.framebufferTextureHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        OpenGlHelper.func_153188_a(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, COLOR_BUFFER_2, 0);

        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete: " + status);
        }

        ClientProxy.rendering = ClientProxy.defaultRendering = fb.framebufferTexture;
    }
}
