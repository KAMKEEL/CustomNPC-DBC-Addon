package kamkeel.npcdbc.network.packets.player.aura;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public final class DBCSetAura extends AbstractPacket {
    public static final String packetName = "NPC|SetAura";
    private int auraID;

    public DBCSetAura(int auraID) {
        this.auraID = auraID;
    }

    public DBCSetAura() {}

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AuraSet;
    }

    @Override
    public PacketChannel getChannel() {
        return PacketHandler.PLAYER_PACKETS;
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
        dbcInfo.currentAura = -1;
        if (auraID != -1 && AuraController.getInstance().has(auraID)){
            if(dbcInfo.hasAuraUnlocked(auraID)){
                dbcInfo.currentAura = auraID;
            }
        }
        dbcInfo.updateClient();
        DBCData.get(player).saveNBTData(true);
    }
}
