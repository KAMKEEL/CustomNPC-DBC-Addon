package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreCliTicH;
import JinRyuu.JRMCore.JRMCoreClient;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCoreCliTicH.class, remap = false)
public class MixinJRMCoreCliTickH {

    @Inject(method = "onTickInGame", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;data1:[Ljava/lang/String;", ordinal = 0, shift = At.Shift.BEFORE))
    public void setCurrentTickPlayerClientMain(CallbackInfo ci, @Local(name = "plyr") LocalRef<EntityPlayer> plyr) {
        CommonProxy.CurrentJRMCTickPlayer = plyr.get();

    }

    @Inject(method = "onTickInGame", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;data(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;", ordinal = 0, shift = At.Shift.BEFORE))
    public void setCurrentTickPlayerClientOthers(CallbackInfo ci, @Local(name = "plyr1") LocalRef<EntityPlayer> plyr1) {
        CommonProxy.CurrentJRMCTickPlayer = plyr1.get();

    }

    @Inject(method = "onRenderTick()V", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;cura:I", ordinal = 0, shift = At.Shift.BEFORE))
    private void renderCustomHeat(CallbackInfo ci) {
        DBCData dbcData = DBCData.getClient();
        if (dbcData == null)
            return;
        if (dbcData.addonCurrentHeat > 0) //renders heat bar
            JRMCoreClient.bars.rendera();

    }

}
