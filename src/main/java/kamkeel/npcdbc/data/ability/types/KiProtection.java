package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class KiProtection extends AddonAbility {
    public KiProtection() {
        super();
        name = "KiProtection";
        langName = "ability.kiprot";
        langDescription = "ability.kiprotDesc";
        id = DBCAbilities.KI_PROTECTION;
        skillId = 11;
        iconX = 240;
        iconY = 0;
        type = Ability.Type.Toggle;
    }

    @Override
    public boolean onUse(EntityPlayer player) {
        if (!super.onUse(player))
            return false;

        DBCSettingsUtil.setKiProtection(player, !DBCSettingsUtil.isKiProtection(player));
        return true;
    }
}
