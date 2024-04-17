package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreComTickH;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = JRMCoreComTickH.class, remap = false)
public class MixinJRMCoreComTickH {

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;addToFormMasteriesValue(Lnet/minecraft/entity/player/EntityPlayer;DDIIIZZZZI)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onFMUpdateGain(MinecraftServer server, CallbackInfo ci) {
        int playersCount = server.getAllUsernames().length;
        for (int playerID = 0; playerID < playersCount; ++playerID) {
            String usernames = server.getAllUsernames()[playerID];
            EntityPlayerMP player = JRMCoreH.getPlayerForUsername(server, usernames);
            Utility.getFormData(player).updateCurrentFormMastery("update");
        }
    }
}
