package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;

public class Darkness extends StatusEffect {

    public Darkness() {
        name = "Darkness";
        id = Effects.DARKNESS;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 144;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect){}
}
