package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Inflation extends StatusEffect {

    public Inflation(int timer) {
        super(timer);
        name = "Inflation";
        id = Effects.INFLATION;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/statuseffects.png";
        iconX = 0;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player){}
}
