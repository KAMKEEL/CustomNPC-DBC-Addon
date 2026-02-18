package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Swoop toggle ability - enables dodge/swoop movement mode.
 */
public class AbilitySwoop extends AbilityDBCToggle {

    public AbilitySwoop() {
        this.name = "Swoop";
        this.setIconX(48);
        this.setIconY(0);
    }

    @Override
    protected void onToggle(EntityPlayer player, boolean newState) {
        DBCSettingsUtil.setSwoop(player, newState);
    }
}
