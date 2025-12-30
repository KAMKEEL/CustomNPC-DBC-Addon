package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class GodOfDestruction extends AddonAbility {
    public GodOfDestruction() {
        super();
        name = "GodOfDestruction";
        langName = "ability.god";
        langDescription = "ability.godDesc";
        id = DBCAbilities.GODOFDESTRUCTION.ordinal();
        iconX = 0;
        iconY = 0;
        abilityData.type = Ability.Type.Toggle;
    }

    @Override
    public void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setGOD(player, !DBCSettingsUtil.isGOD(player));
    }
}
