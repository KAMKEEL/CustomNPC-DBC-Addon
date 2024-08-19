package kamkeel.npcdbc.mixins.early.impl.client.optifine;


import kamkeel.npcdbc.client.OptifineHelper;
import kamkeel.npcdbc.client.shader.PostProcessing;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "shadersmod.client.Shaders", remap = false)
public class MixinShaders {

    @Shadow
    public static int activeProgram;

    @Shadow
    public static int renderWidth = 0;
    @Shadow
    public static int renderHeight = 0;

    @Shadow
    public static boolean shaderPackLoaded;


    @Inject(method = "loadShaderPack()V", at = @At("TAIL"))
    private static void loadPack(CallbackInfo ci) {
        OptifineHelper.shaderPackLoaded = shaderPackLoaded;

    }


    @Inject(method = "setupFrameBuffer()V", at = @At("TAIL"))
    private static void init(CallbackInfo ci) {
        System.out.println("hiey");
        PostProcessing.init(renderWidth, renderHeight);
        PostProcessing.VIEWPORT_WIDTH = renderWidth;
        PostProcessing.VIEWPORT_HEIGHT = renderHeight;


    }

    @Inject(method = "useProgram(I)V", at = @At(value = "FIELD", target = "Lshadersmod/client/Shaders;activeProgram:I", ordinal = 1, shift = At.Shift.AFTER))
    private static void activeOptifineShader(int program, CallbackInfo ci) {
        ShaderHelper.currentOptifineProgram = activeProgram;
    }

    @Shadow
    private static int checkGLError(String location) {
        return 0;
    }

}
