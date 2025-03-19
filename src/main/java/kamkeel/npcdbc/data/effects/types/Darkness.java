package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.effects.AddonEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;

public class Darkness extends AddonEffect {

    public Darkness() {
        name = "Darkness";
        langName = "effect.darkness";
        id = Effects.DARKNESS;
        iconX = 144;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
    }
}
