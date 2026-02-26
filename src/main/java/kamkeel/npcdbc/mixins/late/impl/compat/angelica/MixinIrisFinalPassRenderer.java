package kamkeel.npcdbc.mixins.late.impl.compat.angelica;

import kamkeel.npcdbc.client.shader.PostProcessing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

@Pseudo
@Mixin(targets = "net.coderbot.iris.postprocess.FinalPassRenderer", remap = false)
public class MixinIrisFinalPassRenderer {

    private static int debugPrintsRemaining = Integer.getInteger("npcdbc.debug.irisfinal.max", 200);

    @Inject(method = "renderFinalPass()V", at = @At("HEAD"), remap = false)
    private void npcdbc$irisFinalPassHead(CallbackInfo ci) {
        if (debugPrintsRemaining <= 0) {
            return;
        }
        debugPrintsRemaining--;
        dump("IrisFinalPass.HEAD");
    }

    @Inject(method = "renderFinalPass()V", at = @At("TAIL"), remap = false)
    private void npcdbc$afterIrisFinalPass(CallbackInfo ci) {
        if (debugPrintsRemaining > 0) {
            debugPrintsRemaining--;
            dump("IrisFinalPass.TAIL.beforeNpcdbc");
        }

        if (!PostProcessing.processBloom) {
            return;
        }

        if (!isShaderPackInUse()) {
            return;
        }

        PostProcessing.postProcess();

        if (debugPrintsRemaining > 0) {
            debugPrintsRemaining--;
            dump("IrisFinalPass.TAIL.afterNpcdbc");
        }
    }

    private static void dump(String tag) {
        int bound = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int program = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        IntBuffer vp = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(GL11.GL_VIEWPORT, vp);

        int draw0 = GL11.glGetInteger(GL20.GL_DRAW_BUFFER0);
        int draw1 = GL11.glGetInteger(GL20.GL_DRAW_BUFFER1);
        int draw2 = GL11.glGetInteger(GL20.GL_DRAW_BUFFER2);
        int draw3 = GL11.glGetInteger(GL20.GL_DRAW_BUFFER3);

        System.out.println("[npcdbc.irisfinal] " + tag
            + " bound=" + bound
            + " prog=" + program
            + " vp=" + vp.get(2) + "x" + vp.get(3)
            + " drawBufs=[" + draw0 + "," + draw1 + "," + draw2 + "," + draw3 + "]");
    }

    private static boolean isShaderPackInUse() {
        try {
            Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object instance = api.getMethod("getInstance").invoke(null);
            Object result = api.getMethod("isShaderPackInUse").invoke(instance);
            return Boolean.TRUE.equals(result);
        } catch (Throwable ignored) {
            return true;
        }
    }
}
