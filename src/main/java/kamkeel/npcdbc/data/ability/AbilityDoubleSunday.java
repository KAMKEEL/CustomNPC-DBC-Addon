package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityBeamDual;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;

public class AbilityDoubleSunday extends AbilityBeamDual {
    public AbilityDoubleSunday() {
        this.typeId = "ability.npcdbc.double_sunday";
        this.name = "Double Sunday";
        this.targetingMode = TargetingMode.AGGRO_TARGET;
        this.maxRange = 20.0f;
        this.minRange = 5.0f;
        this.cooldownTicks = 0;
        this.windUpTicks = 30;
        this.lockMovement = LockMovementType.WINDUP_AND_ACTIVE;
        this.telegraphType = TelegraphType.CIRCLE;
        this.showTelegraph = false;
        // Default built-in animations
        this.windUpAnimationName = "Ability_Orb_Windup";
        this.activeAnimationName = "Ability_Orb_Active";

        setBeamWidth(1.0f);
        setHeadSize(1.25f);
        setSpeed(1.3f);

        setRotationSpeed(0, 30f);
        setRotationSpeed(1, 30f);

        setOuterColorAlpha(0, 0.5f);
        setOuterColorAlpha(1, 0.5f);

        setOuterColor(0, 0xFFBBFF);
        setOuterColor(1, 0xFFBBFF);

        setDualFire(true);
        setHoming(false);
    }
}
