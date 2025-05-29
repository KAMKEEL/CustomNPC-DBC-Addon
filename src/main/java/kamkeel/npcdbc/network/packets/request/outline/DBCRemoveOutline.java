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
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCAURA;

;

public class DBCRemoveOutline extends AbstractPacket {
    public static final String packetName = "NPC|RemOutline";

    private int outlineID;

    public DBCRemoveOutline(int outlineID) {
        this.outlineID = outlineID;
    }

    public DBCRemoveOutline() {

    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.OutlineRemove;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.outlineID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        int outlineID = in.readInt();
        OutlineController.getInstance().delete(outlineID);
        NetworkUtility.sendCustomOutlineDataAll((EntityPlayerMP) player);
        NBTTagCompound compound = (new Outline()).writeToNBT();
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
    }
}
