package kamkeel.npcdbc.mixins.early.impl.client;


import kamkeel.npcdbc.mixins.early.IEntityMC;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Entity.class, remap = true)
public class MixinEntity implements IEntityMC {
    @Unique
    private int renderPass = 0;

    @Unique
    public void setRenderPass(int renderPass) {
        this.renderPass = renderPass;
    }

    /**
     * @return
     * @author
     * @reason
     */
//    @Overwrite
//    public boolean shouldRenderInPass(int pass) {
//        //   System.out.println();
//        return pass == renderPass;
//    }

//    @Inject(method = "shouldRenderInPass", at = @At("HEAD"))
//    private void shouldRenderInPass(int pass, CallbackInfoReturnable<Boolean> cir) {
//        if(renderPass != 0)
//        cir.setReturnValue(pass == renderPass);
//    }
}
