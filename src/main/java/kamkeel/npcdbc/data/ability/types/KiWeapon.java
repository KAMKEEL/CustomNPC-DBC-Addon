package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class KiWeapon extends AddonAbility {
    public KiWeapon() {
        super();
        name = "KiWeapon";
        langName = "ability.kiWeapon";
        langDescription = "ability.kiWeaponDesc";
        id = DBCAbilities.KiWeapon;
        iconX = 0;
        iconY = 0;
        abilityData.type = Ability.Type.Toggle;
    }

    @Override
    public void onToggle(EntityPlayer player) {
        int mode = DBCSettingsUtil.getKiWeapon(player);

        if (mode == -1) {
            DBCSettingsUtil.setKiWeapon(player, true, 0);
        } else if (mode == 0) {
            DBCSettingsUtil.setKiWeapon(player, true, 1);
        } else if (mode == 1) {
            DBCSettingsUtil.setKiWeapon(player, false);
        }
    }
}
