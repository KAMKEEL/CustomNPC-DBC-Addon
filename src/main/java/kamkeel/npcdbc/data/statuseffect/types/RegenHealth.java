package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;

public class RegenHealth extends StatusEffect {

    public RegenHealth() {
        name = "Health Regeneration";
        id = Effects.REGEN_HEALTH;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 0;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        int percentToRegen = ConfigDBCEffects.HealthRegenPercent * playerEffect.level;
        if(dbcData.Body > 0)
            dbcData.stats.restoreHealthPercent(percentToRegen);
    }
}
