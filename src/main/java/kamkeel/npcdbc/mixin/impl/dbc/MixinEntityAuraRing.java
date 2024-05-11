package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.Npcs.EntityAuraRing;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityAuraRing.class, remap = false)
public class MixinEntityAuraRing {
    @Shadow
    private String mot;

    @Inject(method = "onUpdate", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getPlayerEntityByName(Ljava/lang/String;)Lnet/minecraft/entity/player/EntityPlayer;", shift = At.Shift.AFTER, remap = true))
    private void redirect(CallbackInfo ci, @Local(name = "other") LocalRef<Entity> player) {

        if (player.get() != null)
            return;
        EntityAuraRing aura = (EntityAuraRing) (Object) this;
        Entity entity = Utility.getEntityFromID(aura.worldObj, mot);
        if (entity != null)
            player.set(entity);
    }
}
