package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.effects.AddonEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;
import noppes.npcs.scripted.event.player.PlayerEvent;

public class Meditation extends AddonEffect {

    public Meditation() {
        name = "Meditation";
        langName = "effect.meditation";
        id = Effects.MEDITATION;
        iconX = 112;
        iconY = 0;
    }

    @Override
    public void onAdded(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        PlayerBonus medBonus = new PlayerBonus(name, (byte) 1);
        medBonus.spirit = dbcData.SPI * ((float) ConfigDBCEffects.MeditationSpiBoostPercent / 100);
        BonusController.getInstance().applyBonus(player, medBonus);
    }

    @Override
    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect, PlayerEvent.EffectEvent.ExpirationType type) {
        BonusController.getInstance().removeBonus(player, name);
    }
}
