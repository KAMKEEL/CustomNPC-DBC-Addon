package kamkeel.npcdbc.data.ability;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcs.controllers.data.ability.AnchorPoint;
import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityOrb;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;
import noppes.npcs.client.gui.advanced.SubGuiAbilityConfig;
import noppes.npcs.client.gui.advanced.ability.SubGuiAbilityOrb;
import noppes.npcs.client.gui.util.IAbilityConfigCallback;

public class AbilityDeathBall extends AbilityOrb {
    public AbilityDeathBall() {
        this.typeId = "ability.npcdbc.death_ball";
        this.name = "Death Ball";
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
        setOrbSpeed(1.0f);
        setOrbSize(1.0f);
        setRotationSpeed(30f);
        setInnerColor(0x30004B);
        setOuterColor(0xE600F6);
        setOuterColorAlpha(1.0f);
        setOuterColorWidth(0.05f);
        setLightningEffect(true);
        setLightningDensity(1.5f);
        setLightningRadius(2.5f);
        setAnchorPointEnum(AnchorPoint.ABOVE_HEAD);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SubGuiAbilityConfig createConfigGui(IAbilityConfigCallback callback) {
        return new SubGuiAbilityOrb(this, callback);
    }
}
