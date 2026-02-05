package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityBeam;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;

public class AbilityKiWave extends AbilityBeam {
    public AbilityKiWave() {
        this.typeId = "ability.npcdbc.ki_wave";
        this.name = "Energy Wave";
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
        setRotationSpeed(30f);
        setOuterColorAlpha(1.0f);
        setOuterColor(0xFFFF00);
        setHoming(false);
    }
}
