package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Friendly Fist toggle ability - enables non-hostile combat mode.
 */
public class AbilityFriendlyFist extends AbilityDBCToggle {

    public AbilityFriendlyFist() {
        this.name = "Friendly Fist";
        this.setIconX(0);
        this.setIconY(0);
    }

    @Override
    protected void onToggle(EntityPlayer player, boolean newState) {
        DBCSettingsUtil.setFriendlyFist(player, newState);
    }
}
