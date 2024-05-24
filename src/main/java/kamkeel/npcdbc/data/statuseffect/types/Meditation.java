package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Meditation extends StatusEffect {

    public Meditation() {
        name = "Meditation";
        id = Effects.MEDITATION;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 112;
        iconY = 0;
    }

    @Override
    public void init(EntityPlayer player, PlayerEffect playerEffect){
        DBCData dbcData = DBCData.get(player);
        PlayerBonus medBonus = new PlayerBonus(name, (byte) 1);
        medBonus.spirit = dbcData.SPI * ((float) ConfigDBCEffects.MeditationSpiBoostPercent / 100);
        BonusController.getInstance().applyBonus(player, medBonus);
    }

    @Override
    public void kill(EntityPlayer player, PlayerEffect playerEffect) {
        BonusController.getInstance().removeBonus(player, name);
    }
}
