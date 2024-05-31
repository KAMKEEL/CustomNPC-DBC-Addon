package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Fatigue extends StatusEffect {
    public Fatigue() {
        name = "Fatigue";
        id = Effects.FATIGUE;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 192;
        iconY = 0;
        length = ConfigDBCEffects.FATIGUE_TIME * 60;
        lossOnDeath = false;
    }
}
