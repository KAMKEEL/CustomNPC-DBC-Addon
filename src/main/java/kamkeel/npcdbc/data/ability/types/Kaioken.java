package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class Kaioken extends AddonAbility {
    public Kaioken() {
        super();
        name = "Kaioken";
        langName = "ability.kaioken";
        langDescription = "ability.kaiokenDesc";
        id = DBCAbilities.Kaioken;
        skillId = 8;
        iconX = 0;
        iconY = 0;
        abilityData.type = Type.Toggle;
    }

    @Override
    public void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setKaioken(player, !DBCSettingsUtil.isKaioken(player));
    }
}
