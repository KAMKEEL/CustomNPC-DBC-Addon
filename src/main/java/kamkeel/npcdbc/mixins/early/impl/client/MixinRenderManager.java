package kamkeel.npcdbc.mixins.early.impl.client;

import kamkeel.npcdbc.client.CNPCAnimationHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderManager.class)
public class MixinRenderManager {

    @Inject(method = "renderEntityStatic", at = @At("HEAD"))
    public void renderEntityStatic(Entity p_147936_1_, float p_147936_2_, boolean p_147936_3_, CallbackInfoReturnable<Boolean> cir) {
        Render render = RenderManager.instance.getEntityRenderObject(p_147936_1_);
        if (render instanceof RendererLivingEntity) {
            ModelBase mainModel = ((RendererLivingEntity) render).mainModel;
            CNPCAnimationHelper.setOriginalValues(mainModel);
        }
    }
}
