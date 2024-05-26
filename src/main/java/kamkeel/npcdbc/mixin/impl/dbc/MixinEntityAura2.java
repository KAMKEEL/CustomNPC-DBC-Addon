package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.client.sound.Sound;
import kamkeel.npcdbc.mixin.IEntityAura;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
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
    private int lightningSpeed;
    @Unique
    private int lightningIntensity;
    @Unique
    private float getSize = 1;

    @Unique
    private EntityAura2 parent;
    @Unique
    private boolean isKaiokenAura;

    @Shadow
    private String mot;


    @Inject(method = "onUpdate", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getPlayerEntityByName(Ljava/lang/String;)Lnet/minecraft/entity/player/EntityPlayer;", shift = At.Shift.AFTER), remap = true)
    private void redirect(CallbackInfo ci, @Local(name = "other") LocalRef<Entity> player) {
        EntityAura2 aura = (EntityAura2) (Object) this;

        if (player.get() != null) {
            if (aura.getAge() < aura.getLightLivingTime() && hasLightning && aura.getAge() == 2)
                new Sound("jinryuudragonbc:1610.spark", player.get()).setVolume(0.0375F).setPitch(0.85F + aura.getLightLivingTime() * 0.05F).play(false);

            return;
        }
        Entity entity = Utility.getEntityFromID(aura.worldObj, mot);
        if (entity != null) {
            player.set(entity);


        }
        
        
        
        
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
    public boolean hasLightning() {
        return hasLightning;
    }

    @Unique
    @Override
    public void setHasLightning(boolean hasLightning) {
        this.hasLightning = hasLightning;
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
    public int getLightningSpeed() {
        return lightningSpeed;
    }

    @Unique
    @Override
    public void setLightningSpeed(int lightningSpeed) {
        this.lightningSpeed = lightningSpeed;
    }

    @Unique
    @Override
    public int getLightningIntensity() {
        return lightningIntensity;
    }

    @Unique
    @Override
    public void setLightningIntensity(int lightningIntensity) {
        this.lightningIntensity = lightningIntensity;
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

    @Unique
    @Override
    public EntityAura2 getParent() {
        return this.parent;
    }


    @Unique
    @Override
    public void setParent(EntityAura2 parent) {
        this.parent = parent;
    }

    @Unique
    @Override
    public boolean hasParent() {
        return this.parent != null;
    }

    @Unique
    @Override
    public void setIsKaioken(boolean is) {
        this.isKaiokenAura = is;
    }


    @Unique
    @Override
    public boolean isKaioken() {
        return isKaiokenAura;
    }


}
