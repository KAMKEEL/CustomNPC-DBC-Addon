package kamkeel.npcdbc.network.packets.request.outline;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCAURA;

public class DBCCloneOutline extends AbstractPacket {
    public static final String packetName = "NPC|CloneOutline";

    private int id;

    public DBCCloneOutline() {
    }

    public DBCCloneOutline(int id) {
        this.id = id;
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.OutlineClone;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.id);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        int outlineId = in.readInt();
        Outline clone = OutlineController.getInstance().cloneOutline(outlineId);
        if (clone != null) {
            NetworkUtility.sendCustomOutlineDataAll((EntityPlayerMP) player);
            GuiDataPacket.sendGuiData((EntityPlayerMP) player, clone.writeToNBT());
        }
    }
}
