package kamkeel.npcdbc.mixins.late.impl.npc.client;

import kamkeel.npcdbc.mixins.late.IScaleRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import noppes.npcs.client.model.util.ModelScaleRenderer;
import noppes.npcs.entity.data.ModelScalePart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelScaleRenderer.class)
public abstract class MixinModelScaleRenderer extends ModelRenderer implements IScaleRenderer {
    @Shadow
    protected ModelScalePart config;

    public MixinModelScaleRenderer(ModelBase p_i1172_1_, String p_i1172_2_) {
        super(p_i1172_1_, p_i1172_2_);
    }

    @Override
    public ModelScalePart config() {
        return config;
    }

}
