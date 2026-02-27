package kamkeel.npcdbc.data.effects.types;

import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.effects.AddonEffect;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerEffect;
import noppes.npcs.scripted.event.player.PlayerEvent;

public class EvilThirdEye extends AddonEffect {
    public PlayerBonus thirdEyeBonus;

    public EvilThirdEye() {
        name = "EvilThirdEye";
        langName = "effect.evilthirdeye";
        id = Effects.EVIL_THIRD_EYE;
        iconX = 32;
        iconY = 16;
        length = -100;

        thirdEyeBonus = new PlayerBonus(name, (byte) 0);
    }

    @Override
    public void onAdded(EntityPlayer player, PlayerEffect playerEffect) {
        float wil = (float) ConfigDBCEffects.ThirdEyeWilBoostPercent;
        float str = (float) ConfigDBCEffects.ThirdEyeStrBoostPercent;
        float dex = (float) ConfigDBCEffects.ThirdEyeDexBoostPercent;

        float boostIncrease = (ConfigDBCEffects.ThirdEyeBoostMultiplier / 100f) * playerEffect.level;

        thirdEyeBonus.strength  = str * (1 + boostIncrease);
        thirdEyeBonus.willpower = wil * (1 + boostIncrease);
        thirdEyeBonus.dexterity = dex * (1 + boostIncrease);

        BonusController.getInstance().applyBonus(player, thirdEyeBonus);
    }

    @Override
    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect, PlayerEvent.EffectEvent.ExpirationType type) {
        BonusController.getInstance().removeBonus(player, name);
    }
}
