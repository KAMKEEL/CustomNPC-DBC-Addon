package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JBRA.RenderPlayerJBRA;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = RenderPlayerJBRA.class, remap = false)
public abstract class MixinRenderPlayerJBRA extends RenderPlayer {


}
