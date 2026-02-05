package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityOrb;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;

public class AbilitySpiritBomb extends AbilityOrb {
    public AbilitySpiritBomb() {
        this.typeId = "ability.npcdbc.spirit_bomb";
        this.name = "Spirit Bomb";
        this.targetingMode = TargetingMode.AGGRO_TARGET;
        this.maxRange = 25.0f;
        this.minRange = 5.0f;
        this.cooldownTicks = 0;
        this.windUpTicks = 100;
        this.lockMovement = LockMovementType.WINDUP;
        this.telegraphType = TelegraphType.CIRCLE;
        this.showTelegraph = false;
        // Default built-in animation
        this.windUpAnimationName = "Ability_Orb_Windup";
        this.activeAnimationName = "Ability_Orb_Active";

        setHoming(true);
        setHomingStrength(0.05f);
        setHomingRange(150f);
        setMaxDistance(150f);
        setMaxLifetime(300);
        setOuterColor(0x48BBF6);
        setOrbSpeed(1.0f);
        setOrbSize(1.5f);
        setRotationSpeed(10f);
        setOuterColorAlpha(1.0f);
        setOuterColorWidth(0.5f);
    }
}
