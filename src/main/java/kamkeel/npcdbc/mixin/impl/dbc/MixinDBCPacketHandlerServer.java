package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.p.DBC.DBCPacketHandlerServer;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DBCPacketHandlerServer.class, remap = false)
public class MixinDBCPacketHandlerServer {
    @Inject(method = "handleDBCenergy", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/server/config/dbc/JGConfigDBCFormMastery;FM_Enabled:Z", shift = At.Shift.BEFORE), remap = false)
    public void onFMkiFireGain(byte b, byte p, EntityPlayer pl, CallbackInfo ci) {
        PlayerCustomFormData c = Utility.getFormData(pl);
        c.updateCurrentFormMastery("fireki");
    }
}
