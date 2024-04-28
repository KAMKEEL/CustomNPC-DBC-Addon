package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Zenkai extends StatusEffect {

    public Zenkai() {
        name = "Zenkai";
        id = Effects.ZENKAI;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/statuseffects.png";
        iconX = 160;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect){}
}
