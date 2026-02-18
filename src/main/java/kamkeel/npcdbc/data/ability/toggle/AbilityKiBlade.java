package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Ki Blade toggle ability - enables ki-enhanced blade mode.
 */
public class AbilityKiBlade extends AbilityDBCToggle {

    public AbilityKiBlade() {
        this.name = "Ki Blade";
        this.setIconX(288);
        this.setIconY(0);
    }

    @Override
    protected void onToggle(EntityPlayer player, boolean newState) {
        DBCSettingsUtil.setKiWeapon(player, newState, 0);
    }
}
