package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.effects.AddonEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;

public class Bloated extends AddonEffect {

    public Bloated() {
        name = "Bloated";
        langName = "effect.bloated";
        id = Effects.BLOATED;
        iconX = 128;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
    }
}
