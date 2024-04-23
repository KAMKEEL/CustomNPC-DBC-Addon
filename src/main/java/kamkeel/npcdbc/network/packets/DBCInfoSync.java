package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.DBCSyncController;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.EnumPacketClient;

import java.io.IOException;

public final class DBCInfoSync extends AbstractPacket {
    public static final String packetName = "NPCDBC|Sync";
    private int syncINT;
    private EnumPacketClient syncType;
    private int removeINT;
    private NBTTagCompound send;

    public DBCInfoSync(int syncINT, EnumPacketClient syncType, NBTTagCompound sendNBT, int remove) {
        this.syncINT = syncINT;
        this.syncType = syncType;
        this.send = sendNBT;
        this.removeINT = remove;
    }

    public DBCInfoSync(){}

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.syncType.ordinal());
        if(this.syncType == EnumPacketClient.SYNC_REMOVE){
            out.writeInt(this.syncINT);
            out.writeInt(this.removeINT);
        } else {
            out.writeInt(this.syncINT);
            ByteBufUtils.writeNBT(out, this.send);
        }
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        this.syncType = EnumPacketClient.values()[in.readInt()];
        if(syncType == EnumPacketClient.SYNC_ADD || syncType == EnumPacketClient.SYNC_END){
            this.syncINT = in.readInt();
            this.send = ByteBufUtils.readNBT(in);
            DBCSyncController.clientSync(this.syncINT, this.send, syncType == EnumPacketClient.SYNC_END);
        }
        else if(syncType == EnumPacketClient.SYNC_UPDATE){
            this.syncINT = in.readInt();
            this.send = ByteBufUtils.readNBT(in);
            DBCSyncController.clientSyncUpdate(this.syncINT, this.send);
        }
        else if(syncType == EnumPacketClient.SYNC_REMOVE){
            this.syncINT = in.readInt();
            this.removeINT = in.readInt();
            DBCSyncController.clientSyncRemove(this.syncINT, this.removeINT);
        }
    }
}
