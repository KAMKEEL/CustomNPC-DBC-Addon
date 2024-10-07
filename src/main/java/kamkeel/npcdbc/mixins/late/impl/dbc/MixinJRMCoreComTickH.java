package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreComTickH;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.JGPlayerMP;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCFormMastery;
import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.LogWriter;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCoreComTickH.class, remap = false)
public abstract class MixinJRMCoreComTickH {

    @Shadow
    private boolean dbc;
    @Shadow
    private boolean charge;
    @Shadow
    public static MinecraftServer server;
    @Shadow
    public static boolean start;

    @Unique
    private static int[] customNPC_DBC_Addon$emptyPowerPointGrowthArray;
    @Unique
    private static int[] customNPC_DBC_Addon$emptyPowerPointCostArray;

    @Inject(method = "serverStart", at=@At("HEAD"))
    public void initializeEmptyArrays(MinecraftServer server, CallbackInfo ci){
        if(start){
            customNPC_DBC_Addon$emptyPowerPointGrowthArray = new int[JRMCoreConfig.ArcosianPPGrowth.length];
            customNPC_DBC_Addon$emptyPowerPointCostArray = new int[JRMCoreConfig.ArcosianPPCost.length];
        }
    }

    @Redirect(method = "serverTick", at=@At(value = "INVOKE", target="LJinRyuu/JRMCore/JRMCoreH;getPlayerForUsername(Lnet/minecraft/server/MinecraftServer;Ljava/lang/String;)Lnet/minecraft/entity/player/EntityPlayerMP;"))
    public EntityPlayerMP setCurrentTickPlayerServerPRE(MinecraftServer server, String s){
        EntityPlayerMP player = JRMCoreH.getPlayerForUsername(server, s);
        CommonProxy.CurrentJRMCTickPlayer = player;
        return player;
    }
    @Inject(method = "serverTick", at=@At("RETURN"))
    public void setCurrentTickPlayerServerPOST(MinecraftServer server, CallbackInfo ci){
        CommonProxy.CurrentJRMCTickPlayer = null;
    }

    @Redirect(method = "updatePlayersData", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;rSai(I)Z"))
    public boolean fixOozaruCustomFormSize(int r){
        boolean isSaiyan = JRMCoreH.rSai(r);

        // if isn't saiyan, skip useless routine
        if(!isSaiyan)
            return false;

        // isSaiyan is now confirmed to be true
        // If there isn't a JRMCTickPlayer stored, just return isSaiyan (true)
        if (CommonProxy.CurrentJRMCTickPlayer == null) {
            return true;
        }

        Form form = DBCData.getForm(CommonProxy.CurrentJRMCTickPlayer);
        if(form == null)
            return true;

        return form.stackable.vanillaStackable;
    }

//    @Inject(method = "updatePlayersData", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;DBCsizeBasedOnRace(IIZ)F", shift = At.Shift.BEFORE))
//    public void setCurrentTickPlayerServer(MinecraftServer server, int playerID, EntityPlayerMP player, JGPlayerMP jgPlayer, NBTTagCompound nbt, CallbackInfo ci) {
//        CommonProxy.CurrentJRMCTickPlayer = player;
//    }

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

    @Redirect(method = "serverTick", at = @At(value = "FIELD", target="LJinRyuu/JRMCore/JRMCoreConfig;ArcosianPPGrowth:[I", ordinal = 0, opcode = Opcodes.GETSTATIC))
    public int[] stopNormalPPRegenOnCustoms(@Local(name = "player") EntityPlayerMP player){
        DBCData dbcData = DBCData.get(player);
        if(dbcData == null)
            return JRMCoreConfig.ArcosianPPGrowth;
        Form form = dbcData.getForm();
        if(form == null || (!form.mastery.powerPointEnabled && form.stackable.vanillaStackable))
            return JRMCoreConfig.ArcosianPPGrowth;

        if(form.mastery.powerPointGrowth > 0 || !form.stackable.vanillaStackable){
            return customNPC_DBC_Addon$emptyPowerPointGrowthArray;
        }
        return JRMCoreConfig.ArcosianPPGrowth;
    }
    @Redirect(method = "serverTick", at = @At(value = "FIELD", target="LJinRyuu/JRMCore/JRMCoreConfig;ArcosianPPCost:[I", ordinal = 0, opcode = Opcodes.GETSTATIC))
    public int[] stopNormalPPCost(@Local(name = "player") EntityPlayerMP player){
        DBCData dbcData = DBCData.get(player);
        if(dbcData == null)
            return JRMCoreConfig.ArcosianPPCost;

        Form form = dbcData.getForm();
        if(form == null || (!form.mastery.powerPointEnabled && form.stackable.vanillaStackable))
            return JRMCoreConfig.ArcosianPPCost;

        if(form.mastery.powerPointCost > 0 || !form.stackable.vanillaStackable){

            return customNPC_DBC_Addon$emptyPowerPointCostArray;
        }

        return JRMCoreConfig.ArcosianPPCost;
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target="LJinRyuu/JRMCore/JRMCoreH;getArcosianFormID(IZZZ)I", shift = At.Shift.AFTER))
    public void customFormPowerPoints(MinecraftServer server, CallbackInfo ci, @Local(name = "player") EntityPlayerMP player){
        DBCData dbcData = DBCData.get(player);
        if(dbcData == null)
            return;
        Form form = dbcData.getForm();
        if(form == null)
            return;

        if(!form.mastery.powerPointEnabled)
            return;

        if(form.mastery.powerPointCost > 0 && dbcData.Release >= 100){
            updatePowerPointCost(dbcData, form);
        }else if(form.mastery.powerPointGrowth > 0 && dbcData.Release < 50){
            updatePowerPointRegen(dbcData, form);
        }

    }

    private void updatePowerPointRegen(DBCData dbcData, Form form) {
        int racialSkill = JRMCoreH.SklLvlX(1, dbcData.RacialSkills) - 1;
        if(racialSkill < 0)
            racialSkill = 0;
        int pointGain = (int) (form.mastery.powerPointGrowth * JRMCoreConfig.appm);

        int newPowerPoints = dbcData.ArcReserve + pointGain;
        if(newPowerPoints > JRMCoreConfig.ArcosianPPMax[racialSkill])
            newPowerPoints = JRMCoreConfig.ArcosianPPMax[racialSkill];
        dbcData.ArcReserve = newPowerPoints;
        dbcData.saveNBTData(false);
    }

    private void updatePowerPointCost(DBCData dbcData, Form form) {
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(dbcData.player);
        int racialSkill = JRMCoreH.SklLvlX(1, dbcData.RacialSkills) - 1;
        float ppCost = form.mastery.powerPointCost;
        if(JGConfigDBCFormMastery.FM_Enabled && ppCost != 0){
            ppCost *= form.mastery.calculateMulti("ppcost", formData.getCurrentLevel());
        }
        dbcData.ArcReserve -= (int) ppCost;
        if(dbcData.ArcReserve < 0)
            dbcData.ArcReserve = 0;
        if(dbcData.ArcReserve > JRMCoreConfig.ArcosianPPMax[racialSkill])
            dbcData.ArcReserve = JRMCoreConfig.ArcosianPPMax[racialSkill];
        dbcData.saveNBTData(false);
    }

    @Shadow
    public void updatePlayersData(MinecraftServer server, int playerID, EntityPlayerMP player, JGPlayerMP jgPlayer, NBTTagCompound nbt){}
}
