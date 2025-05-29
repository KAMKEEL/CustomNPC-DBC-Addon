package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCorePacHanS;
import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = JRMCorePacHanS.class, remap = false)
public class MixinJRMCPacketHandlerServer {

    @Redirect(method = "handleStats3", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;canAffordSkill(II)Z"))
    private boolean addAddonMindBonus(int mindAttribute, int haveSkillLvls, @Local(ordinal = 0, argsOnly = true) EntityPlayer player) {
        return JRMCoreH.canAffordSkill(mindAttribute + DBCData.get(player).calculateMindBonuses(), haveSkillLvls);
    }
}
