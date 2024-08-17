package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;

public class HumanSpirit extends StatusEffect {

    public HumanSpirit() {
        name = "HumanSpirit";
        id = Effects.HUMAN_SPIRIT;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 192;
        iconY = 0;
        length = ConfigDBCEffects.HumanSpiritLength;
        lossOnDeath = false;
    }
}
