package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class Fusion extends AddonAbility {
    public Fusion() {
        super();
        name = "Fusion";
        langName = "ability.fusion";
        langDescription = "ability.fusionDesc";
        id = DBCAbilities.FUSION;
        skillId = 0;
        iconX = 0;
        iconY = 0;
        type = Ability.Type.Toggle;
    }

    @Override
    public boolean callEvent(EntityPlayer player) {
        if (!super.callEvent(player))
            return false;

        DBCSettingsUtil.setFusion(player, !DBCSettingsUtil.isFusion(player));
        return true;
    }
}
