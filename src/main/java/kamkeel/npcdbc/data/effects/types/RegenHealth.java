package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.effects.AddonEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;

public class RegenHealth extends AddonEffect {

    public RegenHealth() {
        name = "Health Regeneration";
        langName = "effect.healthregeneration";
        id = Effects.REGEN_HEALTH;
        iconX = 0;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        int percentToRegen = ConfigDBCEffects.HealthRegenPercent * playerEffect.level;
        if (dbcData.Body > 0)
            dbcData.stats.restoreHealthPercent(percentToRegen);
    }
}
