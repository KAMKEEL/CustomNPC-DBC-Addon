package kamkeel.npcdbc.data.ability;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityDualDisc;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;
import noppes.npcs.client.gui.advanced.SubGuiAbilityConfig;
import noppes.npcs.client.gui.advanced.ability.SubGuiAbilityDualDisc;
import noppes.npcs.client.gui.util.IAbilityConfigCallback;

public class AbilityDeathSaucer extends AbilityDualDisc {
    public AbilityDeathSaucer() {
        this.typeId = "ability.npcdbc.death_saucer";
        this.name = "Death Saucer";
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

        setSpeed(1.6f);
        setDiscRadius(1.5f);
        setMaxDistance(70);
        setLockMovement(LockMovementType.WINDUP_AND_ACTIVE);

        setHoming(true);
        setHomingRange(60f);
        setHomingStrength(0.2f);

        setOuterColor(0, 0xDE5CBA);
        setOuterColor(1, 0xDE5CBA);
        setOuterColorAlpha(0, 1f);
        setOuterColorAlpha(1, 1f);
        setOuterColorWidth(0, 0.85f);
        setOuterColorWidth(1, 0.85f);

        setInnerColor(0, 0xB3000A);
        setInnerColor(1, 0xB3000A);
        setRotationSpeed(0, 40f);
        setRotationSpeed(1, 40f);

        setAnchorOffsetY(0, 0.3f);
        setAnchorOffsetY(1, 0.3f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SubGuiAbilityConfig createConfigGui(IAbilityConfigCallback callback) {
        return new SubGuiAbilityDualDisc(this, callback);
    }
}
