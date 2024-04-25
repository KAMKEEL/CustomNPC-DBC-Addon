package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.mixin.IEntityAura;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = EntityAura2.class, remap = false)
public class MixinEntityAura2 implements IEntityAura {

    @Shadow
    private float state;
    @Unique
    private boolean hasLightning;
    @Unique
    private int lightningColor;
    @Unique
    private int lightningAlpha;
    @Unique
    private float getSize = 1;

    @ModifyArgs(method = "<init>(Lnet/minecraft/world/World;Ljava/lang/String;IFFI)V", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/Npcs/EntityAura2;setPositionAndRotation(DDDFF)V"))
    private void fixAuraOffset(Args args, @Local(name = "other") LocalRef<Entity> player) {
        Entity p = player.get();
        p.worldObj.getBlock((int) p.posX, (int) (p.posY - 1), (int) p.posZ);

    }

    @Unique
    @Override
    public float getState() {
        return state;
    }

    @Unique
    @Override
    public void setState(float ok) {
        state = ok;
    }

    @Unique
    @Override
    public boolean isHasLightning() {
        return hasLightning;
    }

    @Unique
    @Override
    public void setHasLightning(boolean hasLightning) {
        this.hasLightning = hasLightning;
        state = hasLightning ? 5 : state;
    }

    @Unique
    @Override
    public int getLightningColor() {
        return lightningColor;
    }

    @Unique
    @Override
    public void setLightningColor(int lightningColor) {
        this.lightningColor = lightningColor;
    }

    @Unique
    @Override
    public int getLightningAlpha() {
        return lightningAlpha;
    }

    @Unique
    @Override
    public void setLightningAlpha(int lightningAlpha) {
        this.lightningAlpha = lightningAlpha;
    }

    @Unique
    @Override
    public float getSize() {
        return getSize;
    }

    @Unique
    @Override
    public void setSize(float getSize) {
        this.getSize = getSize;
    }
}
