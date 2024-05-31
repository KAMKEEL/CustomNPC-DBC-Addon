package kamkeel.npcdbc.mixins.late;

import JinRyuu.JRMCore.entity.EntityCusPar;
import JinRyuu.JRMCore.entity.RenderCusPar;
import org.spongepowered.asm.mixin.Unique;

public interface IRenderCusPar {
    void renderParticle(EntityCusPar particle, float partialTicks);
}
