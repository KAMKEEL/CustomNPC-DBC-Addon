package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class Swoop extends AddonAbility {
    public Swoop() {
        super();
        name = "Swoop";
        langName = "ability.swoop";
        langDescription = "ability.swoopDesc";
        id = DBCAbilities.Swoop;
        iconX = 0;
        iconY = 0;
        type = Ability.Type.Toggle;
    }

    @Override
    public void onToggle(EntityPlayer player) {
        super.onToggle(player);
        DBCSettingsUtil.setSwoop(player, !DBCSettingsUtil.isSwoop(player));
    }
}
