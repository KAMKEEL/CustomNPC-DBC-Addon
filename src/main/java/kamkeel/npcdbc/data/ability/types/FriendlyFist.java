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
        langDescription = "ability.friendlyfistDesc";
        id = DBCAbilities.FriendlyFist;
        iconX = 0;
        iconY = 0;
        type = Ability.Type.Toggle;
    }

    @Override
    public void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setFriendlyFist(player, !DBCSettingsUtil.isFriendlyFist(player));
    }
}
