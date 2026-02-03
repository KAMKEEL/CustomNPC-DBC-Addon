package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityOrb;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;

public class AbilityBigBangAttack extends AbilityOrb {
    public AbilityBigBangAttack() {
        this.typeId = "ability.npcdbc.bigbang_attack";
        this.name = "Big Bang Attack";
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
        setOuterColor(0x48BBF6);
        setOrbSpeed(1.0f);
        setOrbSize(3f);
        setRotationSpeed(30f);
        setOuterColorAlpha(1.0f);
        setOuterColorWidth(0.1f);
    }
}
