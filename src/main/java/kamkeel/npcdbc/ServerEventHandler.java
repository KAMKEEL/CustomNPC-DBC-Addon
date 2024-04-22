package kamkeel.npcdbc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.PlayerFormData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.PingPacket;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

public class ServerEventHandler {

    @SubscribeEvent
    public void loginEvent(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote || event.player instanceof FakePlayer)
            return;
        DBCData dbcData = DBCData.get(event.player);
        dbcData.loadNBTData();
        dbcData.syncAllClients();
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
                if (((IPlayerFormData) playerData).getFormUpdate()) {
                    NBTTagCompound formCompound = new NBTTagCompound();
                    playerData.getDBCSync(formCompound);
                    NoppesUtilServer.sendDBCCompound((EntityPlayerMP) player, formCompound);
                    ((IPlayerFormData) playerData).finishFormInfo();
                }
            }

            if (player.ticksExisted % 10 == 0) {
                DBCData dbcData = DBCData.get(player);
                dbcData.loadNBTData();
            }

            handleFormProcesses(player);
        }
    }

    public void handleFormProcesses(EntityPlayer player) {
        Form form = Utility.getCurrentForm(player);
        if (form != null) {
            PlayerFormData formData = Utility.getFormData(player);
            DBCData dbcData = DBCData.get(player);

            if (dbcData.Release <= 0 || dbcData.Ki <= 0) { //reverts player from CF when ki or release are 0
                formData.currentForm = -1;
                formData.updateClient();
                dbcData.loadNBTData();
            }

            if (formData.hasTimer(form.id)) {
                formData.decrementTimer(form.id);
                if (player.ticksExisted % 20 == 0)
                    formData.updateClient();
            }
        }


    }

    @SubscribeEvent
    public void addTracking(PlayerEvent.StartTracking event) {
        if (event.target.worldObj == null || event.target.worldObj.isRemote)
            return;

        if (event.target instanceof EntityPlayer && !(event.target instanceof FakePlayer)) {
            DBCData data = DBCData.get((EntityPlayer) event.target);
            if (data == null) {
                return;
            }
            data.loadNBTData();
            PacketHandler.Instance.sendToPlayer(new PingPacket(data).generatePacket(), ((EntityPlayerMP) event.entityPlayer));
        }
    }
}
