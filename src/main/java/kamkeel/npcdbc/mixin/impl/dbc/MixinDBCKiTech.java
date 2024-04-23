package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.DBCKiTech;
import JinRyuu.JRMCore.JRMCoreH;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.TransformPacket;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DBCKiTech.class, remap = false)
public class MixinDBCKiTech {
    /**
     * Prevents player from transforming to other DBC forms if they are in custom form, except stackable ones
     */
    @Inject(method = "Ascend", at = @At("HEAD"), cancellable = true)
    private static void Ascend(KeyBinding K, CallbackInfo ci) {
        if (K.getIsKeyPressed()) {
            PlayerDBCInfo formData = Utility.getSelfData();
            if (formData != null && formData.isInCustomForm()) {
                Form form = formData.getCurrentForm();
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
    }

    /**
     * Descends from custom form when DBC descend key is pressed
     */
    @Inject(method = "Descend", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;kiInSuper:I", shift = At.Shift.AFTER), cancellable = true)
    private static void DescendModified(KeyBinding K, CallbackInfo ci) {
        PlayerDBCInfo formData = Utility.getSelfData();
        DBCData d = DBCData.get(Minecraft.getMinecraft().thePlayer);
        boolean returnEarly = true;
        if (d != null && formData != null && formData.isInCustomForm()) {
            Form form = formData.getCurrentForm();
            if (d.formSettingOn(DBCForm.Kaioken)) {
                if (d.isForm(DBCForm.Kaioken))
                    returnEarly = false;
            } else if (d.formSettingOn(DBCForm.UltraInstinct)) {
                if (d.isForm(DBCForm.UltraInstinct))
                    returnEarly = false;
            } else if (d.formSettingOn(DBCForm.GodOfDestruction)) {
                if (d.isForm(DBCForm.GodOfDestruction))
                    returnEarly = false;
            } else if (d.formSettingOn(DBCForm.Mystic))
                if (d.isForm(DBCForm.Mystic))
                    returnEarly = false;


            if (returnEarly) {
                if (form.hasParent() && formData.hasUnlocked(form.getParentID()))
                    PacketHandler.Instance.sendToServer(new TransformPacket(Minecraft.getMinecraft().thePlayer, form.getParentID(), false).generatePacket());
                else
                    PacketHandler.Instance.sendToServer(new TransformPacket(Minecraft.getMinecraft().thePlayer, -1, false).generatePacket());
                DBCKiTech.soundAsc(form.getDescendSound());
                ci.cancel();
            }
        }
    }


    @Inject(method = "triForce", at = @At("HEAD"), cancellable = true)
    private static void fixRage(int i, int j, int k, CallbackInfo ci) {
        if (i == 1 && j == 1 && k == 100 && TransformController.ascending)
            ci.cancel();
    }

    @Inject(method = "chargePart(Lnet/minecraft/entity/player/EntityPlayer;IIIIIZLjava/lang/String;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", ordinal = 0, shift = At.Shift.AFTER))
    private static void chargePart(EntityPlayer p, int r, int a, int c, int s, int k, boolean b, String se, CallbackInfo ci, @Local(name = "state") LocalFloatRef state, @Local(name = "state2") LocalFloatRef state2, @Local(name = "kk") LocalBooleanRef kk, @Local(name = "ssb") LocalBooleanRef ssb, @Local(name = "ssg") LocalBooleanRef ssg, @Local(name = "ssbs") LocalBooleanRef ssbs, @Local(name = "v") LocalBooleanRef divine, @Local(name = "oozar") LocalBooleanRef oozaru, @Local(name = "ui") LocalBooleanRef ui, @Local(name = "gd") LocalBooleanRef godestruction) {
        Aura aura = null; //pls make the framework for this
        if (aura != null) {
            if (aura.display.type.equals("ssg"))
                ssg.set(true);
            else if (aura.display.type.equals("ssb"))
                ssb.set(true);
            else if (aura.display.type.equals("ssbkk")) {
                kk.set(true);
                ssb.set(true);
            } else if (aura.display.type.equals("ssbevo"))
                ssbs.set(true);
            else if (aura.display.type.equals("ssrose")) {
                ssb.set(true);
                divine.set(true);
            } else if (aura.display.type.equals("ssroseevo")) {
                ssbs.set(true);
                divine.set(true);
            } else if (aura.display.type.equals("ui"))
                ui.set(true);
            else if (aura.display.type.equals("godofdestruction"))
                godestruction.set(true);

        }
    }

//    @Inject(method = "Descend", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;Rls(B)V", shift = At.Shift.AFTER), cancellable = true)
//    private static void fix0ReleaseOnDescend(KeyBinding K, CallbackInfo ci) {
//        CustomForm form = Utility.getSelfData() != null ? Utility.getSelfData().getCurrentForm() : null;
//        if (form != null)
//            ci.cancel();
//    }
}
