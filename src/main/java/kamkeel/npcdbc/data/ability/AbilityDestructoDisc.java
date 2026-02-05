package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityDisc;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;

public class AbilityDestructoDisc extends AbilityDisc {
    public AbilityDestructoDisc() {
        this.typeId = "ability.npcdbc.destructo_disc";
        this.name = "Destructo Disc";
        this.targetingMode = TargetingMode.AGGRO_TARGET;
        this.maxRange = 30.0f;
        this.minRange = 5.0f;
        this.cooldownTicks = 0;
        this.windUpTicks = 60;
        this.lockMovement = LockMovementType.WINDUP;
        this.telegraphType = TelegraphType.CIRCLE;
        this.showTelegraph = false;
        // Default built-in animations
        this.windUpAnimationName = "Ability_Disc_Windup";
        this.activeAnimationName = "Ability_Disc_Active";

        setSpeed(1.5f);
        setOuterColor(0xFFDD00);
        setInnerColor(0xFFFF00);
        setRotationSpeed(30f);
        setMaxDistance(35);
        setBoomerang(false);
        setHoming(false);
        setOuterColorWidth(0.1f);
        setDiscRadius(1.5f);
        setLockMovement(LockMovementType.WINDUP_AND_ACTIVE);
        setAnchorOffsetY(0.25f);
    }
}
