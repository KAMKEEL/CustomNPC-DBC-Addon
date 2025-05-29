package kamkeel.npcdbc.network.packets.player.outline;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public final class DBCRequestOutline extends AbstractPacket {
    public static final String packetName = "NPC|RequestOutline";
    private int outlineID;

    public DBCRequestOutline(int outlineID) {
        this.outlineID = outlineID;
    }

    public DBCRequestOutline() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.OutlineList;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.outlineID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        this.outlineID = in.readInt();
        if (outlineID != -1) {
            Outline customOutline = (Outline) OutlineController.getInstance().get(outlineID);
            if (customOutline != null) {
                NBTTagCompound compound = customOutline.writeToNBT();
                compound.setString("PACKETTYPE", "Outline");
                GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
            }
        } else {
            NetworkUtility.sendCustomOutlineDataAll((EntityPlayerMP) player);
        }
    }
}
