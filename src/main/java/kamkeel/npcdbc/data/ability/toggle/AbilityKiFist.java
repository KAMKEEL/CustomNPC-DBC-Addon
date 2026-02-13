package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Ki Fist toggle ability - enables ki-enhanced melee attacks.
 */
public class AbilityKiFist extends DBCToggleAbility {

    public AbilityKiFist() {
        super("npcdbc:ki_fist");
        this.name = "Ki Fist";
    }

    @Override
    protected void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setKiFist(player, !DBCSettingsUtil.isKiFist(player));
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.isKiFist(player);
    }
}
