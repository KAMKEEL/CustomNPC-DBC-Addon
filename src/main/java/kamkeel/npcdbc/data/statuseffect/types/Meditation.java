package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;

public class Meditation extends StatusEffect {

    public PlayerBonus meditationBonus;

    public Meditation() {
        name = "Meditation";
        id = Effects.MEDITATION;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/statuseffects.png";
        iconX = 96;
        iconY = 0;

        meditationBonus = new PlayerBonus(name, (byte) 1, (float) ConfigDBCEffects.FOM_Strength, (float) ConfigDBCEffects.FOM_Dex, (float) ConfigDBCEffects.FOM_Will);
    }

    @Override
    public void init(EntityPlayer player, PlayerEffect playerEffect){
        BonusController.getInstance().applyBonus(player, meditationBonus);
    }

    @Override
    public void runout(EntityPlayer player, PlayerEffect playerEffect) {
        PlayerDBCInfo c = PlayerDataUtil.getDBCInfo(player);
        BonusController.getInstance().removeBonus(player, meditationBonus);
    }
}
