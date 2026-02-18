package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Ki Fist toggle ability - enables ki-enhanced melee attacks.
 */
public class AbilityKiFist extends AbilityDBCToggle {

    public AbilityKiFist() {
        this.name = "Ki Fist";
        this.setIconX(192);
        this.setIconY(0);
    }

    @Override
    protected void onToggle(EntityPlayer player, boolean newState) {
        DBCSettingsUtil.setKiFist(player, newState);
    }
}
