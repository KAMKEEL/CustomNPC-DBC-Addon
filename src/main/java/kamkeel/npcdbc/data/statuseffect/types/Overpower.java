package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.util.ValueUtil;

public class Overpower extends StatusEffect {

    public Overpower() {
        name = "Overpower";
        id = Effects.OVERPOWER;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 80;
        iconY = 0;
    }

    @Override
    public void kill(EntityPlayer player, PlayerEffect playerEffect) {
        // Ensure Overpower Caps Release back to Default upon Removal
        DBCData dbcData = DBCData.getData(player);
        dbcData.loadCharging();

        byte release = dbcData.Release;
        byte maxRelease = (byte) ((byte) (50 + dbcData.stats.getPotentialUnlockLevel() * 5));

        int newRelease = ValueUtil.clamp(release, (byte) 0, maxRelease);
        dbcData.getRawCompound().setByte("jrmcRelease", (byte) newRelease);

        StatusEffectController.getInstance().applyEffect(player, Effects.EXHAUSTED);
    }
}
