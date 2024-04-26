package kamkeel.npcdbc.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IStatusEffectHandler {

    boolean hasEffectTime(EntityPlayer player, int id);

    int getEffectTime(EntityPlayer player, int id);

    void applyEffect(EntityPlayer player, int id, int timer);

    void removeEffect(EntityPlayer player, int id);
}
