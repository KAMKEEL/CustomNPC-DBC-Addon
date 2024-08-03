package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.DragonBC.common.DBCKiTech;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreKeyHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.client.KeyHandler;
import kamkeel.npcdbc.client.sound.ClientSound;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumNBTType;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixins.late.IEntityAura;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = DBCKiTech.class, remap = false)
public class MixinDBCKiTech {


    @Inject(method = "ChargeKi", at = @At(value = "FIELD", target = "LJinRyuu/DragonBC/common/DBCKiTech;time:I", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private static void ChargeKi(CallbackInfo ci) {
        boolean FnPressed = JRMCoreKeyHandler.Fn.getIsKeyPressed();
        if (DBCData.getClient().isFnPressed != FnPressed) {
            DBCData.getClient().isFnPressed = FnPressed;
            PacketHandler.Instance.sendToServer(new DBCSetValPacket(DBCData.getClient().player, EnumNBTType.BOOLEAN, "DBCIsFnPressed", FnPressed).generatePacket());

        }

        if (ClientCache.kiRevamp)
            ci.cancel();
    }

    @ModifyArgs(method = "DashKi", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/DBCKiTech;mv(FFLnet/minecraft/entity/player/EntityPlayer;F)V"))
    private static void changeSprintSpeed(Args args) {
        float speed = args.get(3);
        args.set(3, speed * DBCData.getClient().getSprintSpeed());
    }

    /**
     * Prevents player from transforming to other DBC forms if they are in custom form, except stackable ones
     */
    @ModifyArgs(method = "FloatKi", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/DBCKiTech;mv(FFLnet/minecraft/entity/player/EntityPlayer;F)V"))
    private static void changeBaseSpeed(Args args) {
        float speed = args.get(3);
        args.set(3, speed * DBCData.getClient().getBaseFlightSpeed() * DBCData.getClient().flightSpeedRelease / 100);

    }

    @ModifyArgs(method = "FloatKi", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/DBCKiTech;setThrowableHeading(Lnet/minecraft/entity/Entity;DDDFF)V"))
    private static void changeDynamic(Args args) {
        float speed = args.get(4);
        args.set(4, speed * DBCData.getClient().getDynamicFlightSpeed() * DBCData.getClient().flightSpeedRelease / 100);

    }

    @Inject(method = "FloatKi", at = @At(value = "FIELD", target = "LJinRyuu/DragonBC/common/DBCKiTech;floating:Z", ordinal = 7, shift = At.Shift.AFTER))
    private static void isFlying(KeyBinding kiFlight, KeyBinding keyBindJump, KeyBinding keyBindSneak, CallbackInfo ci) {

        if (DBCData.getClient().isFlying != DBCKiTech.floating) {
            DBCData.getClient().isFlying = DBCKiTech.floating;
            PacketHandler.Instance.sendToServer(new DBCSetValPacket(DBCData.getClient().player, EnumNBTType.BOOLEAN, "DBCisFlying", DBCKiTech.floating).generatePacket());
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

    @Redirect(method = "Ascend", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;isInState(I)Z"))
    private static boolean fixSomeFormsNotAscendingProperly(int state) {
        PlayerDBCInfo dbc = PlayerDataUtil.getClientDBCInfo();
        DBCData data = DBCData.getClient();
        if (state == 4) { //SSG2/SSJ4/SSBE condition
            if (dbc.selectedDBCForm != -1 && dbc.selectedDBCForm != JRMCoreH.State)
                return true;
        }
        if (dbc.selectedDBCForm != -1) {
            if (state == 0 && (data.Race == DBCRace.NAMEKIAN || data.Race == DBCRace.HUMAN))
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
        if (KeyHandler.AscendKey.getIsKeyPressed() && K.getIsKeyPressed())
            KeyBinding.setKeyBindState(K.getKeyCode(), false);

        if (K.getIsKeyPressed()) {
            if (TransformController.ascending)
                ci.cancel();

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
                new ClientSound(new SoundSource(form.getDescendSound(), dbcData.player)).play(true);
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
