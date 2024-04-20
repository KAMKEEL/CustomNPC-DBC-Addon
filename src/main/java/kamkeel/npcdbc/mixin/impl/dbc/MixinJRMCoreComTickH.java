package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreComTickH;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.JGPlayerMP;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalByteRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
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

    @Inject(method = "updatePlayersData", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;DBCsizeBasedOnRace(IIZ)F", shift = At.Shift.BEFORE))
    public void changeSize1(MinecraftServer server, int playerID, EntityPlayerMP player, JGPlayerMP jgPlayer, NBTTagCompound nbt, CallbackInfo ci, @Local(name = "state") LocalByteRef st) {
        CommonProxy.CurrentJRMCTickPlayer = player;
    }

}
