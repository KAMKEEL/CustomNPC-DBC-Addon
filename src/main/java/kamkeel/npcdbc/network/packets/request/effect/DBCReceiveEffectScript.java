package kamkeel.npcdbc.network.packets.request.effect;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import net.minecraft.entity.player.EntityPlayer;
import kamkeel.npcdbc.network.LargeAbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public class DBCReceiveEffectScript extends LargeAbstractPacket {
    public static final String packetName = "NPC|ApplyEffectScript";

    @Override
    public Enum getType() {
        return EnumPacketRequest.EffectScriptReceive;
    }

    @Override
    public PacketChannel getChannel() {
        return PacketHandler.REQUEST_PACKETS;
    }


    @Override
    protected byte[] getData() throws IOException {
        return new byte[0];
    }

    @Override
    protected void handleCompleteData(ByteBuf data, EntityPlayer player) throws IOException {

    }
}
