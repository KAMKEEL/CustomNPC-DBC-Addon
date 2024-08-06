package kamkeel.npcdbc.network.packets.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.FormWheelData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.ByteBufUtils;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public final class DBCSaveFormWheel extends AbstractPacket {
    public static final String packetName = "NPC|SaveFormWheel";

    private int wheelSlot;
    private FormWheelData data;

    public DBCSaveFormWheel() {
    }

    public DBCSaveFormWheel(int wheelSlot, FormWheelData data) {
        this.wheelSlot = wheelSlot;
        this.data = data;
    }


    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.wheelSlot);
        ByteBufUtils.writeNBT(out, this.data.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int wheelSlot = in.readInt();

        PlayerDBCInfo data = PlayerDataUtil.getDBCInfo(player);
        data.formWheel[wheelSlot].readFromNBT(ByteBufUtils.readNBT(in).getCompoundTag("FormWheel" + wheelSlot));
        data.updateClient();
    }
}
