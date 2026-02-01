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

public class AbilitySupernova extends AbilityOrb {
    public AbilitySupernova() {
        this.typeId = "ability.npcdbc.supernova";
        this.name = "Spirit Bomb";
        this.targetingMode = TargetingMode.AGGRO_TARGET;
        this.maxRange = 100.0f;
        this.minRange = 5.0f;
        this.cooldownTicks = 0;
        this.windUpTicks = 100;
        this.lockMovement = LockMovementType.WINDUP;
        this.telegraphType = TelegraphType.CIRCLE;
        this.showTelegraph = false;
        // Default built-in animation
        this.windUpAnimationName = "Ability_Orb_Windup";
        this.activeAnimationName = "Ability_Orb_Active";


        setAnchorPointEnum(AnchorPoint.ABOVE_HEAD);
        setAnchorOffsetY(12f);
        setHoming(false);
        setMaxDistance(150f);
        setMaxLifetime(300);

        setOrbSpeed(1.0f);
        setOrbSize(8.5f);
        setRotationSpeed(7.5f);

        setInnerColor(0xFFB410);
        setOuterColor(0xE86202);
        setOuterColorAlpha(1.0f);
        setOuterColorWidth(0.1f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SubGuiAbilityConfig createConfigGui(IAbilityConfigCallback callback) {
        return new SubGuiAbilityOrb(this, callback);
    }
}
