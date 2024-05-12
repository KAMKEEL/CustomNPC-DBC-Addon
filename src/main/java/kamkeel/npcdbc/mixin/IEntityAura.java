package kamkeel.npcdbc.mixin;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import org.spongepowered.asm.mixin.Unique;

public interface IEntityAura {
    float getState();

    void setState(float ok);

    boolean hasLightning();

    void setHasLightning(boolean hasLightning);

    int getLightningColor();

    void setLightningColor(int lightningColor);

    @Unique
    int getLightningAlpha();

    @Unique
    void setLightningAlpha(int lightningAlpha);

    @Unique
    int getLightningSpeed();

    @Unique
    int getLightningIntensity();

    @Unique
    void setLightningSpeed(int lightningSpeed);

    @Unique
    void setLightningIntensity(int lightningIntensity);

    float getSize();

    void setSize(float getSize);

    @Unique
    EntityAura2 getParent();

    @Unique
    void setParent(EntityAura2 parent);

    @Unique
    boolean hasParent();

    @Unique
    void setIsKaioken(boolean is);

    @Unique
    boolean isKaioken();


}
