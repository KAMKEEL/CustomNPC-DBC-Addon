package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Potential Unleashed toggle ability - enables Potential Unleashed transformation.
 */
public class AbilityPotentialUnleashed extends DBCToggleAbility {

    public AbilityPotentialUnleashed() {
        super("npcdbc:potential_unleashed");
    }

    @Override
    protected void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setPotentialUnleashed(player, !DBCSettingsUtil.isPotentialUnleashed(player));
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.isPotentialUnleashed(player);
    }
}
