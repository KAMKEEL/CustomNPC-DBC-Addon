package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.DBCExtended;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public final class PingPacket extends AbstractPacket {
    public static final String packetName = "NPCDBC|Ping";
    private DBCExtended data;

    public PingPacket() {
    }

    public PingPacket(DBCExtended data) {
        this.data = data;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeUTF8String(out,this.data.player.getCommandSenderName());
        ByteBufUtils.writeNBT(out,this.data.getNBT(new NBTTagCompound()));
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        String playerName = ByteBufUtils.readUTF8String(in);
        EntityPlayer sendingPlayer = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(playerName);
        if (sendingPlayer != null) {
            data = DBCExtended.get(sendingPlayer);
        }

        if (data == null) {
            return;
        }
        data.setNBT(ByteBufUtils.readNBT(in));
    }
}
