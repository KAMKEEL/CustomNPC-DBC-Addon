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
        id = DBCAbilities.KIPROTECTION.ordinal();
        iconX = 0;
        iconY = 0;
        abilityData.type = Ability.Type.Toggle;
    }

    @Override
    public void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setKiProtection(player, !DBCSettingsUtil.isKiProtection(player));
    }
}
