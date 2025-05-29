package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.effects.AddonEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;

public class RegenKi extends AddonEffect {

    public RegenKi() {
        name = "Ki Regeneration";
        langName = "effect.kiregeneration";
        id = Effects.REGEN_KI;
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
