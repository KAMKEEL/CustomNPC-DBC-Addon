package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.mixin.IEntityAura;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Shadow
    private boolean rot;
    @Shadow
    private float yaw;
    @Shadow
    private float pitch;
    @Shadow
    private String mot;

    @Inject(method = "<init>(Lnet/minecraft/world/World;Ljava/lang/String;IFFI)V", at = @At(value = "RETURN"))
    private void fixAuraOffset(World par1World, String dbcCharger, int c, float s, float s2, int cr, CallbackInfo ci, @Local(name = "other") LocalRef<Entity> player) {
        if (player.get() != null)
            return;
        EntityAura2 aura = (EntityAura2) (Object) this;
        Entity other = Utility.getEntityFromID(par1World, mot);
        if (other != null) {
            if (this.rot) {
                this.yaw = other.rotationYaw;
                this.pitch = other.rotationPitch;
            }

            aura.setPositionAndRotation(other.posX, other.posY + (double) (other instanceof EntityPlayerSP ? -1.6F : 0.0F), other.posZ, other.rotationYaw, other.rotationPitch);
        }

    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getPlayerEntityByName(Ljava/lang/String;)Lnet/minecraft/entity/player/EntityPlayer;", shift = At.Shift.AFTER))
    private void redirect(CallbackInfo ci, @Local(name = "other") LocalRef<Entity> player) {


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
