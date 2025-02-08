package kamkeel.npcdbc.network.packets.effect;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class DBCRequestEffectScript extends AbstractPacket {
    public static final String packetName = "NPC|GetEffectScript";

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {

    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {

    }
}
