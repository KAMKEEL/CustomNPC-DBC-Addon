package kamkeel.npcdbc.network.packets.player.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.FormWheelData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.util.ByteBufUtils;
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
    public Enum getType() {
        return EnumPacketPlayer.FormWheelSave;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
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
        NBTTagCompound newSlot = ByteBufUtils.readNBT(in).getCompoundTag("FormWheel" + wheelSlot);
        data.formWheel[wheelSlot].readFromNBT(newSlot);
        data.updateClient();
    }
}
