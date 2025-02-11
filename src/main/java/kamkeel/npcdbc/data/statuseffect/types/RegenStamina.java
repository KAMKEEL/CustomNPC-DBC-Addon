package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;

public class RegenStamina extends StatusEffect {

    public RegenStamina() {
        name = "Stamina Regeneration";
        langName = "effect.staminaregeneration";
        id = Effects.REGEN_STAMINA;
        iconX = 32;
        iconY = 0;
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        int percentToRegen = ConfigDBCEffects.StaminaRegenPercent * playerEffect.level;
        dbcData.stats.restoreStaminaPercent(percentToRegen);
    }
}
