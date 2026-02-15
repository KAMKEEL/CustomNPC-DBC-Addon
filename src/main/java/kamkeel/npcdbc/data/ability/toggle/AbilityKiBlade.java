package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Ki Blade toggle ability - enables ki-enhanced blade mode.
 */
public class AbilityKiBlade extends DBCToggleAbility {

    public AbilityKiBlade() {
        super("npcdbc:ki_blade");
        this.name = "Ki Blade";
        this.setIconX(288);
        this.setIconY(0);
    }

    @Override
    protected void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setKiWeapon(player, DBCSettingsUtil.getKiWeapon(player) != 0, 0);
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.getKiWeapon(player) == 0;
    }
}
