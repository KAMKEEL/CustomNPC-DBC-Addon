package kamkeel.npcdbc.network.packets.request.effect;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import net.minecraft.entity.player.EntityPlayer;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public class DBCRequestEffectScript extends AbstractPacket {
    public static final String packetName = "NPC|GetEffectScript";

    @Override
    public Enum getType() {
        return EnumPacketRequest.EffectScriptGet;
    }

    @Override
    public PacketChannel getChannel() {
        return PacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {

    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {

    }
}
