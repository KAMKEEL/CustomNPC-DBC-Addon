package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Kaioken toggle ability - enables Kaioken transformation mode.
 */
public class AbilityKaioken extends DBCToggleAbility {

    public AbilityKaioken() {
        super("npcdbc:kaioken");
    }

    @Override
    protected void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setKaioken(player, !DBCSettingsUtil.isKaioken(player));
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.isKaioken(player);
    }
}
