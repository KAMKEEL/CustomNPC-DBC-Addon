package kamkeel.npcdbc.mixin.impl;

import JinRyuu.JRMCore.server.JGPlayerMP;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.events.DBCEventHooks;
import kamkeel.npcdbc.events.DBCPlayerEvent;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JGPlayerMP.class, remap = false)
public class MixinJGPlayerMP {

    @Shadow
    public EntityPlayer player;
    @Shadow
    private NBTTagCompound nbt;


    @Inject(method = "setState", at = @At("HEAD"), remap = false, cancellable = true)
    public void setState(int value, CallbackInfo ci) {
        if (DBCEventHooks.onFormChangeEvent(new DBCPlayerEvent.FormChangeEvent(Utility.getIPlayer(player), DBCData.get(player).State, value)))
            ci.cancel();
    }

    @Inject(method = "setState2", at = @At("HEAD"), remap = false, cancellable = true)
    public void setState2(int value, CallbackInfo ci) {
        if (DBCEventHooks.onFormChangeEvent(new DBCPlayerEvent.FormChangeEvent(Utility.getIPlayer(player), DBCData.get(player).State, value)))
            ci.cancel();
    }


}
