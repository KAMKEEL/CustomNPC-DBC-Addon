package kamkeel.npcdbc.mixins.early.impl.client;

import net.minecraft.client.shader.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Framebuffer.class)
public abstract class FramebufferStencilCreation {

    @Redirect(method = "createFramebuffer(II)V", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OpenGlHelper;func_153186_a(IIII)V", ordinal = 1))
    public void changeStencilCreationLogic(int p_153186_0_, int p_153186_1_, int p_153186_2_, int p_153186_3_) {
        throw new RuntimeException("Setup the stencil bits smartly!");
    }

    @Redirect(method = "createFramebuffer(II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OpenGlHelper;func_153190_b(IIII)V", ordinal = 1))
    public void NO_OP_calls1(int p_153186_0_, int p_153186_1_, int p_153186_2_, int p_153186_3_) {

    }
    @Redirect(method = "createFramebuffer(II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OpenGlHelper;func_153190_b(IIII)V", ordinal = 2))
    public void NO_OP_calls2(int p_153186_0_, int p_153186_1_, int p_153186_2_, int p_153186_3_) {

    }
}
