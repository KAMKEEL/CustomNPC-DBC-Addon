package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.AnchorPoint;
import kamkeel.npcs.controllers.data.ability.LockMovementType;

public class AbilityLargeSpiritBomb extends AbilitySpiritBomb {
    public AbilityLargeSpiritBomb() {
        this.typeId = "ability.npcdbc.large_spirit_bomb";
        this.name = "Large Spirit Bomb";
        this.maxRange = 100.0f;
        this.windUpTicks = 150;
        this.lockMovement = LockMovementType.WINDUP_AND_ACTIVE;

        setAnchorPointEnum(AnchorPoint.ABOVE_HEAD);
        setAnchorOffsetY(15f);
        setOrbSize(20f);
        setRotationSpeed(7.5f);
    }
}
