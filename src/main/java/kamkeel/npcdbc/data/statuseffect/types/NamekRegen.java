package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class NamekRegen extends StatusEffect {

    public NamekRegen(int timer) {
        super(timer);
        name = "NamekRegen";
        id = Effects.NAMEK_REGEN;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/statuseffects.png";
        iconX = 0;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player){
        DBCData dbcData = DBCData.get(player);
        float currentBodyPercent = dbcData.getCurrentBodyPercentage();
        float percentToRegen = ConfigDBCEffects.NamekRegenPercent * this.level;
        if(dbcData.Body != 0 && currentBodyPercent < ConfigDBCGameplay.NamekianRegenMax){
            boolean kill = false;
            if (currentBodyPercent + percentToRegen > ConfigDBCGameplay.NamekianRegenMax) {
                percentToRegen = ConfigDBCGameplay.NamekianRegenMax - currentBodyPercent;
                kill = true;
            }
            dbcData.restoreHealthPercent(percentToRegen);
            if (kill) {
                this.kill();
            }
        } else {
            this.kill();
        }
    }
}
