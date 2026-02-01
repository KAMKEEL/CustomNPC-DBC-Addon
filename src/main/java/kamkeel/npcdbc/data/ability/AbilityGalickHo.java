package kamkeel.npcdbc.data.ability;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.TargetingMode;
import kamkeel.npcs.controllers.data.ability.type.AbilityEnergyBeam;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;
import noppes.npcs.client.gui.advanced.SubGuiAbilityConfig;
import noppes.npcs.client.gui.advanced.ability.SubGuiAbilityEnergyBeam;
import noppes.npcs.client.gui.util.IAbilityConfigCallback;

public class AbilityGalickHo extends AbilityEnergyBeam {
    public AbilityGalickHo() {
        this.typeId = "ability.npcdbc.galick_ho";
        this.name = "Galick Gun";
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

        this.setBeamWidth(1.5f);
        this.setHeadSize(2.0f);
        this.setSpeed(1.5f);
        this.setRotationSpeed(30f);
        this.setInnerColor(0xDBC2EC);
        this.setOuterColorAlpha(1.0f);
        this.setOuterColor(0xC580E5);
        this.setHoming(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SubGuiAbilityConfig createConfigGui(IAbilityConfigCallback callback) {
        return new SubGuiAbilityEnergyBeam(this, callback);
    }
}
