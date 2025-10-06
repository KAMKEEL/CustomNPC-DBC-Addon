package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.server.JGPlayerMP;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.controllers.AttributeController;
import kamkeel.npcs.controllers.data.attribute.tracker.PlayerAttributeTracker;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.config.ConfigMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JGPlayerMP.class, remap = false)
public class MixinJGPlayerMP {

    @Shadow
    public EntityPlayer player;


    @Inject(method = "setState", at = @At("HEAD"), remap = false, cancellable = true)
    public void setState(int value, CallbackInfo ci) {
        if (DBCEventHooks.onFormChangeEvent(new DBCPlayerEvent.FormChangeEvent(PlayerDataUtil.getIPlayer(player), false, DBCData.get(player).State, false, value)))
            ci.cancel();
    }

    @Inject(method = "setState", at = @At("TAIL"), remap = false)
    public void setStateTail(int value, CallbackInfo ci) {
        if (player != null && !player.worldObj.isRemote && ConfigMain.AttributesEnabled) {
            PlayerAttributeTracker tracker = AttributeController.getTracker(player);
            tracker.recalcAttributes(player);
        }
    }

    @Inject(method = "setState2", at = @At("HEAD"), remap = false, cancellable = true)
    public void setState2(int value, CallbackInfo ci) {
        if (DBCEventHooks.onFormChangeEvent(new DBCPlayerEvent.FormChangeEvent(PlayerDataUtil.getIPlayer(player), false, DBCData.get(player).State, false, value)))
            ci.cancel();
    }

    @Inject(method = "setState2", at = @At("TAIL"), remap = false)
    public void setState2Tail(int value, CallbackInfo ci) {
        if (player != null && !player.worldObj.isRemote && ConfigMain.AttributesEnabled) {
            PlayerAttributeTracker tracker = AttributeController.getTracker(player);
            tracker.recalcAttributes(player);
        }
    }
}
