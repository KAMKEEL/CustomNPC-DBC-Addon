package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Bloated extends StatusEffect {

    public Bloated() {
        name = "Bloated";
        id = Effects.BLOATED;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 128;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect){}
}
