package kamkeel.npcdbc.controllers.sync.handlers;

import kamkeel.npcdbc.controllers.sync.DBCSyncType;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.controllers.sync.SyncHandler;
import kamkeel.npcs.network.PacketHandler;
import kamkeel.npcs.network.enums.EnumSyncAction;
import kamkeel.npcs.network.packets.data.large.SyncPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class DBCInfoSyncHandler implements SyncHandler {

    public static void sync(EntityPlayerMP player) {
        PlayerDBCInfo data = PlayerDataUtil.getDBCInfo(player);
        if (data != null) {
            NBTTagCompound nbt = data.saveNBTData(new NBTTagCompound());
            PacketHandler.Instance.sendToPlayer(new SyncPacket(DBCSyncType.PLAYER_DATA, EnumSyncAction.UPDATE, -1, nbt), player);
        }
    }

    @Override
    public NBTTagCompound serializeAll() {
        return null;
    }

    @Override
    public void clientHandleUpdate(NBTTagCompound compound) {
        PlayerDataUtil.getClientDBCInfo().loadNBTData(compound);
    }

    /** No need to cache, same as CNPC player data*/
    public boolean isCachedType() {
        return false;
    }
}
