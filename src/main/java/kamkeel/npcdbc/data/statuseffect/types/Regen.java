package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Regen extends StatusEffect {

    public Regen(int timer) {
        super(timer);
        name = "Regeneration";
        id = Effects.REGEN;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/statuseffects.png";
        iconX = 0;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player){}
}
