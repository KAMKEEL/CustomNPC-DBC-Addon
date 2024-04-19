package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.DBCKiTech;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.data.DBCExtended;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.network.packets.TransformPacket;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
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
            PlayerCustomFormData formData = Utility.getSelfData();
            if (formData != null && formData.isInCustomForm()) {
                if (JRMCoreH.PlyrSettingsB(0) && formData.getCurrentForm().isFormStackable(DBCForm.Kaioken)) {
                } else if (JRMCoreH.PlyrSettingsB(11) && formData.getCurrentForm().isFormStackable(DBCForm.UltraInstinct)) {
                } else if (JRMCoreH.PlyrSettingsB(16) && formData.getCurrentForm().isFormStackable(DBCForm.GodOfDestruction)) {
                } else if (JRMCoreH.PlyrSettingsB(6) && formData.getCurrentForm().isFormStackable(DBCForm.Mystic)) {
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
        PlayerCustomFormData formData = Utility.getSelfData();
        DBCExtended d = DBCExtended.get(Minecraft.getMinecraft().thePlayer);
        boolean returnEarly = true;
        if (d != null && formData != null && formData.isInCustomForm()) {
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
                PacketHandler.Instance.sendToServer(new TransformPacket(Minecraft.getMinecraft().thePlayer, -1, false).generatePacket());
                DBCKiTech.soundAsc(formData.getCurrentForm().getDescendSound());
                ci.cancel();
            }
        }
    }

    @Inject(method = "triForce", at = @At("HEAD"), cancellable = true)
    private static void fixRage(int i, int j, int k, CallbackInfo ci) {
        if (i == 1 && j == 1 && k == 100 && TransformController.ascending)
            ci.cancel();
    }
}
