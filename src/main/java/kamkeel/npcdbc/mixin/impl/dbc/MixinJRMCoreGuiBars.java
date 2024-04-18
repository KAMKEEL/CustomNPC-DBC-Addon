package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreGuiBars;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.skills.Transform;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * For transformation rage bar
 */
@Mixin(value = JRMCoreGuiBars.class, remap = false)
public abstract class MixinJRMCoreGuiBars extends Gui {
    @Unique
    private float maxPerc;

    @ModifyVariable(method = "renderRageBar", at = @At(value = "STORE"), ordinal = 0, remap = false)
    public float modifyMaxPerc(float original) {
        maxPerc = original;
        return original;
    }


    @ModifyVariable(method = "renderRageBar", at = @At(value = "STORE"), ordinal = 1, remap = false)
    public float modifyVar22(float original) {
        return maxPerc * (Transform.rage > 0 ? Transform.rage : JRMCoreH.TransSaiCurRg);
    }

}
