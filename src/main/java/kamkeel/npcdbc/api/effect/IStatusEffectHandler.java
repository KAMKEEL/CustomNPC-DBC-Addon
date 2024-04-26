package kamkeel.npcdbc.api.effect;

import net.minecraft.entity.player.EntityPlayer;

public interface IStatusEffectHandler {

    boolean hasEffectTime(EntityPlayer player, int id);

    int getEffectDuration(EntityPlayer player, int id);

    void applyEffect(EntityPlayer player, int id, int duration, byte level);

    void removeEffect(EntityPlayer player, int id);
}
