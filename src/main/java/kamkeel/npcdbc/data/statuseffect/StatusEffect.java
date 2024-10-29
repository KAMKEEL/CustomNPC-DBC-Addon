package kamkeel.npcdbc.data.statuseffect;

import kamkeel.npcdbc.api.effect.IStatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class StatusEffect implements IStatusEffect {
    public int id = -1;

    public String name = "";
    public boolean lossOnDeath = true;
    public int length = 30;

    // Must be a multiple of 10
    public int everyXTick = 20;

    public String icon = "";
    public int iconX = 0, iconY = 0;
    boolean isCustom = false;

    public StatusEffect() {
    }

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
            onTick(player, playerEffect);
        }
    }

    public void onAdded(EntityPlayer player, PlayerEffect playerEffect) {

    }

    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
    }

    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect) {
    }
}
