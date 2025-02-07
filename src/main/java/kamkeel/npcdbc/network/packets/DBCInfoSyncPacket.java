package kamkeel.npcdbc.network.packets;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.controllers.DBCSyncController;
import kamkeel.npcdbc.network.LargeAbstractPacket;
import kamkeel.npcs.network.enums.EnumSyncAction;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientCacheHandler;

import java.io.IOException;

/**
 * A large sync packet that sends chunked data to the client for a given SyncType
 * along with a SyncAction (RELOAD, UPDATE, REMOVE).
 */
public final class DBCInfoSyncPacket extends LargeAbstractPacket {
    public static final String packetName = "NPC|SyncDBC";

    private int enumSyncType;
    private EnumSyncAction enumSyncAction;
    private NBTTagCompound syncData;
    private int operationID;

    public DBCInfoSyncPacket() {}

    /**
     * Constructs a new LargeSyncPacket.
     */
    public DBCInfoSyncPacket(int enumSyncType, EnumSyncAction enumSyncAction, int catId, NBTTagCompound syncData) {
        this.enumSyncType = enumSyncType;
        this.enumSyncAction = enumSyncAction;
        this.syncData = syncData;
        this.operationID = catId;
    }

    @Override
    public String getChannel() {
        return packetName;
    }


    @Override
    protected byte[] getData() throws IOException {
        ByteBuf buffer = Unpooled.buffer();
        // 1) Write SyncType
        buffer.writeInt(enumSyncType);
        // 2) Write SyncAction
        buffer.writeInt(enumSyncAction.ordinal());
        // 3) Optional Category ID
        buffer.writeInt(operationID);
        // 4) Write the NBTTagCompound
        ByteBufUtils.writeBigNBT(buffer, syncData);

        // Copy the buffer’s readable bytes into a byte[]
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        return bytes;
    }

    @Override
    protected void handleCompleteData(ByteBuf data, EntityPlayer player) throws IOException {
        if (CustomNpcs.side() != Side.CLIENT)
            return;

        int syncType = data.readInt();
        int syncActionOrdinal = data.readInt();
        int categoryID = data.readInt();

        EnumSyncAction action = EnumSyncAction.values()[syncActionOrdinal];
        try {
            NBTTagCompound tag = ByteBufUtils.readBigNBT(data);
            if(syncType == DBCSyncType.PLAYERDATA){
                ClientCacheHandler.playerData.setDBCSync(tag);
                return;
            }
            handleSyncPacketClient(syncType, action, categoryID, tag);
        }
        catch (RuntimeException ignored){}
    }

    private void handleSyncPacketClient(int syncType, EnumSyncAction enumSyncAction, int id, NBTTagCompound data) {
        switch (enumSyncAction) {
            case RELOAD:
                DBCSyncController.clientSync(syncType, data);
                break;
            case UPDATE:
                DBCSyncController.clientSyncUpdate(syncType, data);
                break;
            case REMOVE:
                DBCSyncController.clientSyncRemove(syncType, id);
                break;
        }
    }
}
