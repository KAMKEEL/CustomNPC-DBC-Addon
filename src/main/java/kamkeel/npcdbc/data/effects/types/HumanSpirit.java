package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.effects.AddonEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;
import noppes.npcs.scripted.event.PlayerEvent;


public class HumanSpirit extends AddonEffect {

    public HumanSpirit() {
        name = "HumanSpirit";
        langName = "effect.humanspirit";
        id = Effects.HUMAN_SPIRIT;
        iconX = 224;
        iconY = 0;
        length = ConfigDBCEffects.HumanSpiritLength;
    }

    @Override
    public void onAdded(EntityPlayer player, PlayerEffect playerEffect){
        DBCData dbcData = DBCData.get(player);
        PlayerBonus humanSpiritBonus = new PlayerBonus(name, (byte) 1);
        humanSpiritBonus.constituion = dbcData.CON * ((float) ConfigDBCEffects.HumanSpiritConBoostPercent / 100);
        humanSpiritBonus.dexterity = dbcData.DEX * ((float) ConfigDBCEffects.HumanSpiritDexBoostPercent / 100);
        BonusController.getInstance().applyBonus(player, humanSpiritBonus);
    }

    @Override
    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect, PlayerEvent.EffectEvent.ExpirationType type) {
        BonusController.getInstance().removeBonus(player, name);

        if(ConfigDBCEffects.EXHAUST_HUMANSPIRIT)
            DBCEffectController.getInstance().applyEffect(player, Effects.EXHAUSTED, ConfigDBCEffects.EXHAUST_HUMANSPIRIT_TIME * 60);
    }
}
