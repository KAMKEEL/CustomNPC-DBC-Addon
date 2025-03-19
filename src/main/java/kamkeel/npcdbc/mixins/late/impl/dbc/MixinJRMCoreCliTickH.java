package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreCliTicH;
import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreH;
import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCStats;
import kamkeel.npcdbc.mixins.late.INPCStats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCoreCliTicH.class, remap = false)
public class MixinJRMCoreCliTickH {

    @Shadow
    public static EntityLivingBase lockOn;

    @Redirect(method = "onTickInGame", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;rSai(I)Z"))
    public boolean fixOozaruCustomFormSize(int r) {
        boolean isSaiyan = JRMCoreH.rSai(r);

        // if isn't saiyan, skip useless routine
        if (!isSaiyan)
            return false;

        // isSaiyan is now confirmed to be true
        // If there isn't a JRMCTickPlayer stored, just return isSaiyan (true)
        if (CommonProxy.getCurrentJRMCTickPlayer() == null) {
            return true;
        }

        Form form = DBCData.getForm(CommonProxy.getCurrentJRMCTickPlayer());
        if (form == null)
            return true;

        return form.stackable.vanillaStackable;
    }

    @Inject(method = "onTickInGame", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;data1:[Ljava/lang/String;", ordinal = 0, shift = At.Shift.BEFORE))
    public void setCurrentTickPlayerClientMain(CallbackInfo ci, @Local(name = "plyr") EntityPlayer plyr) {
        CommonProxy.setCurrentJRMCTickPlayer(plyr);

    }

    @Inject(method = "onTickInGame", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;data(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;", ordinal = 0, shift = At.Shift.BEFORE))
    public void setCurrentTickPlayerClientOthers(CallbackInfo ci, @Local(name = "plyr1") EntityPlayer plyr1) {
        CommonProxy.setCurrentJRMCTickPlayer(plyr1);

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
    private void updateLockOn(CallbackInfo ci) {
        if (lockOn instanceof EntityNPCInterface) {
            DBCStats stats = ((INPCStats) ((EntityNPCInterface) lockOn).stats).getDBCStats();
            if (!stats.canBeLockedOn())
                lockOn = null;
        }
    }

}
