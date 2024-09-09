package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreCliTicH;
import JinRyuu.JRMCore.JRMCoreClient;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCStats;
import kamkeel.npcdbc.mixins.late.INPCStats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCoreCliTicH.class, remap = false)
public class MixinJRMCoreCliTickH {

    @Shadow
    public static EntityLivingBase lockOn;

    @Inject(method = "onTickInGame", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;data1:[Ljava/lang/String;", ordinal = 0, shift = At.Shift.BEFORE))
    public void setCurrentTickPlayerClientMain(CallbackInfo ci, @Local(name = "plyr") EntityPlayer plyr) {
        CommonProxy.CurrentJRMCTickPlayer = plyr;

    }

    @Inject(method = "onTickInGame", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;data(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;", ordinal = 0, shift = At.Shift.BEFORE))
    public void setCurrentTickPlayerClientOthers(CallbackInfo ci, @Local(name = "plyr1") EntityPlayer plyr1) {
        CommonProxy.CurrentJRMCTickPlayer = plyr1;

    }

    @Inject(method = "onRenderTick()V", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;cura:I", ordinal = 0, shift = At.Shift.BEFORE))
    private void renderCustomHeat(CallbackInfo ci) {
        DBCData dbcData = DBCData.getClient();
        if (dbcData == null)
            return;
        if (dbcData.addonCurrentHeat > 0) //renders heat bar
            JRMCoreClient.bars.rendera();

    }

    @Inject(method = "onRenderTick()V", at = @At("HEAD"))
    private void updateLockOn(CallbackInfo ci){
        if(lockOn instanceof EntityNPCInterface){
            DBCStats stats = ((INPCStats) ((EntityNPCInterface) lockOn).stats).getDBCStats();
            if(!stats.canBeLockedOn())
                lockOn = null;
        }
    }

}
