package kamkeel.npcdbc.data.ability;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcs.controllers.data.ability.AnchorPoint;
import kamkeel.npcs.controllers.data.ability.LockMovementType;
import noppes.npcs.client.gui.advanced.SubGuiAbilityConfig;
import noppes.npcs.client.gui.advanced.ability.SubGuiAbilityOrb;
import noppes.npcs.client.gui.util.IAbilityConfigCallback;

public class AbilityLargeGenkiDama extends AbilityGenkiDama{
    public AbilityLargeGenkiDama() {
        this.typeId = "ability.npcdbc.large_genki_dama";
        this.name = "Large Spirit Bomb";
        this.maxRange = 100.0f;
        this.windUpTicks = 150;
        this.lockMovement = LockMovementType.WINDUP_AND_ACTIVE;

        setAnchorPointEnum(AnchorPoint.ABOVE_HEAD);
        setAnchorOffsetY(15f);
        setOrbSize(20f);
        setRotationSpeed(7.5f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public SubGuiAbilityConfig createConfigGui(IAbilityConfigCallback callback) {
        return new SubGuiAbilityOrb(this, callback);
    }
}
