package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class KiFist extends AddonAbility {
    public KiFist() {
        super();
        name = "KiFist";
        langName = "ability.kifist";
        id = DBCAbilities.KI_FIST;
        skillId = 12;
        iconX = 192;
        iconY = 0;
        type = Ability.Type.Toggle;
    }

    @Override
    public boolean onUse(EntityPlayer player) {
        if (!super.onUse(player))
            return false;

        DBCSettingsUtil.setKiFist(player, !DBCSettingsUtil.isKiFist(player));
        return true;
    }
}
