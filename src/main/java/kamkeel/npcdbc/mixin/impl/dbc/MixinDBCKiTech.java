package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.DBCClient;
import JinRyuu.DragonBC.common.DBCKiTech;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.client.sound.Sound;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.enums.EnumNBTType;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.DBCSetValPacket;
import kamkeel.npcdbc.network.packets.TransformPacket;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = DBCKiTech.class, remap = false)
public class MixinDBCKiTech {

    /**
     * Prevents player from transforming to other DBC forms if they are in custom form, except stackable ones
     */
    @ModifyArgs(method = "FloatKi", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/DBCKiTech;mv(FFLnet/minecraft/entity/player/EntityPlayer;F)V"))
    private static void changeBaseSpeed(Args args) {
        DBCData dbcData = DBCData.getClient();
        float speed = args.get(3);

        args.set(3, speed * dbcData.flightSpeed);

    }

    @ModifyArgs(method = "FloatKi", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/DBCKiTech;setThrowableHeading(Lnet/minecraft/entity/Entity;DDDFF)V", ordinal = 1))
    private static void changeDynamic(Args args) {
        DBCData dbcData = DBCData.getClient();
        float speed = args.get(3);

        args.set(3, speed * dbcData.flightSpeed);

    }

    @Inject(method = "FloatKi", at = @At(value = "FIELD", target = "LJinRyuu/DragonBC/common/DBCKiTech;floating:Z", ordinal = 7, shift = At.Shift.AFTER))
    private static void isFlying(KeyBinding kiFlight, KeyBinding keyBindJump, KeyBinding keyBindSneak, CallbackInfo ci) {
        DBCData dbcData = DBCData.getClient();
        if (dbcData.isFlying != DBCKiTech.floating) {
            dbcData.isFlying = DBCKiTech.floating;
            PacketHandler.Instance.sendToServer(new DBCSetValPacket(dbcData.player, EnumNBTType.BOOLEAN, "DBCisFlying", DBCKiTech.floating).generatePacket());
        }


    }

    @Inject(method = "FloatKi", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;motionY:D", ordinal = 5,shift = At.Shift.BEFORE))
    private static void flightGravity(KeyBinding kiFlight, KeyBinding keyBindJump, KeyBinding keyBindSneak, CallbackInfo ci) {
        DBCData dbcData = DBCData.getClient();
        if (!dbcData.flightGravity)
            DBCClient.mc.thePlayer.motionY /= dbcData.flightGravity && !JRMCoreH.isShtng && JRMCoreConfig.PlayerFlyingDragDownOn ? 15.15 : 150.15;


    }

    @Inject(method = "FloatKi", at = @At("HEAD"), cancellable = true)
    private static void disableFlight(KeyBinding kiFlight, KeyBinding keyBindJump, KeyBinding keyBindSneak, CallbackInfo ci) {
        DBCData dbcData = DBCData.getClient();
        if (!dbcData.flightEnabled)
            ci.cancel();

    }


    @Inject(method = "chargePart(Z)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getPlayerEntityByName(Ljava/lang/String;)Lnet/minecraft/entity/player/EntityPlayer;", remap = true), cancellable = true)
    private static void cancelAura(boolean b, CallbackInfo ci, @Local(name = "e") LocalRef<Entity> entity) {
        if (entity.get() instanceof EntityPlayer) {
            DBCData dbcData = DBCData.get((EntityPlayer) entity.get());
            Aura aura = dbcData.getAura();
            if (aura != null) {
                if (aura.display.overrideDBCAura)
                    ci.cancel();
                else if (dbcData.isForm(DBCForm.Base) || dbcData.isForm(DBCForm.Kaioken) && aura.display.hasKaiokenAura)
                    ci.cancel();
            }
        }

    }


    /**
     * Prevents player from transforming to other DBC forms if they are in custom form, except stackable ones
     */
    @Inject(method = "Ascend", at = @At("HEAD"), cancellable = true)
    private static void Ascend(KeyBinding K, CallbackInfo ci) {
        if (K.getIsKeyPressed()) {
            Form form = DBCData.getClient().getForm();

            if (form != null) {
                if (JRMCoreH.PlyrSettingsB(0) && form.stackable.isFormStackable(DBCForm.Kaioken)) {
                } else if (JRMCoreH.PlyrSettingsB(11) && form.stackable.isFormStackable(DBCForm.UltraInstinct)) {
                } else if (JRMCoreH.PlyrSettingsB(16) && form.stackable.isFormStackable(DBCForm.GodOfDestruction)) {
                } else if (JRMCoreH.PlyrSettingsB(6) && form.stackable.isFormStackable(DBCForm.Mystic)) {
                } else
                    ci.cancel();
            }

        }
    }

    /**
     * Descends from custom form when DBC descend key is pressed
     */
    @Inject(method = "Descend", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;kiInSuper:I", shift = At.Shift.AFTER), cancellable = true)
    private static void DescendModified(KeyBinding K, CallbackInfo ci) {
        PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();

        DBCData dbcData = DBCData.getClient();
        Form form = dbcData.getForm();

        boolean returnEarly = true;
        if (form != null) {
            if (dbcData.formSettingOn(DBCForm.Kaioken)) {
                if (dbcData.isForm(DBCForm.Kaioken))
                    returnEarly = false;
            } else if (dbcData.formSettingOn(DBCForm.UltraInstinct)) {
                if (dbcData.isForm(DBCForm.UltraInstinct))
                    returnEarly = false;
            } else if (dbcData.formSettingOn(DBCForm.GodOfDestruction)) {
                if (dbcData.isForm(DBCForm.GodOfDestruction))
                    returnEarly = false;
            } else if (dbcData.formSettingOn(DBCForm.Mystic))
                if (dbcData.isForm(DBCForm.Mystic))
                    returnEarly = false;


            if (returnEarly) {
                if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                    PacketHandler.Instance.sendToServer(new TransformPacket(Minecraft.getMinecraft().thePlayer, -10, false).generatePacket());
                else {
                    if (form.requiredForm.containsKey((int) JRMCoreH.Race)) {
                        int id = dbcData.stats.getJRMCPlayerID();
                        JRMCoreH.State = form.requiredForm.get((int) JRMCoreH.Race);
                        JRMCoreH.data2[id] = JRMCoreH.State + JRMCoreH.data2[id].substring(1);
                    }

                    PacketHandler.Instance.sendToServer(new TransformPacket(Minecraft.getMinecraft().thePlayer, -1, false).generatePacket());
                }
                new Sound(form.getDescendSound(), dbcData.player).play(true);
                ci.cancel();
            }
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


}
