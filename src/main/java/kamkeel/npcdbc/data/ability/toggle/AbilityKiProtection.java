package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Ki Protection toggle ability - enables ki-based damage protection.
 */
public class AbilityKiProtection extends DBCToggleAbility {

    public AbilityKiProtection() {
        super("npcdbc:ki_protection");
        this.name = "Ki Protection";
        this.setIconX(240);
        this.setIconY(0);
    }

    @Override
    protected void onToggle(EntityPlayer player, boolean newState) {
        DBCSettingsUtil.setKiProtection(player, newState);
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.isKiProtection(player);
    }
}
