package kamkeel.npcdbc.network.packets.player.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public final class DBCRequestFormWheel extends AbstractPacket {
    public static final String packetName = "NPC|RequestFormWheel";

    public DBCRequestFormWheel() {
    }


    @Override
    public Enum getType() {
        return null;
    }

    @Override
    public PacketChannel getChannel() {
        return PacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {

    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        NetworkUtility.sendPlayerFormWheel(player);
    }
}
