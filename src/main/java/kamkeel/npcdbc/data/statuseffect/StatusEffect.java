package kamkeel.npcdbc.data.statuseffect;

import kamkeel.npcdbc.api.effect.IStatusEffect;
import kamkeel.npcdbc.controllers.StatusEffectController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPlayer;

public class StatusEffect implements IStatusEffect {
    public int id = -1;

    public String name = "";
    public int duration = -1;
    public byte level = 1;
    public boolean lossOnDeath = true;

    // Must be a multiple of 10
    public int everyXTick = 20;

    public String icon = "";
    public int iconX = 0, iconY = 0;
    boolean isCustom = false;

    public StatusEffect(int duration) {
        this.duration = duration;
    }

    public static StatusEffect readEffectData(NBTTagCompound nbt) {
        int id = nbt.getInteger("Id");
        if (id > 0) {
            boolean found = StatusEffectController.getInstance().standardEffects.containsKey(id) ||
                    StatusEffectController.getInstance().customEffects.containsKey(id);
            if (found) {
                byte level = nbt.getByte("Level");
                int dur = nbt.getInteger("Dur");
                if (id >= 100) {
                    CustomEffect customEffect = new CustomEffect(id, dur);
                    customEffect.level = level;
                    return customEffect;
                } else {
                    StatusEffect statusEffect = new StatusEffect(dur);
                    statusEffect.id = id;
                    statusEffect.level = level;
                    return statusEffect;
                }
            }
        }
        return null;
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
        return name;
    }

    public void runEffect(EntityPlayer player) {
        if (player.ticksExisted % everyXTick == 0) {
            process(player);
        }
    }

    public void init(EntityPlayer player) {

    }

    public void process(EntityPlayer player) {
    }

    public void runout(EntityPlayer player) {

    }

    public void kill() {
        duration = 0;
    }

    @Override
    public void performEffect(IPlayer player) {
        if (player != null && player.getMCEntity() != null && player.getMCEntity() instanceof EntityPlayer) {
            process((EntityPlayer) player.getMCEntity());
        }
    }

    public NBTTagCompound writeEffectData(NBTTagCompound nbt) {
        nbt.setInteger("Id", this.id);
        nbt.setByte("Level", this.getLevel());
        nbt.setInteger("Dur", this.getDuration());
        return nbt;
    }
}
