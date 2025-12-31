package kamkeel.npcdbc.network.packets.player.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.AbilityWheelData;
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

public class DBCSaveAbilityWheel extends AbstractPacket {
    public static final String packetName = "NPC|SaveAbilityWheel";

    private int wheelSlot;
    private AbilityWheelData data;

    public DBCSaveAbilityWheel() {
    }

    public DBCSaveAbilityWheel(int wheelSlot, AbilityWheelData data) {
        this.wheelSlot = wheelSlot;
        this.data = data;
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AbilityWheelSave;
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
        NBTTagCompound newSlot = ByteBufUtils.readNBT(in).getCompoundTag("AbilityWheel" + wheelSlot);
        data.abilityWheel[wheelSlot].readFromNBT(newSlot);
        data.updateClient();
    }
}
