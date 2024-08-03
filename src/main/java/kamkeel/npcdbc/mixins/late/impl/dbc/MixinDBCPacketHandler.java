package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.p.DBC.DBCPacketHandlerServer;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalByteRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static kamkeel.npcdbc.constants.DBCForm.*;

@Mixin(value = DBCPacketHandlerServer.class, remap = false)
public class MixinDBCPacketHandler {

    @Inject(method = "handleDBCwish", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;DBCgetConfigDeadInv()Z", shift = At.Shift.BEFORE), cancellable = true)
    public void injectReviveEvent(int id, String s, EntityPlayer p, CallbackInfo ci) {
        DBCPlayerEvent.ReviveEvent reviveEvent = new DBCPlayerEvent.ReviveEvent(PlayerDataUtil.getIPlayer(p));
        if (DBCEventHooks.onReviveEvent(reviveEvent))
            ci.cancel();

        DBCData dbcData = DBCData.get(p);

        if (StatusEffectController.Instance.hasEffect(p, Effects.EXHAUSTED))
            return;

        if ((ConfigDBCGameplay.SaiyanZenkai && dbcData.Race == DBCRace.SAIYAN) || (ConfigDBCGameplay.HalfSaiyanZenkai && dbcData.Race == DBCRace.HALFSAIYAN)) {

            if (dbcData.Race == DBCRace.SAIYAN) {
                StatusEffectController.getInstance().applyEffect(p, Effects.ZENKAI, ConfigDBCEffects.ZenkaiSaiyanLength);
            } else {
                StatusEffectController.getInstance().applyEffect(p, Effects.ZENKAI);
            }
        }
    }

    @Inject(method = "handleDBCascend", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;isRaceSaiyan(I)Z", ordinal = 1), cancellable = true)
    public void fixEnergy10xKi(byte dbcascend, EntityPlayer p, CallbackInfo ci, @Local(name = "st") LocalByteRef st, @Local(name = "st2") LocalByteRef st2, @Local(name = "playerAscendNormal") LocalBooleanRef playerAscendNormal, @Local(name = "playerAscendGod") LocalBooleanRef playerAscendGod, @Local(name = "playerAscendBlue") LocalBooleanRef playerAscendBlue, @Local(name = "playerAscendSS4") LocalBooleanRef playerAscendSS4) {
        PlayerDBCInfo dbc = PlayerDataUtil.getDBCInfo(p);
        DBCData data = DBCData.get(p);
        int race = data.Race, selected = dbc.tempSelectedDBCForm;
        if (selected == -1 || st.get() == selected)
            return;
        if (race == DBCRace.HUMAN || race == DBCRace.NAMEKIAN) {
            playerAscendGod.set(false);

            boolean human = race == DBCRace.HUMAN;
            if (selected == (human ? HumanFullRelease : NamekFullRelease)) {
                data.setSetting(1, 0);
                st.set((byte) 0);
                playerAscendNormal.set(true);

            }

            if (selected == (human ? HumanBuffed : NamekGiant)) {
                data.setSetting(1, -1);
                st.set((byte) 0);
                playerAscendNormal.set(false);
            }


            if (selected == (human ? HumanGod : NamekGod)) {
                data.setSetting(1, 1);
                st.set((byte) 0);
                playerAscendGod.set(true);
            }
        } else if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            st.set((byte) 0);
            playerAscendNormal.set(false);
            playerAscendGod.set(false);
            playerAscendBlue.set(false);
            playerAscendSS4.set(false);

            if (selected == SuperSaiyan) {
                data.setSetting(1, -1);
                st.set((byte) 0);
            }

            if (selected == SuperSaiyanG2) {
                data.setSetting(1, -1);
                st.set((byte) 1);
                playerAscendNormal.set(true);
            }

            if (selected == SuperSaiyanG3) {
                data.setSetting(1, -1);
                st.set((byte) 2);
                playerAscendNormal.set(true);
            }

            if (selected == MasteredSuperSaiyan) {
                data.setSetting(1, 9);
                st.set((byte) 0);
            }

            if (selected == SuperSaiyan2) {
                data.setSetting(1, 0);
                st.set((byte) 4);
                playerAscendNormal.set(true);
            }

            if (selected == SuperSaiyan3) {
                data.setSetting(1, 0);
                st.set((byte) 5);
                playerAscendNormal.set(true);
            }

            if (selected == SuperSaiyanGod) {
                data.setSetting(1, 1);
                st.set((byte) 0);
                playerAscendGod.set(true);
            }

            if (selected == SuperSaiyanBlue) {
                data.setSetting(1, 2);
                st.set((byte) SuperSaiyanGod);
                playerAscendBlue.set(true);
            }

            if (selected == BlueEvo) {
                data.setSetting(1, 2);
                st.set((byte) SuperSaiyanBlue);
                playerAscendBlue.set(true);
            }

            if (selected == SuperSaiyan4) {
                data.setSetting(1, 3);
                st.set((byte) 0);
                playerAscendSS4.set(true);
            }

        } else if (race == DBCRace.NAMEKIAN) {

        } else if (race == DBCRace.ARCOSIAN) {
            if (selected == SuperSaiyanGod) {
                st.set((byte) 0);
                playerAscendGod.set(true);
            }
        } else if (race == DBCRace.MAJIN) {
            if (selected == SuperSaiyanGod) {
                st.set((byte) 0);
                playerAscendGod.set(true);
            }

        }
        dbc.tempSelectedDBCForm = -1;


    }

    @Inject(method = "handleDBCenergy", at = @At("HEAD"), cancellable = true)
    public void fixEnergy10xKi(byte b, byte p, EntityPlayer pl, CallbackInfo ci) {
        CommonProxy.CurrentJRMCTickPlayer = pl;
    }

    @Inject(method = "handleDBCenergy", at = @At("TAIL"), cancellable = true)
    public void fixEnergy10xKi2(byte b, byte p, EntityPlayer pl, CallbackInfo ci) {
        CommonProxy.CurrentJRMCTickPlayer = null;
    }
}
