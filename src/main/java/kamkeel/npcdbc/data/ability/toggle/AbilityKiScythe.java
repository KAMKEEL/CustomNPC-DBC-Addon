package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public class AbilityKiScythe extends AbilityDBCToggle {

    public AbilityKiScythe() {
        this.name = "Ki Scythe";
        this.setIconX(336);
        this.setIconY(0);
    }

    @Override
    protected void onToggle(EntityPlayer player, boolean newState) {
        DBCSettingsUtil.setKiWeapon(player, newState, 1);
    }
}
