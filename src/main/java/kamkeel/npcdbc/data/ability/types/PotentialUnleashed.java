package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class PotentialUnleashed extends AddonAbility {
    public PotentialUnleashed() {
        super();
        name = "PotentialUnleashed";
        langName = "ability.potential";
        langDescription = "ability.potentialDesc";
        id = DBCAbilities.PotentialUnleashed;
        skillId = 10;
        iconX = 0;
        iconY = 0;
        abilityData.type = Ability.Type.Toggle;
    }

    @Override
    public void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setPotentialUnleashed(player, !DBCSettingsUtil.isPotentialUnleashed(player));
    }
}
