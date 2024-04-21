package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreHDBC;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.data.PlayerCustomFormData;
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
            PlayerCustomFormData formData = Utility.getSelfData();
            if (formData != null && formData.isInCustomForm()) {
                CustomForm form = formData.getCurrentForm();
                if (form.auraColor != -1 && Utility.stackTraceContains("chargePart"))
                    ci.setReturnValue(form.auraColor);
                else if (form.furColor != -1 && (form.hairType.equals("ssj4") || form.hairType.equals("oozaru")))
                    ci.setReturnValue(form.furColor);
                else if (form.hairColor != -1)
                    ci.setReturnValue(form.hairColor);
            }
        }
    }

    @Inject(method = "DBCsizeBasedOnRace2(IIZ)F", at = @At("HEAD"), cancellable = true)
    private static void setCustomFormSize(int race, int state, boolean divine, CallbackInfoReturnable<Float> cir) {
        if (CommonProxy.CurrentJRMCTickPlayer != null) {
            CustomForm form = null;

            if (Utility.isServer()) {
                PlayerCustomFormData data = Utility.getFormData(CommonProxy.CurrentJRMCTickPlayer);
                if (data != null)
                    form = data.getCurrentForm();
            } else
                form = Utility.getFormClient(CommonProxy.CurrentJRMCTickPlayer);
            if (form != null) {
                cir.setReturnValue(form.formSize);
            }
        }
    }

}
