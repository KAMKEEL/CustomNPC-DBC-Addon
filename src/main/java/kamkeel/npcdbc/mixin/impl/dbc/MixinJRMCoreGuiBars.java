package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreGuiBars;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = JRMCoreGuiBars.class, remap = false)
public abstract class MixinJRMCoreGuiBars extends Gui {
    @Inject(method = "showSE", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", shift = At.Shift.BEFORE))
    private void renderStatusEffectIcon(int var51, int var61, int var71, int var81, CallbackInfo ci, @Local(name = "i") LocalIntRef i, @Local(name = "j") LocalIntRef j) {



    }

}
