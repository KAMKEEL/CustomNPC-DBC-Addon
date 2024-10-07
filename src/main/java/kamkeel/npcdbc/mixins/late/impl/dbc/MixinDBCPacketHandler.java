package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.DragonBC.common.DBCConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import JinRyuu.JRMCore.p.DBC.DBCPacketHandlerServer;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCGoD;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalByteRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
    public void fixEnergy10xKi(byte dbcascend, EntityPlayer p, CallbackInfo ci, @Local(name = "isInBaseForm") LocalBooleanRef isInBaseForm, @Local(name = "StE") LocalRef<String> StE, @Local(name = "st") LocalByteRef st, @Local(name = "statusKaiokenOn") LocalBooleanRef statusKaiokenOn, @Local(name = "statusMysticOn") LocalBooleanRef statusMysticOn, @Local(name = "statusUltraInstinctOn") LocalBooleanRef statusUltraInstinctOn, @Local(name = "statusGodOfDestructionOn") LocalBooleanRef statusGodOfDestructionOn, @Local(name = "st2") LocalByteRef st2, @Local(name = "isKaiokenAvailable") LocalBooleanRef isKaiokenAvailable, @Local(name = "isUIAvailable") LocalBooleanRef isUIAvailable, @Local(name = "isGoDAvailable") LocalBooleanRef isGoDAvailable, @Local(name = "isMysticAvailable") LocalBooleanRef isMysticAvailable, @Local(name = "playerAscendNormal") LocalBooleanRef playerAscendNormal, @Local(name = "playerAscendGod") LocalBooleanRef playerAscendGod, @Local(name = "playerAscendBlue") LocalBooleanRef playerAscendBlue, @Local(name = "playerAscendSS4") LocalBooleanRef playerAscendSS4) {
        PlayerDBCInfo dbc = PlayerDataUtil.getDBCInfo(p);
        DBCData data = DBCData.get(p);

        boolean update = false;
        int race = data.Race, selected = dbc.selectedDBCForm;
        if (selected != -1 && st.get() != selected) {
            //  data.setForm(GodOfDestruction, true);
            NBTTagCompound nbt = JRMCoreH.nbt(p);
            String stus = StE.get();

            if (selected == Mystic) {
                st.set((byte) (race == 4 ? 4 : 0));
                st2.set((byte) 0);
                statusKaiokenOn.set(true);
                isMysticAvailable.set(true);
                stus = data.setForm(GodOfDestruction, false);
                if (!DBCConfig.MysticKaiokenOn) {
                    data.setForm(Kaioken, false);
                }
                if (statusUltraInstinctOn.get())
                    stus = data.setForm(UltraInstinct, false);
                else
                    stus = JRMCoreH.StusEfcts(19, stus, nbt, false);
                statusUltraInstinctOn.set(false);
            } else if (selected >= Kaioken && selected <= Kaioken6) {
                int chosen = (selected - Kaioken + 1);
                isKaiokenAvailable.set(true);
                st2.set((byte) (chosen - 1));
                if (!DBCConfig.MysticKaiokenOn)
                    data.setForm(Mystic, false);
                stus = data.setForm(GodOfDestruction, false);
                stus = JRMCoreH.StusEfcts(19, stus, nbt, false);
            } else if (selected >= UltraInstinct && selected <= UltraInstinct + 10) {
                isInBaseForm.set(true);
                int chosen = (selected - UltraInstinct + 1);
                isUIAvailable.set(true);
                statusMysticOn.set(false);
                st.set((byte) (race == 4 ? 4 : 0));
                st2.set((byte) (chosen - 1));
                data.setForm(Mystic, false);
                stus = data.setForm(GodOfDestruction, false);
                stus = JRMCoreH.StusEfcts(4, stus, nbt, false);
            } else if (selected == GodOfDestruction) {
                isInBaseForm.set(true);
                st.set((byte) (race == 4 ? 4 : 0));
                st2.set((byte) 0);
                statusUltraInstinctOn.set(false);
                isGoDAvailable.set(true);
                data.setForm(Kaioken, false);
                data.setForm(UltraInstinct, false);
                stus = data.setForm(Mystic, false);

            } else {
                if (statusMysticOn.get()) {
                    statusMysticOn.set(false);
                    data.setForm(Mystic, false);

                }
                if (statusUltraInstinctOn.get()) {
                    statusUltraInstinctOn.set(false);
                    data.setForm(UltraInstinct, false);

                }

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
                    playerAscendNormal.set(false);
                    playerAscendGod.set(false);

                    if (selected == FirstForm) {
                        st.set((byte) 0);
                    }

                    if (selected == SecondForm) {
                        st.set((byte) 1);
                    }

                    if (selected == ThirdForm) {
                        st.set((byte) 2);
                    }

                    if (selected == FinalForm) {
                        st.set((byte) 3);
                    }

                    if (selected == SuperForm) {
                        data.setSetting(1, -1);
                        st.set((byte) 4);
                    }

                    if (selected == UltimateForm) {
                        data.setSetting(1, 0);
                        st.set((byte) 4);
                        playerAscendNormal.set(true);
                    }

                    if (selected == ArcoGod) {
                        data.setSetting(1, 1);
                        st.set((byte) 4);
                        playerAscendGod.set(true);
                    }

                } else if (race == DBCRace.MAJIN) {
                    playerAscendGod.set(false);
                    playerAscendNormal.set(false);

                    if (selected == MajinEvil) {
                        data.setSetting(1, -1);
                        st.set((byte) 0);

                    }
                    if (selected == MajinFullPower) {
                        data.setSetting(1, -1);
                        st.set((byte) 1);

                    }

                    if (selected == MajinPure) {
                        data.setSetting(1, 0);
                        st.set((byte) 0);
                        playerAscendNormal.set(true);
                    }


                    if (selected == MajinGod) {
                        data.setSetting(1, 1);
                        st.set((byte) 0);
                        playerAscendGod.set(true);
                    }


                }
            }
            dbc.selectedDBCForm = -1;
            update = true;
            StE.set(stus);
        }

        Form form = data.getForm();
        if (form != null) {
            if (!form.stackable.vanillaStackable && !isGoDAvailable.get() && !isMysticAvailable.get() && !isUIAvailable.get() && !isKaiokenAvailable.get()) {
                dbc.currentForm = -1;
                update = true;
            }
        }
        if (update)
            dbc.updateClient();

    }

    @Inject(method = "handleDBCenergy", at = @At("HEAD"), cancellable = true)
    public void fixEnergy10xKi(byte b, byte p, EntityPlayer pl, CallbackInfo ci) {
        CommonProxy.CurrentJRMCTickPlayer = pl;
    }

    @Inject(method = "handleDBCenergy", at = @At("TAIL"), cancellable = true)
    public void fixEnergy10xKi2(byte b, byte p, EntityPlayer pl, CallbackInfo ci) {
        CommonProxy.CurrentJRMCTickPlayer = null;
    }

    @Inject(method = "handleDBCascend", at = @At("HEAD"), cancellable = true)
    public void setCurrentPlayer(byte dbcascend, EntityPlayer p, CallbackInfo ci) {
        CommonProxy.CurrentJRMCTickPlayer = p;
    }

    @Inject(method = "handleDBCascend", at = @At("TAIL"), cancellable = true)
    public void setCurrentPlayerPOST(byte dbcascend, EntityPlayer p, CallbackInfo ci) {
        CommonProxy.CurrentJRMCTickPlayer = null;
    }

    @Redirect(method = "handleDBCenergy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z", ordinal = 2, remap = true), remap = false)
    public boolean addDestroyerConfigsToAttack(World instance, Entity entity){
        EntityEnergyAtt kiAttack = (EntityEnergyAtt) entity;
        DBCData dbcData = DBCData.get(CommonProxy.CurrentJRMCTickPlayer);
        Form form = dbcData.getForm();

        if(form != null && form.mastery.destroyerEnabled && JGConfigDBCGoD.CONFIG_GOD_ENERGY_ENABLED && JGConfigDBCGoD.CONFIG_GOD_ENABLED){
            kiAttack.destroyer = true;
            kiAttack.DAMAGE_REDUCTION = form.mastery.calculateMulti("destroyerKiDamage", dbcData.addonFormLevel);
        }

        return instance.spawnEntityInWorld(kiAttack);
    }

    @Redirect(method = "handleDBCascend", at = @At(value = "INVOKE", target="LJinRyuu/JRMCore/JRMCoreH;setInt(ILnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V", ordinal = 5))
    public void adjustKaiokenStrain(int s, EntityPlayer Player, String string, @Local(name = "strainMulti") float strainMulti, @Local(name = "kaiokenSkillLevel") int kaiokenSkillLevel){
        DBCData dbcData = DBCData.get(Player);
        if(dbcData != null && dbcData.getForm() != null){
            Form form = dbcData.getForm();
            if(form.stackable.kaiokenStackable){
                int oldStrain = JRMCoreH.getInt(Player, string);
                int strain = (int) ((12 + 20 - kaiokenSkillLevel) * strainMulti);
                s = strain+oldStrain;
            }
        }

        JRMCoreH.setInt(s, Player, string);
    }

    @Redirect(method = "handleDBCdescend", at = @At(value = "INVOKE", target="Lnet/minecraft/nbt/NBTTagCompound;setByte(Ljava/lang/String;B)V", remap = true), remap = false)
    public void DBCTransformEventDescend(NBTTagCompound instance, String key, byte value, @Local(argsOnly = true) EntityPlayer player) {
        if (key.equals("jrmcState")) {
            int oldForm = DBCData.get(player).State;

            if (DBCEventHooks.onFormChangeEvent(new DBCPlayerEvent.FormChangeEvent(PlayerDataUtil.getIPlayer(player), false, oldForm, false, value)))
                return;
        }

        instance.setByte(key, value);
    }
}
