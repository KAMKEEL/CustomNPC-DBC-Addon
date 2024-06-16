package kamkeel.npcdbc.mixins.early.impl.client;

import kamkeel.npcdbc.client.shader.PostProcessing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "resize", at = @At("TAIL"))
    private void pre(int width, int height, CallbackInfo ci) {
        PostProcessing.delete();
        PostProcessing.init(width, height);
    }
}
