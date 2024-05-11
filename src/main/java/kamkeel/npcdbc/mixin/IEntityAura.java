package kamkeel.npcdbc.mixin;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import org.spongepowered.asm.mixin.Unique;

public interface IEntityAura {
    float getState();

    void setState(float ok);

    boolean isHasLightning();

    void setHasLightning(boolean hasLightning);

    int getLightningColor();

    void setLightningColor(int lightningColor);

    @Unique
    int getLightningAlpha();

    @Unique
    void setLightningAlpha(int lightningAlpha);

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
