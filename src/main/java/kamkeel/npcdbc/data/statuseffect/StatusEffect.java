package kamkeel.npcdbc.data.statuseffect;

import net.minecraft.entity.player.EntityPlayer;

public class StatusEffect {
    public int id = -1, timer = -1;
    public String name = "", icon = "", type = "";

    public boolean lossOnDeath = true;

    public StatusEffect(int timer) {
        this.timer = timer;
    }

    public int getTimer() {
        return timer;
    }

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

    public void processEffect(EntityPlayer player) {

    }
}
