package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class RegenEffect extends StatusEffect {

    public RegenEffect(int timer) {
        super(timer);
        id = 1;
        name = "Regeneration";

    }

    @Override
    public void processEffect(EntityPlayer player){

    }
}
