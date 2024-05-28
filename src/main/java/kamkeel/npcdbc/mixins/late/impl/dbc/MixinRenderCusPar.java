package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.entity.EntityCusPar;
import JinRyuu.JRMCore.entity.RenderCusPar;
import kamkeel.npcdbc.config.ConfigDBCClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderCusPar.class, remap = false)
public class MixinRenderCusPar {


    @Inject(method = "renderAura", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal = 0, shift = At.Shift.BEFORE))
    private void disableLightMap(EntityCusPar entity, double parX, double parY, double parZ, float par8, float par9, CallbackInfo ci) {
        if (ConfigDBCClient.RevampAura)
            Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
    }


    @Inject(method = "renderAura", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", ordinal = 1, shift = At.Shift.BEFORE))
    private void enableLightmap(EntityCusPar entity, double parX, double parY, double parZ, float par8, float par9, CallbackInfo ci) {
        if (ConfigDBCClient.RevampAura)
            Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
    }


}
