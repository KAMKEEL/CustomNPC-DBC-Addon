package kamkeel.npcdbc.data.statuseffect;

import kamkeel.npcdbc.api.effect.IStatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class StatusEffect implements IStatusEffect {
    public int id = -1;

    public String name = "";
    public boolean lossOnDeath = true;

    // Must be a multiple of 10
    public int everyXTick = 20;

    public String icon = "";
    public int iconX = 0, iconY = 0;
    boolean isCustom = false;

    public StatusEffect(){}

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void runEffect(EntityPlayer player, PlayerEffect playerEffect) {
        if (player.ticksExisted % everyXTick == 0) {
            process(player, playerEffect);
        }
    }

    public void init(EntityPlayer player, PlayerEffect playerEffect) {

    }

    public void process(EntityPlayer player, PlayerEffect playerEffect) {
    }

    public void runout(EntityPlayer player, PlayerEffect playerEffect) {

    }
}
