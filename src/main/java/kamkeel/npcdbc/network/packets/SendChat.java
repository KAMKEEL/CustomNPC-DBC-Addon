package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.ByteBufUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.io.IOException;

public final class SendChat extends AbstractPacket {
    public static final String packetName = "NPC|SendChat";
    private int dataCount;
    private Object[] data;

    public SendChat() {
    }

    public SendChat(Object... data) {
        this.dataCount = data.length;
        this.data = data;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
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
        int stringAmount = in.readInt();
        StringBuilder message = new StringBuilder();
        for(int i = 0; i < stringAmount; i++){
            message.append(StatCollector.translateToLocal(ByteBufUtils.readUTF8String(in)));
        }
        Utility.sendMessage(player, message.toString());
    }
}
