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
    @Inject(method = "getPlayerColor2", at = @At("HEAD"), cancellable = true)
    private static void onGetPlayerColor2(int t, int d, int p, int r, int s, boolean v, boolean y, boolean ui, boolean gd, CallbackInfoReturnable<Integer> ci) {
        if (!Utility.stackTraceContains("RenderPlayerJBRA")) { //if this method is not called by RenderPlayerJBRA, as rendering data is already handled, and this method is heavily used in rendering in DBC
            PlayerCustomFormData formData = Utility.getFormDataClient();
            if (formData.isInCustomForm())
                ci.setReturnValue(formData.getCurrentForm().hairColor);
        }
    }
}
