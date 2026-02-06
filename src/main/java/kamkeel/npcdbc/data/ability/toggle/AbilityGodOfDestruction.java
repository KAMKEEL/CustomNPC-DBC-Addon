package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * God of Destruction toggle ability - enables God of Destruction transformation mode.
 */
public class AbilityGodOfDestruction extends DBCToggleAbility {

    public AbilityGodOfDestruction() {
        super("npcdbc:god_of_destruction");
    }

    @Override
    protected void onToggle(EntityPlayer player) {
        DBCSettingsUtil.setGOD(player, !DBCSettingsUtil.isGOD(player));
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.isGOD(player);
    }
}
