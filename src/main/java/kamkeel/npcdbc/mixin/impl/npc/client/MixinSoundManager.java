package kamkeel.npcdbc.mixin.impl.npc.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import kamkeel.npcdbc.util.SoundHelper;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SoundManager.class)
public class MixinSoundManager {

    @Inject(method = "playSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;newSource(ZLjava/lang/String;Ljava/net/URL;Ljava/lang/String;ZFFFIF)V", shift = At.Shift.BEFORE))
    private void setRange(ISound sound, CallbackInfo ci, @Local(name = "f1") LocalFloatRef range) {
        if (sound instanceof SoundHelper.Sound)
            range.set(((SoundHelper.Sound) sound).range);
    }
}
