package kamkeel.npcdbc.mixins.early.impl.client;


import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "DynamicLights", remap = false)
public class MixinDynamicLights {

    @Inject(method = "getLightLevel(Lnet/minecraft/entity/Entity;)I", at = @At("HEAD"), cancellable = true)
    private static void getLightLevel(Entity entity, CallbackInfoReturnable<Integer> cir) {
        if (entity instanceof EntityAura) {
            cir.setReturnValue(20);
        }
    }


    @Inject(method = "getLightLevel(Lnet/minecraft/item/ItemStack;)I", at = @At("TAIL"), cancellable = true)
    private static void getLightLevel(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
    }
}
