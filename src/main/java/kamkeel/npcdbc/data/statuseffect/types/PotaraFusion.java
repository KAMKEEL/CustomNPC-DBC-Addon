package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.constants.enums.EnumPotaraTypes;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;
import noppes.npcs.scripted.event.PlayerEvent;

public class PotaraFusion extends StatusEffect {

    public PotaraFusion() {
        name = "Potara";
        langName = "effect.potara";
        id = Effects.POTARA;
        iconX = 176;
        iconY = 0;
    }

    @Override
    public void onAdded(EntityPlayer player, PlayerEffect playerEffect){
        EnumPotaraTypes potaraTypes = EnumPotaraTypes.getPotaraFromMeta(playerEffect.level);
        float bonusMulti = potaraTypes.getMulti();
        if(bonusMulti > 0){
            PlayerBonus bonus = new PlayerBonus(name, (byte) 0, bonusMulti, bonusMulti, bonusMulti);
            BonusController.getInstance().applyBonus(player, bonus);
        }
    }

    @Override
    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect, PlayerEvent.EffectEvent.ExpirationType type) {
        BonusController.getInstance().removeBonus(player, name);
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        boolean isFused = dbcData.containsSE(10) || dbcData.containsSE(11);
        if(!isFused){
            playerEffect.kill();
            BonusController.getInstance().removeBonus(player, name);
        }
    }
}
