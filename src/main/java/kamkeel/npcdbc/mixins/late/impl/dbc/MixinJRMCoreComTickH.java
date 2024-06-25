package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreComTickH;
import JinRyuu.JRMCore.server.JGPlayerMP;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.CommonProxy;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.LogWriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ExecutionException;

@Mixin(value = JRMCoreComTickH.class, remap = false)
public abstract class MixinJRMCoreComTickH {


    @Shadow
    public static MinecraftServer server;

    @Inject(method = "updatePlayersData", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;DBCsizeBasedOnRace(IIZ)F", shift = At.Shift.BEFORE))
    public void setCurrentTickPlayerServer(MinecraftServer server, int playerID, EntityPlayerMP player, JGPlayerMP jgPlayer, NBTTagCompound nbt, CallbackInfo ci) {
        CommonProxy.CurrentJRMCTickPlayer = player;
    }

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreComTickH;updatePlayersData(Lnet/minecraft/server/MinecraftServer;ILnet/minecraft/entity/player/EntityPlayerMP;LJinRyuu/JRMCore/server/JGPlayerMP;Lnet/minecraft/nbt/NBTTagCompound;)V"))
    public void tryCatchPlayerData(JRMCoreComTickH instance, MinecraftServer server, int chunkcoordinates, EntityPlayerMP A, JGPlayerMP divine, NBTTagCompound isp2,
                                   @Local(name = "playerID") int playerID, @Local(name = "player") EntityPlayerMP player, @Local(name = "jgPlayer") JGPlayerMP jgPlayer,
                                   @Local(name = "nbt") NBTTagCompound nbtTagCompound) {
        try {
            this.updatePlayersData(server, playerID, player, jgPlayer, nbtTagCompound);
        }
        catch (NullPointerException ok){
            LogWriter.except(ok);
        }
    }

    @Shadow
    public void updatePlayersData(MinecraftServer server, int playerID, EntityPlayerMP player, JGPlayerMP jgPlayer, NBTTagCompound nbt){}
}
