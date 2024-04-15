package kamkeel.npcdbc.mixin.impl;

import JinRyuu.DragonBC.common.DBCKiTech;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.network.PacketRegistry;
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
            PlayerCustomFormData formData = Utility.getClientFormData(Minecraft.getMinecraft().thePlayer);
            if(formData.isInCustomForm()){
                if (JRMCoreH.PlyrSettingsB(0) && formData.getCurrentForm().isFormStackable(DBCForm.Kaioken)) {
                }
                if (JRMCoreH.PlyrSettingsB(11) && formData.getCurrentForm().isFormStackable(DBCForm.UltraInstinct)) {
                }
                if (JRMCoreH.PlyrSettingsB(16) && formData.getCurrentForm().isFormStackable(DBCForm.GodOfDestruction)) {
                }
                if (JRMCoreH.PlyrSettingsB(6) && formData.getCurrentForm().isFormStackable(DBCForm.Mystic)) {
                } else
                    ci.cancel();
            }
        }
    }

    /**
     * Descends from custom form when DBC descend key is pressed
     */
    @Inject(method = "Descend", at = @At("HEAD"), cancellable = true)
    private static void Descend(KeyBinding K, CallbackInfo ci) {
        if (K.getIsKeyPressed()) {
            PlayerCustomFormData formData = Utility.getClientFormData(Minecraft.getMinecraft().thePlayer);
            if(formData.isInCustomForm()){
                PacketRegistry.tellServer("Descend");
                DBCKiTech.soundAsc(formData.getCurrentForm().getDescendSound());
                ci.cancel();
            }
        }
    }
}
