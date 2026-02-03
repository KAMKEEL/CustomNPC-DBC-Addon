package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityOrb;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;

public class AbilityBurningAttack extends AbilityOrb {
    public AbilityBurningAttack() {
        this.typeId = "ability.npcdbc.burning_attack";
        this.name = "Burning Attack";
        this.targetingMode = TargetingMode.AGGRO_TARGET;
        this.maxRange = 25.0f;
        this.minRange = 5.0f;
        this.cooldownTicks = 0;
        this.windUpTicks = 50;
        this.lockMovement = LockMovementType.WINDUP;
        this.telegraphType = TelegraphType.CIRCLE;
        this.showTelegraph = false;
        // Default built-in animation
        this.windUpAnimationName = "Ability_Orb_Windup";
        this.activeAnimationName = "Ability_Orb_Active";

        setHoming(false);
        setInnerColor(0xFFEE00);
        setOuterColor(0xFFEE00);
        setOrbSpeed(1.0f);
        setOrbSize(3.5f);
        setOuterColorEnabled(false);
        setRotationSpeed(30f);
    }
}
