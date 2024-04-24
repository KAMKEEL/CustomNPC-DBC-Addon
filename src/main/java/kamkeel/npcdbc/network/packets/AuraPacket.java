package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.ByteBufUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

import static noppes.npcs.NoppesUtilServer.getPlayerByName;

public final class AuraPacket extends AbstractPacket {
    public static final String packetName = "NPCDBC|Aura";
    private EntityPlayer player;
    private int auraID;
    private boolean enable;

    public AuraPacket(EntityPlayer player, int state, boolean enable) {
        this.player = player;
        this.auraID = state;
        this.enable = enable;
    }

    public AuraPacket() {
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeUTF8String(out, this.player.getCommandSenderName());
        out.writeInt(this.auraID);
        out.writeBoolean(enable);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        String playerName = ByteBufUtils.readUTF8String(in);
        EntityPlayer sendingPlayer = getPlayerByName(playerName);
        if (sendingPlayer == null)
            return;

        int auraID = in.readInt();
        boolean enable = in.readBoolean();

        DBCData dbcData = DBCData.get(player);
        PlayerDBCInfo formData = Utility.getData(player);

        if (enable) {
            formData.currentAura = auraID;
            Utility.sendMessage(player, "§aToggled on§r " + formData.getAura(auraID).getMenuName());
        } else {
            formData.currentAura = -1;
            Utility.sendMessage(player, "§cToggled off§r " + formData.getAura(auraID).getMenuName());
        }

        formData.updateClient();
        dbcData.saveNBTData();

    }
}
