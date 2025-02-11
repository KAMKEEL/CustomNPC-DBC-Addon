package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;
import noppes.npcs.scripted.event.PlayerEvent;
import noppes.npcs.util.ValueUtil;

public class Overpower extends StatusEffect {

    public Overpower() {
        name = "Overpower";
        id = Effects.OVERPOWER;
        iconX = 80;
        iconY = 0;
    }

    @Override
    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect, PlayerEvent.EffectEvent.ExpirationType type) {
        // Ensure Overpower Caps Release back to Default upon Removal
        DBCData dbcData = DBCData.getData(player);
        dbcData.loadCharging();

        byte release = dbcData.Release;
        byte maxRelease = (byte) ((byte) (50 + dbcData.stats.getPotentialUnlockLevel() * 5));

        int newRelease = ValueUtil.clamp(release, (byte) 0, maxRelease);
        dbcData.getRawCompound().setByte("jrmcRelease", (byte) newRelease);

        if(ConfigDBCEffects.EXHAUST_OVERPOWER)
            DBCEffectController.getInstance().applyEffect(player, Effects.EXHAUSTED, ConfigDBCEffects.EXHAUST_OVERPOWER_TIME * 60);
    }
}
