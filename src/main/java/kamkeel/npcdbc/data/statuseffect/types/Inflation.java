package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Inflation extends StatusEffect {

    public Inflation() {
        name = "Inflation";
        id = Effects.INFLATION;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/statuseffects.png";
        iconX = 80;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect){}
}
