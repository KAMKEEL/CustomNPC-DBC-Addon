package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class MajinRegen extends StatusEffect {

    public MajinRegen() {
        name = "Majin Regen";
        id = Effects.NAMEK_REGEN;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 48;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect){
        DBCData dbcData = DBCData.get(player);
        float currentBodyPercent = dbcData.stats.getCurrentBodyPercentage();
        float percentToRegen = ConfigDBCEffects.NamekRegenPercent;
        if(dbcData.Body != 0){
            if(currentBodyPercent < 100){
                boolean kill = false;
                if (currentBodyPercent + percentToRegen > 100) {
                    percentToRegen = 100 - currentBodyPercent;
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
