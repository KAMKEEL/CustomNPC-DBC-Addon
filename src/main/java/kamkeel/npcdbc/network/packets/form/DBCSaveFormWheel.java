package kamkeel.npcdbc.network.packets.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public final class DBCSaveFormWheel extends AbstractPacket {
    public static final String packetName = "NPC|SaveFormWheel";

    private int wheelSlot, formID;

    public DBCSaveFormWheel() {
    }

    public DBCSaveFormWheel(int wheelSlot, int formID) {
        this.wheelSlot = wheelSlot;
        this.formID = formID;
    }


    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.wheelSlot);
        out.writeInt(this.formID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int wheelSlot = in.readInt();
        int formID = in.readInt();

        PlayerDBCInfo data = PlayerDataUtil.getDBCInfo(player);
        data.addFormWheel(wheelSlot, formID);
        data.updateClient();
    }
}
