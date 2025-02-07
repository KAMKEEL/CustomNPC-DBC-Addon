package kamkeel.npcdbc.mixins.late.impl.npc.client;

import noppes.npcs.client.gui.script.GuiNPCEventScripts;
import noppes.npcs.client.gui.script.GuiScriptInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiNPCEventScripts.class, remap = false)
public abstract class MixinGuiNPCEventScripts extends GuiScriptInterface {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void onConstructorComplete(CallbackInfo info) {
    }
}

