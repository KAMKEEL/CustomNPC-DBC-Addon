package kamkeel.npcdbc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixin.IPlayerDBCInfo;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.CapsuleInfo;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.FakePlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

public class ServerEventHandler {

    @SubscribeEvent
    public void loginEvent(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote || event.player instanceof FakePlayer)
            return;
        DBCData dbcData = DBCData.get(event.player);
        dbcData.loadNBTData(true);
        PacketHandler.Instance.sendToPlayer(new CapsuleInfo(true).generatePacket(), (EntityPlayerMP) event.player);
        PacketHandler.Instance.sendToPlayer(new CapsuleInfo(false).generatePacket(), (EntityPlayerMP) event.player);
        StatusEffectController.getInstance().loadEffects(event.player);
    }


    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote || event.player instanceof FakePlayer)
            return;

        EntityPlayer player = event.player;
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
            // Send Form Information
            if (PlayerDataController.Instance != null) {
                PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
                if (((IPlayerDBCInfo) playerData).getDBCInfoUpdate()) {
                    NBTTagCompound formCompound = new NBTTagCompound();
                    playerData.getDBCSync(formCompound);
                    NoppesUtilServer.sendDBCCompound((EntityPlayerMP) player, formCompound);
                    ((IPlayerDBCInfo) playerData).endDBCInfo();
                }
            }

            if (player.ticksExisted % ConfigDBCGameplay.CheckEffectsTick == 0)
                StatusEffectController.Instance.runEffects(player);

            if (player.ticksExisted % 10 == 0) {
                // Keep the Player informed on their own data
                DBCData dbcData = DBCData.get(player);
                if (player.ticksExisted % 20 == 0)
                    dbcData.decrementActiveEffects();

                dbcData.syncAllClients();
            }
            handleFormProcesses(player);
        }
    }

    public void handleFormProcesses(EntityPlayer player) {
        Form form = Utility.getCurrentForm(player);
        if (form != null) {
            PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
            DBCData dbcData = DBCData.get(player);

            if (dbcData.Release <= 0 || dbcData.Ki <= 0) { //reverts player from CF when ki or release are 0
                formData.currentForm = -1;
                formData.updateClient();
                dbcData.loadNBTData(true);
            }

            if (formData.hasTimer(form.id)) {
                formData.decrementTimer(form.id);
                if (player.ticksExisted % 20 == 0)
                    formData.updateClient();
            }
            if (form.mastery.hasKiDrain()) {
                if (player.ticksExisted % 10 == 0) {
                    float toDrain = form.mastery.kiDrain * form.mastery.calculateMulti("kiDrain", formData.getCurrentLevel());
                    dbcData.restoreKiPercent(-toDrain / form.mastery.kiDrainTimer * 10);
                }
            }
        }
    }

}
