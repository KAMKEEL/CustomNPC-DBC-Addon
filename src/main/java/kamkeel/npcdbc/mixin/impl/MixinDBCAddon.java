package kamkeel.npcdbc.mixin.impl;

import io.netty.buffer.ByteBuf;
import kamkeel.addon.DBCAddon;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.data.DBCStats;
import kamkeel.npcdbc.mixin.INPCDisplay;
import kamkeel.npcdbc.mixin.INPCStats;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.controllers.data.Animation;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.util.HashMap;

import static JinRyuu.JRMCore.JRMCoreH.getInt;

@Mixin(DBCAddon.class)
public class MixinDBCAddon {

    @Shadow(remap = false) public boolean supportEnabled;

    /**
     * @author Kamkeel
     * @reason Allow for Copying of Data for DBC Model information
     */
    @Overwrite(remap = false)
    public void dbcCopyData(EntityLivingBase copied, EntityLivingBase entity) {
        if(!supportEnabled)
            return;

        if (entity instanceof EntityNPCInterface && copied instanceof EntityNPCInterface) {
            EntityNPCInterface receiverNPC = (EntityNPCInterface) entity;
            EntityNPCInterface npc = (EntityNPCInterface) copied;
            INPCStats stats = (INPCStats) npc.stats;
            INPCStats receiverStats =  (INPCStats) receiverNPC.stats;

            INPCDisplay display = (INPCDisplay) npc.display;
            INPCDisplay receiverDisplay =  (INPCDisplay) receiverNPC.display;

            receiverStats.getDBCStats().setFriendlyFist(stats.getDBCStats().isFriendlyFist());
            receiverStats.getDBCStats().setIgnoreDex(stats.getDBCStats().isIgnoreDex());
            receiverStats.getDBCStats().setIgnoreBlock(stats.getDBCStats().isIgnoreBlock());
            receiverStats.getDBCStats().setIgnoreEndurance(stats.getDBCStats().isIgnoreEndurance());
            receiverStats.getDBCStats().setIgnoreKiProtection(stats.getDBCStats().isIgnoreKiProtection());
            receiverStats.getDBCStats().setIgnoreFormReduction(stats.getDBCStats().isIgnoreFormReduction());
            receiverStats.getDBCStats().setHasDefensePenetration(stats.getDBCStats().hasDefensePenetration());
            receiverStats.getDBCStats().setDefensePenetration(stats.getDBCStats().getDefensePenetration());
        }
    }

    /**
     * @author Kamkeel
     * @reason Checks if the DBC Attack event will be run
     */
    @Overwrite(remap = false)
    public boolean canDBCAttack(EntityNPCInterface npc, float attackStrength, Entity receiver) {
        if(!supportEnabled)
            return false;
        DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();
        return dbcStats.enabled && receiver instanceof EntityPlayer && attackStrength > 0;
    }

    /**
     * @author Kamkeel
     * @reason Performs DBC Damage Calculations
     */
    @Overwrite(remap = false)
    public void doDBCDamage(EntityNPCInterface npc, float attackStrength, Entity receiver) {
        if(npc.isRemote())
            return;

        if(attackStrength <= 0)
            return;

        if(!(receiver instanceof EntityPlayer))
            return;

        if(npc.stats instanceof INPCStats){
            EntityPlayer player = (EntityPlayer) receiver;
            DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();
            DBCUtils.doDBCDamage(player, (int) attackStrength, dbcStats);
        }
    }

    /**
     * @author Kamkeel
     * @reason Performs DBC Damage Calculations
     */
    @Overwrite(remap = false)
    public boolean isKO(EntityNPCInterface npc, EntityPlayer player) {
        if(npc.isRemote())
            return false;

        if(npc.stats instanceof INPCStats){
            DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();
            if(dbcStats.enabled && dbcStats.isFriendlyFist()){
                int currentKO = getInt(player, "jrmcHar4va");
                return currentKO > 0;
            }
        }
        return false;
    }


    /**
     * @author Kamkeel
     * @reason Writes Custom Form Player Data
     */
    @Overwrite(remap = false)
    public void writeToNBT(PlayerData playerData, NBTTagCompound nbtTagCompound) {
        ((IPlayerFormData) playerData).getCustomFormData().saveNBTData(nbtTagCompound);
    }

    /**
     * @author Kamkeel
     * @reason Reads Custom Form Player Data
     */
    @Overwrite(remap = false)
    public void readFromNBT(PlayerData playerData, NBTTagCompound nbtTagCompound) {
        ((IPlayerFormData) playerData).getCustomFormData().loadNBTData(nbtTagCompound);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController. Sent by Server to Client
     */
    @Overwrite(remap = false)
    public void syncPlayer(EntityPlayerMP playerMP){
        NBTTagList list = new NBTTagList();
        NBTTagCompound compound = new NBTTagCompound();
        for(CustomForm customForm : FormController.getInstance().customForms.values()){
            list.appendTag(customForm.writeToNBT());
            if(list.tagCount() > 10){
                compound = new NBTTagCompound();
                compound.setTag("Data", list);
                Server.sendData(playerMP, EnumPacketClient.SYNC_ADD, SyncType.CUSTOM_FORM, compound);
                list = new NBTTagList();
            }
        }
        compound = new NBTTagCompound();
        compound.setTag("Data", list);
        Server.sendData(playerMP, EnumPacketClient.SYNC_END, SyncType.CUSTOM_FORM, compound);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController
     */
    @Overwrite(remap = false)
    public void clientSync(NBTTagCompound compound, boolean syncEnd){
        NBTTagList list = compound.getTagList("Data", 10);
        for(int i = 0; i < list.tagCount(); i++)
        {
            CustomForm form = new CustomForm();
            form.readFromNBT(list.getCompoundTagAt(i));
            FormController.getInstance().customFormsSync.put(form.id, form);
        }
        if(syncEnd){
            FormController.getInstance().customForms = FormController.getInstance().customFormsSync;
            FormController.getInstance().customFormsSync = new HashMap<Integer, CustomForm>();
        }
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController
     */
    @Overwrite(remap = false)
    public void syncUpdate(NBTTagCompound compound, ByteBuf buffer){
        CustomForm form = new CustomForm();
        form.readFromNBT(compound);
        FormController.getInstance().customForms.put(form.id, form);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController
     */
    @Overwrite(remap = false)
    public void syncRemove(int id){
        CustomForm form = FormController.Instance.customForms.remove(id);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController
     */
    @Overwrite(remap = false)
    public void formPacketGet(EntityPlayer player, ByteBuf buffer){
        CustomForm customForm = (CustomForm) FormController.getInstance().get(buffer.readInt());
        NBTTagCompound compound = customForm.writeToNBT();
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController
     */
    @Overwrite(remap = false)
    public void formPacketGets(EntityPlayer player, ByteBuf buffer){
        NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController
     */
    @Overwrite(remap = false)
    public void formPacketRemove(EntityPlayer player, ByteBuf buffer){
        FormController.getInstance().delete(buffer.readInt());
        NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
        NBTTagCompound compound = (new CustomForm()).writeToNBT();
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController
     */
    @Overwrite(remap = false)
    public void formPacketSave(EntityPlayer player, ByteBuf buffer) throws IOException {
        CustomForm customForm = new CustomForm();
        customForm.readFromNBT(Server.readNBT(buffer));
        FormController.getInstance().saveForm(customForm);
        NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, customForm.writeToNBT());
    }
}
