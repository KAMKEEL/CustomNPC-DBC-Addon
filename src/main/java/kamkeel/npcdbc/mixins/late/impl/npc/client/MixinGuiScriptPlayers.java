package kamkeel.npcdbc.mixins.late.impl.npc.client;

import kamkeel.npcdbc.constants.DBCScriptType;
import noppes.npcs.client.gui.script.GuiScriptInterface;
import noppes.npcs.client.gui.script.GuiScriptPlayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = GuiScriptPlayers.class, remap = false)
public abstract class MixinGuiScriptPlayers extends GuiScriptInterface {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void onConstructorComplete(CallbackInfo info) {
        for (DBCScriptType val : DBCScriptType.values()) {
            this.hookList.add(val.function);
        }
    }
}
