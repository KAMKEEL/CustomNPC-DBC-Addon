package kamkeel.npcdbc.network.packets.request.outline;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCAURA;

public class DBCSaveOutline extends AbstractPacket {
    public static final String packetName = "NPC|SaveOutline";

    private String prevName;
    private NBTTagCompound outline;

    public DBCSaveOutline(NBTTagCompound compound, String prev) {
        this.outline = compound;
        this.prevName = prev;
    }

    public DBCSaveOutline() {

    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.OutlineSave;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeString(out, prevName);
        ByteBufUtils.writeNBT(out, outline);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        String prevName = ByteBufUtils.readString(in);

        Outline outline = new Outline();
        outline.readFromNBT(ByteBufUtils.readNBT(in));

        OutlineController.getInstance().saveOutline(outline);

        if (!prevName.isEmpty() && !prevName.equals(outline.name)) {
            OutlineController.getInstance().deleteOutlineFile(prevName);
        }

        NetworkUtility.sendCustomOutlineDataAll((EntityPlayerMP) player);
    }
}
