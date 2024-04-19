package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreHDBC;
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
            if (formData != null && formData.isInCustomForm())
                if (formData.getCurrentForm().hairType.equals("ssj4"))
                    ci.setReturnValue(formData.getCurrentForm().furColor);
                else
                    ci.setReturnValue(formData.getCurrentForm().hairColor);
        }
    }
}
