package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class AbilityKiScythe extends DBCToggleAbility {

    public AbilityKiScythe() {
        super("npcdbc:ki_scythe");
        this.name = "Ki Scythe";
        this.setIconX(336);
        this.setIconY(0);
    }

    @Override
    protected void onToggle(EntityPlayer player, boolean newState) {
        DBCSettingsUtil.setKiWeapon(player, newState, 1);
    }

    @Override
    public boolean isActive(EntityPlayer player) {
        return DBCSettingsUtil.getKiWeapon(player) == 1;
    }
}
