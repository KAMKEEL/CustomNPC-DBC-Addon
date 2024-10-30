package kamkeel.npcdbc.data.statuseffect;

import kamkeel.npcdbc.api.effect.ICustomEffect;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPlayer;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CustomEffect extends StatusEffect implements ICustomEffect {

    /**
     * Experimental script stuff.
     */
    public BiConsumer<IPlayer, PlayerEffect> onAddedConsumer, onTickConsumer, onRemovedConsumer;

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


    public void onAdded(BiConsumer<IPlayer, PlayerEffect> function) {
        onAddedConsumer = function;
    }

    public void onTick(BiConsumer<IPlayer, PlayerEffect> function) {
        onTickConsumer = function;
    }

    public void onRemoved(BiConsumer<IPlayer, PlayerEffect> function) {
        onRemovedConsumer = function;
    }

    public void onAdded(EntityPlayer player, PlayerEffect playerEffect) {
        if (onAddedConsumer != null)
            onAddedConsumer.accept(PlayerDataUtil.getIPlayer(player), playerEffect);
    }

    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
        if (onTickConsumer != null)
            onTickConsumer.accept(PlayerDataUtil.getIPlayer(player), playerEffect);
    }

    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect) {
        if (onRemovedConsumer != null)
            onRemovedConsumer.accept(PlayerDataUtil.getIPlayer(player), playerEffect);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setInteger("length", length);
        compound.setInteger("everyXTick", everyXTick);
        compound.setInteger("iconX", iconX);
        compound.setInteger("iconY", iconY);
        compound.setString("icon", icon);
        compound.setBoolean("lossOnDeath", lossOnDeath);

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("ID"))
            id = compound.getInteger("ID");
        else
            id = StatusEffectController.Instance.getUnusedId();
        name = compound.getString("name");
        length = compound.getInteger("length");
        everyXTick = compound.getInteger("everyXTick");
        iconX = compound.getInteger("iconX");
        iconY = compound.getInteger("iconY");
        icon = compound.getString("icon");
        lossOnDeath = compound.getBoolean("lossOnDeath");
    }
}
