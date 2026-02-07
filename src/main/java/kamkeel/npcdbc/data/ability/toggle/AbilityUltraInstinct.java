package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Ultra Instinct toggle ability - enables Ultra Instinct transformation mode.
 */
public class AbilityUltraInstinct extends DBCToggleAbility {

    public AbilityUltraInstinct() {
        super("npcdbc:ultra_instinct");
    }

    @Override
    protected void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setUI(player, !DBCSettingsUtil.isUI(player));
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.isUI(player);
    }
}
