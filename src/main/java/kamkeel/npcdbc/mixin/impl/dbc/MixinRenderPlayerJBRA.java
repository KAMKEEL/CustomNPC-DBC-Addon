package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JBRA.RenderPlayerJBRA;
import kamkeel.npcdbc.skills.Transform;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = RenderPlayerJBRA.class, remap = false)
public abstract class MixinRenderPlayerJBRA extends RenderPlayer {


}
