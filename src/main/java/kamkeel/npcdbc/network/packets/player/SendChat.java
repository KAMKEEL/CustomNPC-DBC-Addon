package kamkeel.npcdbc.network.packets.player;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.util.ByteBufUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public final class SendChat extends AbstractPacket {
    public static final String packetName = "NPC|SendChat";
    private int dataCount;
    private Object[] data;
    private boolean info;

    public SendChat() {
    }

    public SendChat(Object... data) {
        this.dataCount = data.length;
        this.data = data;
    }

    public SendChat(boolean info, Object... data) {
        this.info = info;
        this.dataCount = data.length;
        this.data = data;
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
        out.writeBoolean(this.info);
        out.writeInt(this.dataCount);
        for(Object ob : data){
            if (ob instanceof String) {
                ByteBufUtils.writeUTF8String(out, (String) ob);
            } else {
                throw new IllegalArgumentException("SendChat only accepts strings.");
            }
        }
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        boolean info = in.readBoolean();
        if(info && ConfigDBCClient.HideInfoMessage)
            return;
        int stringAmount = in.readInt();
        StringBuilder message = new StringBuilder();
        for(int i = 0; i < stringAmount; i++){
            message.append(StatCollector.translateToLocal(ByteBufUtils.readUTF8String(in)));
        }
        Utility.sendMessage(player, message.toString());
    }
}
