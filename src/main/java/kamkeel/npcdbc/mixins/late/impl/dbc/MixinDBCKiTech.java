package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.DragonBC.common.DBCKiTech;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreKeyHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumNBTType;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixins.late.IEntityAura;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.DBCSetValPacket;
import kamkeel.npcdbc.network.packets.player.TransformPacket;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static kamkeel.npcdbc.constants.DBCForm.UltraInstinct;

@Mixin(value = DBCKiTech.class, remap = false)
public abstract class MixinDBCKiTech {

    @Shadow
    public static void mv(float strafe, float frward, EntityPlayer var4, float add) {

    }

    @Shadow
    public static void setThrowableHeading(Entity e, double par1, double par3, double par5, float par7, float par8) {

    }

    @Redirect(method = "Ascend", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;rotationPitch:F", remap = true, ordinal = 0))
    private static float disableOozaruTransformInCustomForm(EntityClientPlayerMP instance) {
        Form form = DBCData.getForm(instance);
        if (form == null)
            return instance.rotationPitch;

        return form.stackable.vanillaStackable ? instance.rotationPitch : 0;
    }

    @Inject(method = "ChargeKi", at = @At(value = "FIELD", target = "LJinRyuu/DragonBC/common/DBCKiTech;time:I", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private static void ChargeKi(CallbackInfo ci) {
        boolean FnPressed = JRMCoreKeyHandler.Fn.getIsKeyPressed();
        if (DBCData.getClient().isFnPressed != FnPressed) {
            DBCData.getClient().isFnPressed = FnPressed;
            DBCPacketHandler.Instance.sendToServer(new DBCSetValPacket(DBCData.getClient().player, EnumNBTType.BOOLEAN, "DBCIsFnPressed", FnPressed));

        }

        if (ClientCache.kiRevamp)
            ci.cancel();
    }
//
//    @ModifyArgs(method = "DashKi", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/DBCKiTech;mv(FFLnet/minecraft/entity/player/EntityPlayer;F)V"))
//    private static void changeSprintSpeed(Args args) {
//        float speed = args.get(3);
//        args.set(3, speed * DBCData.getClient().getSprintSpeed());
//    }

    @Redirect(method = "DashKi", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/DBCKiTech;mv(FFLnet/minecraft/entity/player/EntityPlayer;F)V"))
    private static void changeSprintSpeed(float f4, float f5, EntityPlayer pitch, float speedY) {
        speedY *= DBCData.getClient().getSprintSpeed();
        mv(f4, f5, pitch, speedY);
    }

    //    @ModifyArgs(method = "FloatKi", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/DBCKiTech;mv(FFLnet/minecraft/entity/player/EntityPlayer;F)V"))
//    private static void changeBaseSpeed(Args args) {
//        float speed = args.get(3);
//        args.set(3, speed * DBCData.getClient().getBaseFlightSpeed() * DBCData.getClient().flightSpeedRelease / 100);
//
//    }
    @Redirect(method = "FloatKi", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/DBCKiTech;mv(FFLnet/minecraft/entity/player/EntityPlayer;F)V"))
    private static void changeBaseSpeed(float f4, float f5, EntityPlayer pitch, float speedY) {
        speedY *= DBCData.getClient().getBaseFlightSpeed() * DBCData.getClient().flightSpeedRelease / 100f;
        mv(f4, f5, pitch, speedY);
    }

//    @ModifyArgs(method = "FloatKi", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/DBCKiTech;setThrowableHeading(Lnet/minecraft/entity/Entity;DDDFF)V"))
//    private static void changeDynamic(Args args) {
//        float speed = args.get(4);
//        args.set(4, speed * DBCData.getClient().getDynamicFlightSpeed() * DBCData.getClient().flightSpeedRelease / 100);
//
//    }

    @Redirect(method = "FloatKi", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/DBCKiTech;setThrowableHeading(Lnet/minecraft/entity/Entity;DDDFF)V"))
    private static void changeDynamic(Entity e, double par1, double par3, double par5, float par7, float par8) {
        par7 *= DBCData.getClient().getDynamicFlightSpeed() * DBCData.getClient().flightSpeedRelease / 100f;
        setThrowableHeading(e, par1, par3, par5, par7, par8);
    }

    @Inject(method = "FloatKi", at = @At(value = "FIELD", target = "LJinRyuu/DragonBC/common/DBCKiTech;floating:Z", ordinal = 7, shift = At.Shift.AFTER))
    private static void isFlying(KeyBinding kiFlight, KeyBinding keyBindJump, KeyBinding keyBindSneak, CallbackInfo ci) {

        if (DBCData.getClient().isFlying != DBCKiTech.floating) {
            DBCData.getClient().isFlying = DBCKiTech.floating;
            DBCPacketHandler.Instance.sendToServer(new DBCSetValPacket(DBCData.getClient().player, EnumNBTType.BOOLEAN, "DBCisFlying", DBCKiTech.floating));
        }


    }

    @Redirect(method = "FloatKi", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;PlayerFlyingDragDownOn:Z"))
    private static boolean flightGravity() {
        if (!DBCData.getClient().flightGravity)
            return false;

        return JRMCoreConfig.PlayerFlyingDragDownOn;
    }

    @Inject(method = "FloatKi", at = @At("HEAD"), cancellable = true)
    private static void disableFlight(KeyBinding kiFlight, KeyBinding keyBindJump, KeyBinding keyBindSneak, CallbackInfo ci) {
        if (!DBCData.getClient().flightEnabled)
            ci.cancel();

    }


    @Inject(method = "chargePart(Z)V", at = @At("HEAD"), cancellable = true)
    private static void cancelAura(boolean b, CallbackInfo ci) {
//        if (ConfigDBCClient.RevampAura)
//            ci.cancel();

    }

    @Inject(method = "chargePart(Lnet/minecraft/entity/player/EntityPlayer;IIIIIZLjava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private static void cancelAura2(EntityPlayer p, int r, int a, int c, int s, int k, boolean b, String se, CallbackInfo ci) {
        DBCData dbcData = DBCData.get(p);
        Aura aura = dbcData.getAura();
        if (dbcData.isFusionSpectator()) {
            ci.cancel();
            return;
        }
        if (aura != null) {
            if (aura.display.overrideDBCAura)
                ci.cancel();
            else if (dbcData.isForm(DBCForm.Base) || dbcData.isForm(DBCForm.Kaioken) && aura.display.hasKaiokenAura)
                ci.cancel();
        } else if (ConfigDBCClient.RevampAura) {
            EntityAura enhancedAura = dbcData.auraEntity;

            if (enhancedAura == null)
                enhancedAura = new EntityAura(p, new Aura().display.setOverrideDBCAura(true)).setIsVanilla(true).load(true).spawn();

            if (enhancedAura != null)
                ci.cancel();
        }


    }

    @Inject(method = "Ascend", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;isRaceMajin()Z", ordinal = 0), cancellable = true)
    private static void fixSomeFormsNotAscendingProperly4(KeyBinding K, CallbackInfo ci, @Local(name = "isInBase") LocalBooleanRef isInBase, @Local(name = "isInKaioken") LocalBooleanRef isInKaioken, @Local(name = "isInMystic") LocalBooleanRef isInMystic, @Local(name = "isInGoD") LocalBooleanRef isInGoD, @Local(name = "isInUI") LocalBooleanRef isInUI, @Local(name = "useGodOfDestruction") LocalBooleanRef useGodOfDestruction, @Local(name = "isInUltraInstinct") LocalBooleanRef isInUltraInstinct) {
        PlayerDBCInfo dbc = PlayerDataUtil.getClientDBCInfo();
        int id = dbc.selectedDBCForm;
        if (id != -1) {
            if (id == DBCForm.GodOfDestruction || (id >= UltraInstinct && id <= UltraInstinct + 10)) {
                isInBase.set(true);
                isInUI.set(false);
            }
            isInGoD.set(false);
            isInMystic.set(false);
            isInKaioken.set(false);

        }
    }

    @Redirect(method = "Ascend", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;StusEfctsMe(I)Z"))
    private static boolean fixSomeFormsNotAscendingProperly5(int ste, @Local(name = "useUltraInstinct2") LocalBooleanRef useUltraInstinct2) {
        PlayerDBCInfo dbc = PlayerDataUtil.getClientDBCInfo();
        int id = dbc.selectedDBCForm;
        if (id != -1) {
            if ((id >= UltraInstinct && id <= UltraInstinct + 10) && ste == 5) {
                useUltraInstinct2.set(true);
                return false;
            }

        }
        return JRMCoreH.StusEfctsMe(ste);
    }


    @Redirect(method = "Ascend", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;State:B", ordinal = 11))
    private static byte fixSomeFormsNotAscendingProperly2() {
        PlayerDBCInfo dbc = PlayerDataUtil.getClientDBCInfo();
        DBCData data = DBCData.getClient();
        byte state = JRMCoreH.State;
        if (dbc.selectedDBCForm != -1) {
            if (state == 4 && data.Race == DBCRace.MAJIN && dbc.selectedDBCForm != state)
                return 3;

            if (data.Race == DBCRace.ARCOSIAN && (state == 5 || state == 6))
                return 3;

        }

        return state;
    }

    @Redirect(method = "Ascend", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;State:B", ordinal = 7))
    private static byte fixSomeFormsNotAscendingProperly3() {
        PlayerDBCInfo dbc = PlayerDataUtil.getClientDBCInfo();
        byte state = JRMCoreH.State;
        if (dbc.selectedDBCForm != -1 && dbc.selectedDBCForm != state) {
            if (JRMCoreH.Race == DBCRace.ARCOSIAN && state > 3)
                return 3;

        }

        return state;
    }

    @Redirect(method = "Ascend", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;isInState(I)Z"))
    private static boolean fixSomeFormsNotAscendingProperly(int state) {
        PlayerDBCInfo dbc = PlayerDataUtil.getClientDBCInfo();
        DBCData data = DBCData.getClient();
        if (state == 4) { //SSG2/SSJ4/SSBE condition
            if (dbc.selectedDBCForm != -1 && dbc.selectedDBCForm != JRMCoreH.State)
                return true;
        }
        if (data.Race == DBCRace.ARCOSIAN && (state == 5 || state == 6))
            return false;

        if (state == 2 && data.Race == DBCRace.MAJIN && (data.State == DBCForm.MajinPure || data.State == DBCForm.MajinFullPower))
            return true;

        if (dbc.selectedDBCForm != -1 && dbc.selectedDBCForm != JRMCoreH.State) {
            if (state == 0 && (data.Race == DBCRace.NAMEKIAN || data.Race == DBCRace.HUMAN || data.Race == DBCRace.MAJIN))
                return true;
            else
                return false;
        }

        return JRMCoreH.isInState(state);
    }

    /**
     * Prevents player from transforming to other DBC forms if they are in custom form, except stackable ones
     */
    @Inject(method = "Ascend", at = @At("HEAD"), cancellable = true)
    private static void Ascend(KeyBinding K, CallbackInfo ci) {
        PlayerDBCInfo dbc = PlayerDataUtil.getClientDBCInfo();
        if (K.getIsKeyPressed()) {
            if (dbc.selectedForm != -1)
                ci.cancel();

            Form form = DBCData.getClient().getForm();

            if (form != null) {
                if (JRMCoreH.PlyrSettingsB(0) && !form.stackable.isFormStackable(DBCForm.Kaioken)) {
                    ci.cancel();
                } else if (JRMCoreH.PlyrSettingsB(11) && !form.stackable.isFormStackable(DBCForm.UltraInstinct)) {
                    ci.cancel();
                } else if (JRMCoreH.PlyrSettingsB(16) && !form.stackable.isFormStackable(DBCForm.GodOfDestruction)) {
                    ci.cancel();
                } else if (JRMCoreH.PlyrSettingsB(6) && !form.stackable.isFormStackable(DBCForm.Mystic)) {
                    ci.cancel();
                }
            }

        }
    }

    /**
     * Descends from custom form when DBC descend key is pressed
     */
    @Inject(method = "Descend", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;kiInSuper:I", shift = At.Shift.AFTER), cancellable = true)
    private static void DescendModified(KeyBinding K, CallbackInfo ci) {
        PlayerDBCInfo dbc = PlayerDataUtil.getClientDBCInfo();
        DBCData dbcData = DBCData.getClient();
        Form form = dbcData.getForm();


        if (form != null) {
            if (dbc.selectedForm != -1) {
            } else if (JRMCoreH.PlyrSettingsB(0)) {
                if (dbcData.isForm(DBCForm.Kaioken))
                    return;
            } else if (JRMCoreH.PlyrSettingsB(11)) {
                if (dbcData.isForm(DBCForm.UltraInstinct))
                    return;
            } else if (JRMCoreH.PlyrSettingsB(16)) {
                if (dbcData.isForm(DBCForm.GodOfDestruction))
                    return;
            } else if (JRMCoreH.PlyrSettingsB(6))
                if (dbcData.isForm(DBCForm.Mystic))
                    return;


            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                DBCPacketHandler.Instance.sendToServer(new TransformPacket(-10, false, -1));
            else {
                if (form.requiredForm.containsKey((int) JRMCoreH.Race)) {
                    int id = dbcData.stats.getJRMCPlayerID();
                    JRMCoreH.State = form.requiredForm.get((int) JRMCoreH.Race);
                    JRMCoreH.data2[id] = JRMCoreH.State + ";" + JRMCoreH.data2[id].split(";")[1];
                }

                DBCPacketHandler.Instance.sendToServer(new TransformPacket(-1, false, -1));
            }
            ci.cancel();

        }
    }

    @Inject(method = "triForce", at = @At("HEAD"), cancellable = true)
    private static void fixRage(int i, int j, int k, CallbackInfo ci) {
        if (i == 1 && j == 1 && k == 100 && TransformController.ascending)
            ci.cancel();
    }


    /**
     * Methods Below so we don't need
     * to constantly scan stack traces
     */
    @Inject(method = "chargePart(Lnet/minecraft/entity/player/EntityPlayer;IIIIIZLjava/lang/String;)V", at = @At("HEAD"))
    private static void setFromRenderPlayerJBRA(EntityPlayer p, int r, int a, int c, int s, int k, boolean b, String se, CallbackInfo ci) {
        CommonProxy.CurrentAuraPlayer = p;
        ClientCache.isChangePart = true;
    }

    @Inject(method = "chargePart(Lnet/minecraft/entity/player/EntityPlayer;IIIIIZLjava/lang/String;)V", at = @At("TAIL"))
    private static void clearFromRenderPlayerJBRA(EntityPlayer p, int r, int a, int c, int s, int k, boolean b, String se, CallbackInfo ci) {
        CommonProxy.CurrentAuraPlayer = null;
        ClientCache.isChangePart = false;
    }

    @Inject(method = "chargePart(Lnet/minecraft/entity/player/EntityPlayer;IIIIIZLjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z", ordinal = 0), remap = true)
    private static void setEntity(EntityPlayer p, int r, int a, int c, int s, int k, boolean b, String se, CallbackInfo ci, @Local(name = "aura") LocalRef<Entity> aura) {
        if (aura.get() instanceof IEntityAura) {
            ((IEntityAura) aura.get()).setEntity(p);
            ((IEntityAura) aura.get()).setAuraData(DBCData.get(p));
        }
    }

    @Inject(method = "chargePart(Lnet/minecraft/entity/player/EntityPlayer;IIIIIZLjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z", ordinal = 1), remap = true)
    private static void setEntity2(EntityPlayer p, int r, int a, int c, int s, int k, boolean b, String se, CallbackInfo ci, @Local(name = "aura2") LocalRef<Entity> aura) {
        if (aura.get() instanceof IEntityAura) {
            ((IEntityAura) aura.get()).setEntity(p);
            ((IEntityAura) aura.get()).setAuraData(DBCData.get(p));
        }
    }


}
