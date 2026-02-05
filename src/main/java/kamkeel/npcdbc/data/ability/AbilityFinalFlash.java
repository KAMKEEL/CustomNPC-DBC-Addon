package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityBeamDual;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;

public class AbilityFinalFlash extends AbilityBeamDual {
    public AbilityFinalFlash() {
        this.typeId = "ability.npcdbc.final_flash";
        this.name = "Final Flash";
        this.targetingMode = TargetingMode.AGGRO_TARGET;
        this.maxRange = 20.0f;
        this.minRange = 5.0f;
        this.cooldownTicks = 0;
        this.windUpTicks = 80;
        this.lockMovement = LockMovementType.WINDUP_AND_ACTIVE;
        this.telegraphType = TelegraphType.CIRCLE;
        this.showTelegraph = false;
        // Default built-in animations
        this.windUpAnimationName = "Ability_BeamDual_Windup";
        this.activeAnimationName = "Ability_BeamDual_Active";

        this.setBeamWidth(3.0f);
        this.setHeadSize(3.25f);
        this.setSpeed(1.5f);

        this.setRotationSpeed(0, 30f);
        this.setRotationSpeed(1, 30f);
        this.setOuterColorAlpha(0, 1.0f);
        this.setOuterColorAlpha(1, 1.0f);
        this.setOuterColor(0, 0xFFFF00);
        this.setOuterColor(1, 0xFFFF00);

        this.setLightningEffect(0, true);
        this.setLightningEffect(1, true);
        this.setLightningDensity(0, 2f);
        this.setLightningDensity(1, 2f);
        this.setLightningRadius(0, 3f);
        this.setLightningRadius(1, 3f);

        this.setDualFire(false);
        this.setHoming(false);
    }
}
