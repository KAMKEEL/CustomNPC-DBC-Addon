package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.effects.AddonEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;

public class Chocolated extends AddonEffect {

    public Chocolated() {
        name = "Chocolated";
        langName = "effect.chocolated";
        id = Effects.CHOCOLATED;
        iconX = 96;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect){}
}
