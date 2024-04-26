package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

public final class DBCSetAura extends AbstractPacket {
    public static final String packetName = "NPCDBC|SetAura";
    private int auraID;

    public DBCSetAura(int auraID) {
        this.auraID = auraID;
    }

    public DBCSetAura() {}

    @Override
    public String getChannel() {
        return packetName;
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
