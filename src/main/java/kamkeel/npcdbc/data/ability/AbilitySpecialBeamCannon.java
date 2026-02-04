package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityLaserShot;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;

public class AbilitySpecialBeamCannon extends AbilityLaserShot {
    public AbilitySpecialBeamCannon() {
        this.typeId = "ability.npcdbc.special_beam_cannon";
        this.name = "Special Beam Cannon";
        this.targetingMode = TargetingMode.AGGRO_TARGET;
        this.maxRange = 35.0f;
        this.minRange = 3.0f;
        this.cooldownTicks = 0;
        this.windUpTicks = 80;
        this.lockMovement = LockMovementType.WINDUP;
        this.telegraphType = TelegraphType.LINE;
        this.showTelegraph = true;
        // Default built-in animations
        this.windUpAnimationName = "Ability_Laser_Windup";
        this.activeAnimationName = "Ability_Laser_Active";

        this.setLaserWidth(0.2f);
        this.setInnerColor(0xFFFF00);
        this.setOuterColor(0xFF00FF);
        this.setOuterColorAlpha(1f);
        this.setLightningEffect(true);
        this.setLightningDensity(1.25f);
        this.setLightningRadius(1.25f);
    }
}
