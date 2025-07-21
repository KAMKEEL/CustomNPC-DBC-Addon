package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCorePacHanS;
import JinRyuu.JRMCore.p.BAmh;
import JinRyuu.JRMCore.p.DBC.DBCPacketHandlerServer;
import JinRyuu.JRMCore.p.DBC.DBCPenergy;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.dbcdata.DBCDataKiAttackValidator;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DBCPenergy.Handler.class)
public abstract class MixinDBCPEnergyHandler extends BAmh<DBCPenergy> {

    @Redirect(method = "handleServerMessage(Lnet/minecraft/entity/player/EntityPlayer;LJinRyuu/JRMCore/p/DBC/DBCPenergy;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/p/DBC/DBCPacketHandlerServer;handleDBCenergy(BBLnet/minecraft/entity/player/EntityPlayer;)V"))
    private void snoopKiAttackCastState(DBCPacketHandlerServer instance, byte kiSelection, byte chargePercent, EntityPlayer p) {
        boolean isCastableAttack = kiSelection >= 0 && kiSelection < 8;

        boolean allowToPassThrough = true;

        if (isCastableAttack) {
            DBCDataKiAttackValidator validator = DBCData.get(p).kiAttackValidator;
            allowToPassThrough = validator.isCastTimeValid(kiSelection, chargePercent, p.worldObj.getTotalWorldTime());
        }

        if (allowToPassThrough) {
            instance.handleDBCenergy(kiSelection, chargePercent, p);
        }


    }
}
