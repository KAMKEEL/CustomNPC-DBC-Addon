package kamkeel.npcdbc.data.statuseffect;

import kamkeel.npcdbc.api.effect.IPlayerEffect;
import kamkeel.npcdbc.controllers.StatusEffectController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPlayer;

public class PlayerEffect implements IPlayerEffect {
    public int id;
    public int duration;
    public byte level;

    public PlayerEffect(int id, int duration, byte level) {
        this.id = id;
        this.duration = duration;
        this.level = level;
    }

    public static PlayerEffect readEffectData(NBTTagCompound nbt) {
        int id = nbt.getInteger("Id");
        if (id > 0) {
            boolean found = StatusEffectController.getInstance().standardEffects.containsKey(id) ||
                StatusEffectController.getInstance().customEffects.containsKey(id);
            if (found) {
                byte level = nbt.getByte("Level");
                int dur = nbt.getInteger("Dur");
                return new PlayerEffect(id, dur, level);
            }
        }
        return null;
    }

    public NBTTagCompound writeEffectData(NBTTagCompound nbt) {
        nbt.setInteger("Id", this.id);
        nbt.setByte("Level", level);
        nbt.setInteger("Dur", duration);
        return nbt;
    }

    @Override
    public void kill() {
        duration = 0;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public byte getLevel() {
        return level;
    }

    @Override
    public void setLevel(byte level) {
        this.level = level;
    }

    @Override
    public String getName() {
        StatusEffect effect = StatusEffectController.getInstance().get(this.id);
        if(effect != null)
            return effect.getName();

        return "UNKNOWN";
    }

    @Override
    public void performEffect(IPlayer player) {
        if (player != null && player.getMCEntity() != null && player.getMCEntity() instanceof EntityPlayer) {
            StatusEffect effect = StatusEffectController.getInstance().get(this.id);
            if(effect != null)
                effect.onTick((EntityPlayer) player.getMCEntity(), this);
        }
    }
}
