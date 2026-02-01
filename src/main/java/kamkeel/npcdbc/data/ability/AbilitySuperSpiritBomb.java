package kamkeel.npcdbc.data.ability;

import kamkeel.npcs.controllers.data.ability.AnchorPoint;
import kamkeel.npcs.controllers.data.ability.LockMovementType;

public class AbilitySuperSpiritBomb extends AbilityGenkiDama {
    public AbilitySuperSpiritBomb() {
        this.typeId = "ability.npcdbc.super_genki_dama";
        this.name = "Super Spirit Bomb";
        this.maxRange = 100.0f;
        this.windUpTicks = 250;
        this.lockMovement = LockMovementType.WINDUP_AND_ACTIVE;

        setAnchorPointEnum(AnchorPoint.ABOVE_HEAD);
        setAnchorOffsetY(30f);
        setOrbSize(20f);
        setRotationSpeed(7.5f);
    }
}
