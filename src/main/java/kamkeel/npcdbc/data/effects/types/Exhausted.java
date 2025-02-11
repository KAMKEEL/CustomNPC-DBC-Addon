package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.effects.AddonEffect;

public class Exhausted extends AddonEffect {
    public Exhausted() {
        name = "Exhausted";
        langName = "effect.exhausted";
        id = Effects.EXHAUSTED;
        iconX = 192;
        iconY = 0;
        length = ConfigDBCEffects.EXHAUST_TIME * 60;
        lossOnDeath = false;
    }
}
