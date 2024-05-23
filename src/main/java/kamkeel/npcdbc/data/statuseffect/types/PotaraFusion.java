package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.constants.enums.EnumPotaraTypes;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class PotaraFusion extends StatusEffect {

    public PotaraFusion() {
        name = "Potara";
        id = Effects.POTARA;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 176;
        iconY = 0;
    }

    @Override
    public void init(EntityPlayer player, PlayerEffect playerEffect){
        EnumPotaraTypes potaraTypes = EnumPotaraTypes.getPotaraFromMeta(playerEffect.level);
        float bonusMulti = potaraTypes.getMulti();
        if(bonusMulti > 0){
            PlayerBonus bonus = new PlayerBonus(name, (byte) 0, bonusMulti, bonusMulti, bonusMulti);
            BonusController.getInstance().applyBonus(player, bonus);
        }
    }

    @Override
    public void kill(EntityPlayer player, PlayerEffect playerEffect) {
        BonusController.getInstance().removeBonus(player, name);
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        boolean isFused = dbcData.containsSE(10) || dbcData.containsSE(11);
        if(!isFused){
            playerEffect.kill();
            BonusController.getInstance().removeBonus(player, name);
        }
    }
}
