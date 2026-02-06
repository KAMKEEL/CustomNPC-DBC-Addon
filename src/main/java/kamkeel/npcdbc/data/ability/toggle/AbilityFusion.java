package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Fusion toggle ability - enables Fusion (Potara/Dance) transformation mode.
 */
public class AbilityFusion extends DBCToggleAbility {

    public AbilityFusion() {
        super("npcdbc:fusion");
    }

    @Override
    protected void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setFusion(player, !DBCSettingsUtil.isFusion(player));
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.isFusion(player);
    }
}
