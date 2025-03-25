package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.effects.AddonEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;
import noppes.npcs.scripted.event.PlayerEvent;

public class Chocolated extends AddonEffect {

    public PlayerBonus ChocolatedDebuff;
    public boolean fusionAffect = ConfigDBCEffects.CHOC_AffectFusion;

    public Chocolated() {
        name = "Chocolated";
        langName = "effect.chocolated";
        id = Effects.CHOCOLATED;
        iconX = 96;
        iconY = 0;
        ChocolatedDebuff = new PlayerBonus(name, (byte) 0, (float) ConfigDBCEffects.CHOC_Str, (float) ConfigDBCEffects.CHOC_Dex, (float) ConfigDBCEffects.CHOC_Wil);
        length = ConfigDBCEffects.CHOC_EffectLength;

    }

    @Override
    public void onAdded(EntityPlayer player, PlayerEffect playerEffect) {
        BonusController.getInstance().applyBonus(player, ChocolatedDebuff);
        DBCData data = DBCData.get(player);
        if (!fusionAffect && data.stats.isFused()) {
            return;
        }
        data.sprintSpeed = 0.1F;
        data.baseFlightSpeed = 0.1F;
        data.saveNBTData(false);
        // BonusController.getInstance().applyBonus(player,ChocolatedDebuff);
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
    }

    @Override
    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect, PlayerEvent.EffectEvent.ExpirationType type) {
        BonusController.getInstance().removeBonus(player, ChocolatedDebuff);
        DBCData data = DBCData.get(player);
        data.sprintSpeed = 1.0F;
        data.baseFlightSpeed = 1.0F;
        data.saveNBTData(false);
    }
}
