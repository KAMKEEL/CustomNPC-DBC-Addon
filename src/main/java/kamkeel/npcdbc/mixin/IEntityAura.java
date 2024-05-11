package kamkeel.npcdbc.mixin;

import kamkeel.npcdbc.data.aura.Aura;
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
    Aura getParent();

    @Unique
    void setParent(Aura parent);

    @Unique
    boolean hasParent();

    @Unique
    void setIsKaioken(boolean is);

    @Unique
    boolean isKaioken();
}
