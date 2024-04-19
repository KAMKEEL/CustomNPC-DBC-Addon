package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.DBCExtended;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

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
        if (this.data.player instanceof EntityPlayer) {
            out.writeBoolean(true);
            ByteBufUtils.writeUTF8String(out,this.data.player.getCommandSenderName());
        } else {
            out.writeBoolean(false);
            out.writeInt(this.data.player.getEntityId());
        }
        ByteBufUtils.writeNBT(out,this.data.getNBT());
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        DBCExtended data = null;
        boolean isPlayer = in.readBoolean();
        if (isPlayer) {
            String playerName = ByteBufUtils.readUTF8String(in);
            EntityPlayer sendingPlayer = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(playerName);
            if (sendingPlayer != null) {
                data = DBCExtended.get(sendingPlayer);
            }
        }

        if (data == null) {
            return;
        }
        data.setNBT(ByteBufUtils.readNBT(in));
    }
}
