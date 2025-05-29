package kamkeel.npcdbc.network.packets.player.aura;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

public final class DBCSelectAura extends AbstractPacket {
    public static final String packetName = "NPC|SelectAura";
    private int auraID;

    public DBCSelectAura(int auraID) {
        this.auraID = auraID;
    }

    public DBCSelectAura() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AuraSelect;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.auraID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int auraID = in.readInt();
        PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
        PlayerDBCInfo dbcInfo = PlayerDataUtil.getDBCInfo(playerData);
        if (auraID == -1)
            dbcInfo.currentAura = -1;
        dbcInfo.selectedAura = -1;
        NBTTagCompound compound = new NBTTagCompound();
        if (auraID != -1 && AuraController.getInstance().has(auraID)) {
            if (dbcInfo.hasAuraUnlocked(auraID)) {
                Aura aura = (Aura) AuraController.getInstance().get(auraID);
                dbcInfo.selectedAura = auraID;
                NetworkUtility.sendServerMessage(player, "§b", "npcdbc.auraSelect", " ", aura.getMenuName());
                compound = aura.writeToNBT();
            }
        } else {
            NetworkUtility.sendServerMessage(player, "§9", "npcdbc.clearedSelection");
        }

        dbcInfo.updateClient();
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
        DBCData.get(player).saveNBTData(true);
    }
}
