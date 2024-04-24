package kamkeel.npcdbc.mixin;

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
}
