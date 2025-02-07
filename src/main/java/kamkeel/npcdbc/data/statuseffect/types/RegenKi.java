package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class RegenKi extends StatusEffect {

    public RegenKi() {
        name = "Ki Regeneration";
        id = Effects.REGEN_KI;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 16;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        int percentToRestore = ConfigDBCEffects.KiRegenPercent * playerEffect.level;
        dbcData.stats.restoreKiPercent(percentToRestore);
    }
}
