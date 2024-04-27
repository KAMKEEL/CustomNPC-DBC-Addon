package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class RegenHealth extends StatusEffect {

    public RegenHealth(int timer) {
        super(timer);
        name = "Health Regeneration";
        id = Effects.REGEN_HEALTH;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/statuseffects.png";
        iconX = 0;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player) {
        DBCData dbcData = DBCData.get(player);
        int percentToRegen = ConfigDBCEffects.HealthRegenPercent * this.level;
        if(dbcData.Body > 0)
            dbcData.restoreHealthPercent(percentToRegen);
    }
}
