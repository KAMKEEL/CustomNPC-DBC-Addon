package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreGui;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(JRMCoreGui.class)
public class MixinJRMCoreGui {

    @Redirect(method = "renderActionMenu", at=@At(value="INVOKE", target = "Lorg/lwjgl/input/Keyboard;getKeyName(I)Ljava/lang/String;"), remap = false)
    private String fixWrongKeyCodeCrash(int key){
        return GameSettings.getKeyDisplayString(key);
    }
}
