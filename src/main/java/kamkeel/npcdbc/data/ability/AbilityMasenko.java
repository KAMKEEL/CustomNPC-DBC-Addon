package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityEnergyBeam;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;

public class AbilityMasenko extends AbilityEnergyBeam {
    public AbilityMasenko() {
        this.typeId = "ability.npcdbc.masenko";
        this.name = "Masenko";
        this.targetingMode = TargetingMode.AGGRO_TARGET;
        this.maxRange = 20.0f;
        this.minRange = 5.0f;
        this.cooldownTicks = 0;
        this.windUpTicks = 40;
        this.lockMovement = LockMovementType.WINDUP_AND_ACTIVE;
        this.telegraphType = TelegraphType.CIRCLE;
        this.showTelegraph = false;
        // Default built-in animations
        this.windUpAnimationName = "Ability_Beam_Windup";
        this.activeAnimationName = "Ability_Beam_Active";

        this.setBeamWidth(1.75f);
        this.setHeadSize(2.25f);
        this.setSpeed(1.5f);
        this.setRotationSpeed(30f);
        this.setOuterColorAlpha(1.0f);
        this.setOuterColor(0xFFFF00);
        this.setHoming(false);
    }
}
