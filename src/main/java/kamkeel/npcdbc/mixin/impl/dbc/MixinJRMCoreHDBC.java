package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreHDBC;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.util.Utility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = JRMCoreHDBC.class, remap = false)
public class MixinJRMCoreHDBC {
    @Inject(method = "getPlayerColor", at = @At("HEAD"), cancellable = true)
    private static void onGetPlayerColor(int type, int def, int p, int r, int s, boolean divine, boolean y, boolean ui, boolean ui2, boolean gd, CallbackInfoReturnable<Integer> ci) {
        //if this method is not called by RenderPlayerJBRA, as rendering data is already handled, and this method is heavily used in rendering in DBC
        // To be improved by kAM
        if (!ClientCache.fromRenderPlayerJBRA) {
            if (ClientCache.isChangePart) {
                DBCData dbcData = DBCData.get(CommonProxy.CurrentAuraPlayer);
                if (dbcData.addonFormID != -1) {
                    IForm form = FormController.getInstance().get(dbcData.addonFormID);
                    if (form != null && ((Form) form).display.auraColor != -1) {
                        ci.setReturnValue(((Form) form).display.auraColor);
                    }
                }
            } else {
                PlayerDBCInfo formData = Utility.getSelfData();
                if (formData != null && formData.getCurrentForm() != null) {
                    Form form = formData.getCurrentForm();
                    if (form.display.auraColor != -1)
                        ci.setReturnValue(form.display.auraColor);
                    else if (form.display.furColor != -1 && (form.display.hairType.equals("ssj4") || form.display.hairType.equals("oozaru")))
                        ci.setReturnValue(form.display.furColor);
                    else if (form.display.hairColor != -1)
                        ci.setReturnValue(form.display.hairColor);
                }
            }
        }
    }

    @Inject(method = "DBCsizeBasedOnRace2(IIZ)F", at = @At(value = "TAIL"), cancellable = true)
    private static void setCustomFormSize(int race, int state, boolean divine, CallbackInfoReturnable<Float> cir, @Local(name = "f3") LocalFloatRef size) {
        if (CommonProxy.CurrentJRMCTickPlayer != null) {
            Form form = null;

            if (Utility.isServer()) {
                PlayerDBCInfo data = Utility.getData(CommonProxy.CurrentJRMCTickPlayer);
                if (data != null)
                    form = data.getCurrentForm();
            } else
                form = Utility.getFormClient(CommonProxy.CurrentJRMCTickPlayer);
            if (form != null && form.display.hasSize()) {
                if (form.display.keepOriginalSize)
                    cir.setReturnValue(size.get() + form.display.formSize);
                else
                    cir.setReturnValue(form.display.formSize);
            }
        }
    }
}
