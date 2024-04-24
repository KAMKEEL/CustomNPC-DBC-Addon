package kamkeel.npcdbc.mixin.impl.npc;

import io.netty.buffer.ByteBuf;
import kamkeel.addon.DBCAddon;
import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.controllers.DBCSyncController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCStats;
import kamkeel.npcdbc.mixin.INPCDisplay;
import kamkeel.npcdbc.mixin.INPCStats;
import kamkeel.npcdbc.mixin.IPlayerDBCInfo;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.SyncType;
import noppes.npcs.controllers.PlayerDataController;
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

    @Shadow(remap = false)
    public boolean supportEnabled;

    /**
     * @author Kamkeel
     * @reason Allow for Copying of Data for DBC Model information
     */
    @Overwrite(remap = false)
    public void dbcCopyData(EntityLivingBase copied, EntityLivingBase entity) {
        if (!supportEnabled)
            return;

        if (entity instanceof EntityNPCInterface && copied instanceof EntityNPCInterface) {
            EntityNPCInterface receiverNPC = (EntityNPCInterface) entity;
            EntityNPCInterface npc = (EntityNPCInterface) copied;
            INPCStats stats = (INPCStats) npc.stats;
            INPCStats receiverStats = (INPCStats) receiverNPC.stats;

            INPCDisplay display = (INPCDisplay) npc.display;
            INPCDisplay receiverDisplay = (INPCDisplay) receiverNPC.display;

            receiverStats.getDBCStats().setEnabled(stats.getDBCStats().isEnabled());
            receiverStats.getDBCStats().setFriendlyFist(stats.getDBCStats().isFriendlyFist());
            receiverStats.getDBCStats().setIgnoreDex(stats.getDBCStats().isIgnoreDex());
            receiverStats.getDBCStats().setIgnoreBlock(stats.getDBCStats().isIgnoreBlock());
            receiverStats.getDBCStats().setIgnoreEndurance(stats.getDBCStats().isIgnoreEndurance());
            receiverStats.getDBCStats().setIgnoreKiProtection(stats.getDBCStats().isIgnoreKiProtection());
            receiverStats.getDBCStats().setIgnoreFormReduction(stats.getDBCStats().isIgnoreFormReduction());
            receiverStats.getDBCStats().setHasDefensePenetration(stats.getDBCStats().hasDefensePenetration());
            receiverStats.getDBCStats().setDefensePenetration(stats.getDBCStats().getDefensePenetration());

            receiverDisplay.getDBCDisplay().setEnabled(display.getDBCDisplay().isEnabled());
            receiverDisplay.getDBCDisplay().setFormAuraTypes(display.getDBCDisplay().getFormAuraTypes());
        }
    }

    /**
     * @author Kamkeel
     * @reason Checks if the DBC Attack event will be run
     */
    @Overwrite(remap = false)
    public boolean canDBCAttack(EntityNPCInterface npc, float attackStrength, Entity receiver) {
        if (!supportEnabled)
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
        if (npc.isRemote())
            return;

        if (attackStrength <= 0)
            return;

        if (!(receiver instanceof EntityPlayer))
            return;

        if (npc.stats instanceof INPCStats) {
            EntityPlayer player = (EntityPlayer) receiver;
            DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();

            // Calculate DBC Damage
            int damageToHP = DBCUtils.calculateDBCStatDamage(player, (int) attackStrength, dbcStats);
            DamageSource damageSource = new NpcDamageSource("mob", npc);
            DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(Utility.getIPlayer(player), damageToHP, damageSource, DBCDamageSource.NPC);
            if (DBCEventHooks.onDBCDamageEvent(damagedEvent))
                return;

            DBCUtils.lastSetDamage = (int) damagedEvent.damage;
            DBCUtils.doDBCDamage(player, damageToHP, dbcStats);
        }
    }

    /**
     * @author Kamkeel
     * @reason Performs DBC Damage Calculations
     */
    @Overwrite(remap = false)
    public boolean isKO(EntityNPCInterface npc, EntityPlayer player) {
        if (npc.isRemote())
            return false;

        if (npc.stats instanceof INPCStats) {
            DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();
            if (dbcStats.enabled && dbcStats.isFriendlyFist()) {
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
        ((IPlayerDBCInfo) playerData).getPlayerDBCInfo().saveNBTData(nbtTagCompound);
    }

    /**
     * @author Kamkeel
     * @reason Reads Custom Form Player Data
     */
    @Overwrite(remap = false)
    public void readFromNBT(PlayerData playerData, NBTTagCompound nbtTagCompound) {
        ((IPlayerDBCInfo) playerData).getPlayerDBCInfo().loadNBTData(nbtTagCompound);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController. Sent by Server to Client
     */
    @Overwrite(remap = false)
    public void syncPlayer(EntityPlayerMP playerMP) {
        DBCSyncController.syncPlayer(playerMP);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController
     */
    @Overwrite(remap = false)
    public void formPacketGet(EntityPlayer player, ByteBuf buffer) {
        Form customForm = (Form) FormController.getInstance().get(buffer.readInt());
        NBTTagCompound compound = customForm.writeToNBT();
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController
     */
    @Overwrite(remap = false)
    public void formPacketRemove(EntityPlayer player, ByteBuf buffer) {
        FormController.getInstance().delete(buffer.readInt());
        NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
        NBTTagCompound compound = (new Form()).writeToNBT();
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController
     */
    @Overwrite(remap = false)
    public void formPacketSave(EntityPlayer player, ByteBuf buffer) throws IOException {
        Form customForm = new Form();
        customForm.readFromNBT(Server.readNBT(buffer));
        FormController.getInstance().saveForm(customForm);
        NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, customForm.writeToNBT());
    }
}
