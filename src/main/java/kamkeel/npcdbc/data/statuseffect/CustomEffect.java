package kamkeel.npcdbc.data.statuseffect;

import kamkeel.npcdbc.api.effect.ICustomEffect;
import net.minecraft.entity.player.EntityPlayer;

public class CustomEffect extends StatusEffect implements ICustomEffect {
    public CustomEffect(int id) {
        this.isCustom = true;
        this.id = id;
    }

    public CustomEffect() {
        isCustom = true;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int getEveryXTick() {
        return everyXTick;
    }

    @Override
    public void setEveryXTick(int everyXTick) {
        this.everyXTick = everyXTick;
    }

    @Override
    public int getIconX() {
        return iconX;
    }

    @Override
    public void setIconX(int iconX) {
        this.iconX = iconX;
    }

    @Override
    public int getIconY() {
        return iconY;
    }

    @Override
    public void setIconY(int iconY) {
        this.iconY = iconY;
    }

    @Override
    public boolean isLossOnDeath() {
        return lossOnDeath;
    }

    @Override
    public void setLossOnDeath(boolean lossOnDeath) {
        this.lossOnDeath = lossOnDeath;
    }

    public void onAdded(EntityPlayer player, PlayerEffect playerEffect) {

    }

    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
    }

    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect) {
    }
}
