package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class FriendlyFist extends AddonAbility {
    public FriendlyFist() {
        super();
        name = "FriendlyFist";
        langName = "ability.friendlyfist";
        id = DBCAbilities.FRIENDLY_FIST;
        iconX = 0;
        iconY = 0;
        type = Ability.Type.Toggle;
    }

    @Override
    public boolean onUse(EntityPlayer player) {
        if (!super.onUse(player))
            return false;

        DBCSettingsUtil.setFriendlyFist(player, !DBCSettingsUtil.isFriendlyFist(player));
        return true;
    }
}
