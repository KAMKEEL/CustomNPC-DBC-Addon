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

public class AbilityKiWave extends AbilityEnergyBeam {
    public AbilityKiWave() {
        this.typeId = "ability.npcdbc.ki_wave";
        this.name = "Energy Wave";
        this.targetingMode = TargetingMode.AGGRO_TARGET;
        this.maxRange = 20.0f;
        this.minRange = 5.0f;
        this.cooldownTicks = 0;
        this.windUpTicks = 30;
        this.lockMovement = LockMovementType.WINDUP_AND_ACTIVE;
        this.telegraphType = TelegraphType.CIRCLE;
        this.showTelegraph = false;
        // Default built-in animations
        this.windUpAnimationName = "Ability_Orb_Windup";
        this.activeAnimationName = "Ability_Orb_Active";

        this.setBeamWidth(1.0f);
        this.setHeadSize(1.25f);
        this.setSpeed(1.3f);
        this.setRotationSpeed(30f);
        this.setOuterColorAlpha(1.0f);
        this.setOuterColor(0xFFFF00);
        this.setHoming(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SubGuiAbilityConfig createConfigGui(IAbilityConfigCallback callback) {
        return new SubGuiAbilityEnergyBeam(this, callback);
    }
}
