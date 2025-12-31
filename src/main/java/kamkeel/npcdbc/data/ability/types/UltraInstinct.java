package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class UltraInstinct extends AddonAbility {
    public UltraInstinct() {
        super();
        name = "UltraInstinct";
        langName = "ability.ui";
        langDescription = "ability.uiDesc";
        id = DBCAbilities.UltraInstinct;
        skillId = 16;
        iconX = 0;
        iconY = 0;
        abilityData.type = Ability.Type.Toggle;
    }

    @Override
    public void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setUI(player, !DBCSettingsUtil.isUI(player));
    }
}
