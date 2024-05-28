package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.p.DBC.DBCPacketHandlerServer;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DBCPacketHandlerServer.class, remap = false)
public class MixinDBCPacketHandler {

    @Inject(method = "handleDBCwish", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;DBCgetConfigDeadInv()Z", shift = At.Shift.BEFORE), cancellable = true)
    public void injectReviveEvent(int id, String s, EntityPlayer p, CallbackInfo ci) {
        DBCPlayerEvent.ReviveEvent reviveEvent = new DBCPlayerEvent.ReviveEvent(PlayerDataUtil.getIPlayer(p));
        if (DBCEventHooks.onReviveEvent(reviveEvent))
            ci.cancel();

        DBCData dbcData = DBCData.get(p);
        if((ConfigDBCGameplay.SaiyanZenkai && dbcData.Race == DBCRace.SAIYAN ) ||
            (ConfigDBCGameplay.HalfSaiyanZenkai && dbcData.Race == DBCRace.HALFSAIYAN)){

            if(dbcData.Race == DBCRace.SAIYAN){
                StatusEffectController.getInstance().applyEffect(p, Effects.ZENKAI, ConfigDBCEffects.ZenkaiSaiyanLength);
            } else {
                StatusEffectController.getInstance().applyEffect(p, Effects.ZENKAI);
            }
        }
    }
}
