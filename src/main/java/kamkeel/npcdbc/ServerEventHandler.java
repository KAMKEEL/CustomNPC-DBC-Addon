package kamkeel.npcdbc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.DBCExtended;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.PingPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

public class ServerEventHandler {
    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void registerExtended(EntityConstructing event) {
        if ((event.entity instanceof EntityPlayer)){
            DBCExtended dbcExtended = DBCExtended.get((EntityPlayer) event.entity);
            dbcExtended.loadNBTData(null);
            dbcExtended.syncClient();
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null || event.player.worldObj == null)
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
                DBCExtended dbcExtended = DBCExtended.get(player);
                dbcExtended.loadNBTData(null);
                dbcExtended.syncClient();
            }
        }
    }


    @SubscribeEvent
    public void addTracking(PlayerEvent.StartTracking event){
        if(event.target instanceof EntityPlayer && !(event.target instanceof FakePlayer)){
            DBCExtended data = DBCExtended.get((EntityPlayer) event.target);
            if (data == null) {
                return;
            }
            data.loadNBTData(null);
            PacketHandler.Instance.sendToPlayer(new PingPacket(data).generatePacket(),
                ((EntityPlayerMP)event.entityPlayer));
        }
    }
}
