package kamkeel.npcdbc.mixins.early.impl.client;

import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeHooksClient.class)
public abstract class MixinForgeHooksClient {

    @Shadow
    static int stencilBits;

    /**
     * @author somehussar
     * @reason Main window buffer on certain devices DOES NOT accelerate a 24depth 8stencil buffer.
     */
    @Inject(method = "createDisplay()V", at = @At("RETURN"), remap = false)
    private static void injectStencilBits(CallbackInfo info) {
        stencilBits = 8;
    }
}
