package kamkeel.npcdbc.network.packets.player.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public final class DBCRequestFormWheel extends AbstractPacket {
    public static final String packetName = "NPC|RequestFormWheel";

    public DBCRequestFormWheel() {
    }


    @Override
    public Enum getType() {
        return EnumPacketPlayer.FormWheel;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {

    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        NetworkUtility.sendPlayerFormWheel(player);
    }
}
