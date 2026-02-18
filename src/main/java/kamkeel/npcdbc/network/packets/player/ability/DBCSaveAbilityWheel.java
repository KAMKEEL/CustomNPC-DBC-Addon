package kamkeel.npcdbc.network.packets.player.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.AbilityWheelData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

/**
 * Packet sent from client to server to save ability wheel slot configuration.
 */
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
        NBTTagCompound compound = ByteBufUtils.readNBT(in);

        if (wheelSlot < 0 || wheelSlot >= 6) return;

        PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
        if (playerData == null) return;

        PlayerDBCInfo data = ((IPlayerDBCInfo) playerData).getPlayerDBCInfo();
        if (data == null) return;

        NBTTagCompound slotData = compound.getCompoundTag("AbilityWheel" + wheelSlot);
        data.abilityWheel[wheelSlot].readFromNBT(slotData);
        data.updateClient();
    }
}
