package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreHDBC;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
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
            if (ClientCache.isChangePart && CommonProxy.CurrentAuraPlayer != null) { //IForm auraColor
                Form form = DBCData.get(CommonProxy.CurrentAuraPlayer).getForm();
                if (form != null && form.display.auraColor != -1)
                    ci.setReturnValue(form.display.auraColor);

            } else { //IForm ki bar color
                Form form = DBCData.getClient().getForm();
                if (form != null) {
                    if (form.display.furColor != -1 && (form.display.hairType.equals("ssj4") || form.display.hairType.equals("oozaru")))
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
            Form form = DBCData.get(CommonProxy.CurrentJRMCTickPlayer).getForm();

            if (form != null && form.display.hasSize()) {
                if (form.display.keepOriginalSize)
                    cir.setReturnValue(size.get() * form.display.formSize);
                else
                    cir.setReturnValue(form.display.formSize);
            }
        }
    }
}
