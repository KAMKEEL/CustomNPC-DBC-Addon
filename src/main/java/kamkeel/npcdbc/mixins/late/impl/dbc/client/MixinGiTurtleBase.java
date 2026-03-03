package kamkeel.npcdbc.mixins.late.impl.dbc.client;

import JinRyuu.JRMCore.items.GiTurtleBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GiTurtleBase.class, remap = false)
public abstract class MixinGiTurtleBase extends ItemArmor {
    public MixinGiTurtleBase(ArmorMaterial p_i45325_1_, int p_i45325_2_, int p_i45325_3_) {
        super(p_i45325_1_, p_i45325_2_, p_i45325_3_);
    }

    @Inject(method = "wear", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectNpcRenderDBCArmor(EntityLivingBase e, CallbackInfoReturnable<Boolean> cir) {
        if (e instanceof EntityCustomNpc)
            cir.setReturnValue(true);
    }
}
