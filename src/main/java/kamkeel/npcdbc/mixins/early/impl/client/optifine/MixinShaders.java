package kamkeel.npcdbc.mixins.early.impl.client.optifine;


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

    @Inject(method = "useProgram(I)V", at = @At(value = "FIELD", target = "Lshadersmod/client/Shaders;activeProgram:I", ordinal = 1, shift = At.Shift.AFTER))
    private static void activeOptifineShader(int program, CallbackInfo ci) {
        ShaderHelper.currentOptifineProgram = activeProgram;
      //  System.out.println("active" + activeProgram);
    }

}
