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
        langDescription = "ability.kifistDesc";
        id = DBCAbilities.KiFist;
        skillId = 12;
        iconX = 0;
        iconY = 0;
        type = Ability.Type.Toggle;
    }

    @Override
    public void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setKiFist(player, !DBCSettingsUtil.isKiFist(player));
    }
}
