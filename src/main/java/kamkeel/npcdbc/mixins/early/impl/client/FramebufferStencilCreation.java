package kamkeel.npcdbc.mixins.early.impl.client;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Framebuffer.class)
public abstract class FramebufferStencilCreation {
    @Shadow
    public int framebufferTextureWidth;
    @Shadow
    public int framebufferTextureHeight;
    @Shadow
    public int depthBuffer;

    @Unique
    public int customNPC_DBC_Addon$stencilBuffer = -1;

    @Inject(method = "deleteFramebuffer()V", at = @At("HEAD"))
    public void removeStencilBuffer(CallbackInfo info) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            if (this.customNPC_DBC_Addon$stencilBuffer != -1) {
                OpenGlHelper.func_153184_g(this.customNPC_DBC_Addon$stencilBuffer);
                this.customNPC_DBC_Addon$stencilBuffer = -1;
            }
        }
    }

    @Redirect(method = "createFramebuffer(II)V", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OpenGlHelper;func_153186_a(IIII)V", ordinal = 1))
    public void changeStencilCreationLogic(int p_153186_0_, int p_153186_1_, int p_153186_2_, int p_153186_3_) {
//        throw new RuntimeException("Setup the stencil bits smartly!");

        boolean packedStencilSupport = GLContext.getCapabilities().OpenGL30 || GLContext.getCapabilities().GL_EXT_framebuffer_object;

        if (packedStencilSupport) {
            OpenGlHelper.func_153186_a(OpenGlHelper.field_153199_f, org.lwjgl.opengl.EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, this.framebufferTextureWidth, this.framebufferTextureHeight);
            OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, OpenGlHelper.field_153199_f, this.depthBuffer);
            OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, org.lwjgl.opengl.EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, OpenGlHelper.field_153199_f, this.depthBuffer);
        } else {
            OpenGlHelper.func_153190_b(
                OpenGlHelper.field_153198_e,
                org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                OpenGlHelper.field_153199_f,
                this.depthBuffer
            );

            this.customNPC_DBC_Addon$stencilBuffer = OpenGlHelper.func_153185_f();
            OpenGlHelper.func_153186_a(
                OpenGlHelper.field_153199_f,
                GL30.GL_STENCIL_INDEX8,
                this.framebufferTextureWidth,
                this.framebufferTextureHeight
            );

            OpenGlHelper.func_153190_b(
                OpenGlHelper.field_153198_e,
                org.lwjgl.opengl.EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT,
                OpenGlHelper.field_153199_f,
                this.customNPC_DBC_Addon$stencilBuffer
            );
        }
    }

    @Redirect(method = "createFramebuffer(II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OpenGlHelper;func_153190_b(IIII)V", ordinal = 1))
    public void NO_OP_calls1(int p_153186_0_, int p_153186_1_, int p_153186_2_, int p_153186_3_) {

    }
    @Redirect(method = "createFramebuffer(II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OpenGlHelper;func_153190_b(IIII)V", ordinal = 2))
    public void NO_OP_calls2(int p_153186_0_, int p_153186_1_, int p_153186_2_, int p_153186_3_) {

    }
}
