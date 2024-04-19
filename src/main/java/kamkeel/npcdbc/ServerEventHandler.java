package kamkeel.npcdbc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.data.SyncedData.PerfectSync;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

public class ServerEventHandler {
    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void registerExtended(EntityConstructing event) {
        if (!(event.entity instanceof EntityPlayer))
            PerfectSync.registerAllDatas(event.entity); // entity registry
    }


    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null || event.player.worldObj == null)
            return;

        EntityPlayer player = event.player;
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
            if (player instanceof EntityPlayer){
                PerfectSync.registerAllDatas(player);
                if (player.ticksExisted % 10 == 0) {
                    // Load DBC Data Every 10 Ticks (0.5s)
                    if (DBCData.has(player))
                        DBCData.get(player).loadFromNBT(true);
                }
            }

            if (PlayerDataController.Instance != null) {
                PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
                if (((IPlayerFormData) playerData).getFormUpdate()) {
                    NBTTagCompound formCompound = new NBTTagCompound();
                    playerData.getDBCSync(formCompound);
                    NoppesUtilServer.sendDBCCompound((EntityPlayerMP) player, formCompound);
                    ((IPlayerFormData) playerData).finishFormInfo();
                }
            }
        }
    }
}
