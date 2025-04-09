package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.effects.AddonEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;
import noppes.npcs.scripted.event.player.PlayerEvent;

public class Zenkai extends AddonEffect {
    public PlayerBonus saiyanZenkai;
    public PlayerBonus halfSaiyanZenkai;

    public Zenkai() {
        name = "Zenkai";
        langName = "effect.zenkai";
        id = Effects.ZENKAI;
        iconX = 160;
        iconY = 0;
        length = ConfigDBCEffects.ZenkaiHALFLength;

        saiyanZenkai = new PlayerBonus("Saiyan" + name, (byte) 0, (float) ConfigDBCEffects.ZenkaiSaiyanStr, (float) ConfigDBCEffects.ZenkaiSaiyanDex, (float) ConfigDBCEffects.ZenkaiSaiyanWil);
        halfSaiyanZenkai = new PlayerBonus("HalfSaiyan" + name, (byte) 0, (float) ConfigDBCEffects.ZenkaiHALFStr, (float) ConfigDBCEffects.ZenkaiHALFDex, (float) ConfigDBCEffects.ZenkaiHALFWil);
    }

    @Override
    public void onAdded(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        if (dbcData.Race == DBCRace.SAIYAN)
            BonusController.getInstance().applyBonus(player, saiyanZenkai);
        else
            BonusController.getInstance().applyBonus(player, halfSaiyanZenkai);
    }

    @Override
    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect, PlayerEvent.EffectEvent.ExpirationType type) {
        DBCData dbcData = DBCData.get(player);
        if (dbcData.Race == DBCRace.SAIYAN)
            BonusController.getInstance().removeBonus(player, saiyanZenkai);
        else
            BonusController.getInstance().removeBonus(player, halfSaiyanZenkai);


        if (ConfigDBCEffects.EXHAUST_ZENKAI)
            DBCEffectController.getInstance().applyEffect(player, Effects.EXHAUSTED, ConfigDBCEffects.EXHAUST_ZENKAI_TIME * 60);
    }
}
