package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JBRA.ModelBipedDBC;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.util.Utility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ModelBipedDBC.class, remap = false)
public class MixinModelBipedDBC {

    @ModifyVariable(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", at = @At("HEAD"), ordinal = 0, name = "s", argsOnly = true)
    private int renderHairsV2(int s) {
        if (Utility.getFormDataClient().isInCustomForm()) {
            CustomForm f = Utility.getFormDataClient().getCurrentForm();
            if (f.hairType.equals("base"))
                s = 0;
            else if (f.hairType.equals("ssj"))
                s = 1;
            else if (f.hairType.equals("ssj2"))
                s = 5;
        }

        return s;
    }
}
