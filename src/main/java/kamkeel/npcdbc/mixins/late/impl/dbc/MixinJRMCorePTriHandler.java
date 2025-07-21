package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCorePacHanS;
import JinRyuu.JRMCore.p.BAmh;
import JinRyuu.JRMCore.p.JRMCorePTri;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.dbcdata.DBCDataKiAttackValidator;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = JRMCorePTri.Handler.class, remap = false)
public abstract class MixinJRMCorePTriHandler extends BAmh<JRMCorePTri> {


    @Redirect(method = "handleServerMessage(Lnet/minecraft/entity/player/EntityPlayer;LJinRyuu/JRMCore/p/JRMCorePTri;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCorePacHanS;handleTri(BBBLnet/minecraft/entity/player/EntityPlayer;)V", remap = false), remap = false)
    private void snoopKiAttackCastState(JRMCorePacHanS instance, byte dataType, byte kiAttackType, byte kiAttackSlot, EntityPlayer p) {
        if (dataType == 3 && kiAttackSlot < 8 && kiAttackSlot >= 0) {
            DBCData data = DBCData.get(p);
            DBCDataKiAttackValidator validator = data.kiAttackValidator;

            if (kiAttackType == 0) {
                validator.cancelCast(kiAttackSlot);
            } else if (kiAttackType > 0) {
                validator.startCast(kiAttackSlot, p.worldObj.getTotalWorldTime());
            }
        }

        instance.handleTri(dataType, kiAttackType, kiAttackSlot, p);
    }
}
