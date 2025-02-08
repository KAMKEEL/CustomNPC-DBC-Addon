package kamkeel.npcdbc.network.packets.player;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public final class PingPacket extends AbstractPacket {
    public static final String packetName = "NPC|Ping";
    private DBCData data;

    public PingPacket() {
    }

    public PingPacket(DBCData data) {
        this.data = data;
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.PingPacket;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeUTF8String(out, this.data.player.getCommandSenderName());
        ByteBufUtils.writeNBT(out,this.data.saveFromNBT(new NBTTagCompound()));
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        String playerName = ByteBufUtils.readUTF8String(in);
        EntityPlayer sendingPlayer = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(playerName);
        if (sendingPlayer != null){
            DBCData dbcData = DBCData.get(sendingPlayer);
            dbcData.loadFromNBT(ByteBufUtils.readNBT(in));
        }
    }
}
