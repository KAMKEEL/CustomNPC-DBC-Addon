package kamkeel.npcdbc.mixins.late.impl.npc.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.constants.DBCScriptType;
import noppes.npcs.client.gui.GuiScript;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiScript.class, remap = false)
public abstract class MixinGuiScript {

    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 14, shift = At.Shift.AFTER))
    public void onConstructorComplete(CallbackInfo info, @Local(name = "list") LocalRef<List<String>> lis) {
        List<String> list = lis.get();
        list.add(DBCScriptType.DAMAGED.function);
    }
}

