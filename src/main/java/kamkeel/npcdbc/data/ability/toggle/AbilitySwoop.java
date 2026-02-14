package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Swoop toggle ability - enables dodge/swoop movement mode.
 */
public class AbilitySwoop extends DBCToggleAbility {

    public AbilitySwoop() {
        super("npcdbc:swoop");
        this.name = "Swoop";
    }

    @Override
    protected void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setSwoop(player, !DBCSettingsUtil.isSwoop(player));
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.isSwoop(player);
    }
}
