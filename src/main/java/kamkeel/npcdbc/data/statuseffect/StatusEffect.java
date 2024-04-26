package kamkeel.npcdbc.data.statuseffect;

import kamkeel.npcdbc.IStatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class StatusEffect implements IStatusEffect {
    public int id = -1;
    public int timer = -1;

    // Must be a multiple of 10
    public int everyXTick = 20;
    public String name = "", icon = "", type = "";
    public int iconX = 0, iconY = 0;

    public boolean lossOnDeath = true;

    public StatusEffect(int timer) {
        this.timer = timer;
    }

    @Override
    public int getTimer() {
        return timer;
    }

    @Override
    public void setTimer(int timer) {
        this.timer = timer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEveryXTick() {
        return everyXTick;
    }

    public void setEveryXTick(int everyXTick) {
        this.everyXTick = everyXTick;
    }

    public void runEffect(EntityPlayer player) {
        if (player.ticksExisted % everyXTick == 0) {
            process(player);
        }
    }

    public boolean hasIcon() {
        return icon.length() < 3;
    }

    public void process(EntityPlayer player) {
    }
}
