package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class RegenKi extends StatusEffect {

    public RegenKi(int timer) {
        super(timer);
        name = "Ki Regeneration";
        id = Effects.REGEN_KI;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/statuseffects.png";
        iconX = 0;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player) {
        DBCData dbcData = DBCData.get(player);
        int percentToRegen = ConfigDBCEffects.KiRegenPercent * this.level;
        dbcData.restoreHealthPercent(percentToRegen);
    }
}
