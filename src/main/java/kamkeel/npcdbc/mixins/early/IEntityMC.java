package kamkeel.npcdbc.mixins.early;

import org.spongepowered.asm.mixin.Unique;

public interface IEntityMC {

    void setRenderPass(int renderPass);

    int getRenderPass();

    @Unique
    boolean getRenderPassTampered();
}
