package kamkeel.npcdbc.mixin.impl;

import JinRyuu.JBRA.RenderPlayerJBRA;
import kamkeel.npcdbc.skills.Transform;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = RenderPlayerJBRA.class, remap = false)
public abstract class MixinRenderPlayerJBRA extends RenderPlayer {
    // @ModifyVariable(method = "renderEquippedItemsJBRA", at = @At(value = "STORE"), ordinal = 1, remap = false)

    @ModifyVariable(method = "renderEquippedItemsJBRA", at = @At(value = "STORE"), name = "rg", remap = false)
    public int modifyRage(int original) {
        System.out.println("hiiii " + original);
        original = Transform.rage > 0 ? (int) Transform.rage : original;

        return original;
    }
}
