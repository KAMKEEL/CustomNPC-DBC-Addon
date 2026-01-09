package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import net.minecraft.entity.player.EntityPlayer;

public class MultiuseTest extends AddonAbility {
    public MultiuseTest() {
        super();
        name = "test";
        langName = "ability.test";
        id = 17;
        iconX = 336;
        iconY = 0;
        type = Type.Cast;
        cooldown = 300;
        multiUse = true;
        maxUses = 5;
    }

    @Override
    protected void fireEvent(EntityPlayer player, DBCPlayerEvent.AbilityEvent event) {
        NetworkUtility.sendServerMessage(player, "FUCKKKKKKKKKKK");
    }
}
