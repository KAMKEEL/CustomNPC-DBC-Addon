package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCorePacHanC;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.util.DBCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCorePacHanC.class, remap = false)
public class MixinJRMCorePacHanC {

    @Inject(method = "handleTri", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/server/config/dbc/JGConfigUltraInstinct;CONFIG_UI_ATTACK_RATE:[[B", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private void UI_CONFIG2(ByteBuf buffer, CallbackInfo ci) {
        DBCUtils.CONFIG_UI_NAME = new String[JGConfigUltraInstinct.CONFIG_UI_LEVELS];
    }


    @Inject(method = "handleTri", at = @At(value = "INVOKE", target = "Lio/netty/buffer/ByteBuf;readInt()I", ordinal = 23), cancellable = true)
    private void UI_CONFIG(ByteBuf buffer, CallbackInfo ci, @Local(name = "i") LocalIntRef i) {
        DBCUtils.CONFIG_UI_NAME[i.get()] = ByteBufUtils.readUTF8String(buffer);
    }


}
