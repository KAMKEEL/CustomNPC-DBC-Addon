package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import JinRyuu.DragonBC.common.Npcs.RenderAura2;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.data.Aura;
import net.minecraft.client.renderer.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = RenderAura2.class, remap = false)
public class MixinRenderAura2 {

    @Inject(method = "lightning", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_F(FFFF)V", ordinal = -1, shift = At.Shift.AFTER))
    private void renderLightning(EntityAura2 e, double par2, double par4, double par6, float par9, float var20, float var13, boolean rot, CallbackInfo ci, @Local(name = "tessellator2") LocalRef<Tessellator> tessellator) {
        Aura aura = null;//add here
        if (aura != null) {
            if (aura.hasLightning() && aura.lightningColor != 0) {
                Color col = Color.decode(aura.lightningColor + "");
                tessellator.get().setColorRGBA(col.getRed(), col.getGreen(), col.getBlue(), aura.lightningAlpha);
            }
        }
    }
}
