package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

import static noppes.npcs.NoppesUtilServer.getPlayerByName;

public final class DBCSetServerNBT extends AbstractPacket {
    public static final String packetName = "DBO|FixInt";
    private EntityPlayer player;
    private String tag;
    private int value;

    public DBCSetServerNBT(EntityPlayer player, String tag, int value) {
        this.player = player;
        this.tag = tag;
        this.value = value;
    }

    public DBCSetServerNBT() {}

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeUTF8String(out,this.player.getCommandSenderName());
        ByteBufUtils.writeUTF8String(out,this.tag);
        out.writeInt(value);

        DBCData dbcData = DBCData.get(this.player);
        dbcData.getRawCompound().setInteger(tag, this.value);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        String playerName = ByteBufUtils.readUTF8String(in);
        EntityPlayer sendingPlayer = getPlayerByName(playerName);
        if (sendingPlayer == null)
            return;

        String tag = ByteBufUtils.readUTF8String(in);
        if(tag == null || tag.isEmpty())
            return;

        DBCData dbcData = DBCData.get(sendingPlayer);
        int newValue = in.readInt();
        dbcData.getRawCompound().setInteger(tag, newValue);
    }
}
