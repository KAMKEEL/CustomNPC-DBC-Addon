package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Ki Weapon toggle ability - enables ki-enhanced weapon mode.
 */
public class AbilityKiWeapon extends DBCToggleAbility {

    public AbilityKiWeapon() {
        super("npcdbc:ki_weapon");
        this.name = "Ki Weapon";
    }

    @Override
    protected void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setKiWeapon(player, !DBCSettingsUtil.hasKiWeapon(player));
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.hasKiWeapon(player);
    }
}
