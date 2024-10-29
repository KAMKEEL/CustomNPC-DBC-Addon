package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class NamekRegen extends StatusEffect {

    public NamekRegen() {
        name = "NamekRegen";
        id = Effects.NAMEK_REGEN;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 48;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect){
        DBCData dbcData = DBCData.get(player);
        float currentBodyPercent = dbcData.stats.getCurrentBodyPercentage();
        float percentToRegen = ConfigDBCEffects.NamekRegenPercent * playerEffect.level;
        if(dbcData.Body != 0){
            if(currentBodyPercent < ConfigDBCGameplay.NamekianRegenMax){
                boolean kill = false;
                if (currentBodyPercent + percentToRegen > ConfigDBCGameplay.NamekianRegenMax) {
                    percentToRegen = ConfigDBCGameplay.NamekianRegenMax - currentBodyPercent;
                    kill = true;
                }
                dbcData.stats.restoreHealthPercent(percentToRegen);
                if (kill) {
                    playerEffect.kill();
                }
            } else {
                playerEffect.kill();
            }
        }
    }
}
