package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreHDBC;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.PlayerFormData;
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
        if (!Utility.stackTraceContains("RenderPlayerJBRA")) {
            PlayerFormData formData = Utility.getSelfData();
            if (formData != null && formData.isInCustomForm()) {
                Form form = formData.getCurrentForm();
                if (form.display.auraColor != -1 && Utility.stackTraceContains("chargePart"))
                    ci.setReturnValue(form.display.auraColor);
                else if (form.display.furColor != -1 && (form.display.hairType.equals("ssj4") || form.display.hairType.equals("oozaru")))
                    ci.setReturnValue(form.display.furColor);
                else if (form.display.hairColor != -1)
                    ci.setReturnValue(form.display.hairColor);
            }
        }
    }

    @Inject(method = "DBCsizeBasedOnRace2(IIZ)F", at = @At("HEAD"), cancellable = true)
    private static void setCustomFormSize(int race, int state, boolean divine, CallbackInfoReturnable<Float> cir) {
        if (CommonProxy.CurrentJRMCTickPlayer != null) {
            Form form = null;

            if (Utility.isServer()) {
                PlayerFormData data = Utility.getFormData(CommonProxy.CurrentJRMCTickPlayer);
                if (data != null)
                    form = data.getCurrentForm();
            } else
                form = Utility.getFormClient(CommonProxy.CurrentJRMCTickPlayer);
            if (form != null) {
                cir.setReturnValue(form.display.formSize);
            }
        }
    }

}
