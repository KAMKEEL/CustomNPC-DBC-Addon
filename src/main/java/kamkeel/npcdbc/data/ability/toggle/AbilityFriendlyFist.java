package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Friendly Fist toggle ability - enables non-hostile combat mode.
 */
public class AbilityFriendlyFist extends DBCToggleAbility {

    public AbilityFriendlyFist() {
        super("npcdbc:friendly_fist");
    }

    @Override
    protected void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setFriendlyFist(player, !DBCSettingsUtil.isFriendlyFist(player));
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.isFriendlyFist(player);
    }
}
