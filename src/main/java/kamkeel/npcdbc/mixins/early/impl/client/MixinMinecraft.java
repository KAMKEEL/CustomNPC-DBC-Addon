package kamkeel.npcdbc.mixins.early.impl.client;

import kamkeel.npcdbc.client.shader.PostProcessing;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class)
public class MixinMinecraft {

    @Shadow
    public int displayWidth;

    @Shadow
    public int displayHeight;

    @Inject(method = "resize", at = @At("TAIL"))
    private void onResize(int width, int height, CallbackInfo ci) {
        PostProcessing.delete();
        PostProcessing.init(width, height);
    }

    @Inject(method = "toggleFullscreen", at = @At("TAIL"))
    private void updateDisplayMode(CallbackInfo ci) {
        PostProcessing.delete();
        PostProcessing.init(this.displayWidth, this.displayHeight);
    }

    @Inject(method = "refreshResources", at = @At("TAIL"))
    private void onRefresh(CallbackInfo ci) {
        ShaderHelper.areOptifineShadersLoaded();
    }
}
