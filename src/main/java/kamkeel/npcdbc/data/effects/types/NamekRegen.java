package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.effects.AddonEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;

public class NamekRegen extends AddonEffect {

    public NamekRegen() {
        name = "NamekRegen";
        langName = "effect.namekianregeneration";
        id = Effects.NAMEK_REGEN;
        iconX = 48;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        float currentBodyPercent = dbcData.stats.getCurrentBodyPercentage();
        float percentToRegen = ConfigDBCEffects.NamekRegenPercent * playerEffect.level;
        if (dbcData.Body != 0) {
            if (currentBodyPercent < ConfigDBCGameplay.NamekianRegenMax) {
                boolean kill = false;
                if (currentBodyPercent + percentToRegen > ConfigDBCGameplay.NamekianRegenMax) {
                    percentToRegen = ConfigDBCGameplay.NamekianRegenMax - currentBodyPercent;
                    kill = true;
                }

                if (!ConfigDBCGameplay.OnlyNamekianRegenCharging || dbcData.isChargingKi()) {
                    dbcData.stats.restoreHealthPercent(percentToRegen);
                }

                if (kill) {
                    playerEffect.kill();
                }
            } else {
                playerEffect.kill();
            }
        }
    }
}
