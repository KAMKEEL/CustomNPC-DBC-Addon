package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.effects.AddonEffect;

public class Harnessed extends AddonEffect {
    public static Aura shockAura = null;

    public Harnessed() {
        name = "Harnessed";
        langName = "effect.harnessed";
        id = Effects.HARNESSED;
        iconX = 16;
        iconY = 16;
        length = -100;
        lossOnDeath = false;
        everyXTick = 10;

        if (shockAura == null) {
            shockAura = new Aura();
            shockAura.id = -10;
            shockAura.display.alpha = 0;
            shockAura.display.type2D = EnumAuraTypes2D.None;

            shockAura.display.hasLightning = true;
            shockAura.display.lightningIntensity = 8;
            shockAura.display.lightningSpeed = 8;
            shockAura.display.lightningAlpha = 255;
            shockAura.display.lightningColor = 0x61E7FF;
            shockAura.display.auraSound = "";
        }
    }


}
