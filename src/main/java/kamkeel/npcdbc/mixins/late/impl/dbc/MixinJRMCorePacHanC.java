package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCorePacHanC;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.util.DBCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCorePacHanC.class,remap = false)
public class MixinJRMCorePacHanC {

    @Inject(method = "handleTri", at = @At(value = "INVOKE", target = "Lio/netty/buffer/ByteBuf;readInt()I", ordinal = 23), cancellable = true)
    private void disableRendering(ByteBuf buffer, CallbackInfo ci, @Local(name = "i") LocalIntRef i) {
        DBCUtils.CONFIG_UI_NAME[i.get()] = ByteBufUtils.readUTF8String(buffer);
    }


}
