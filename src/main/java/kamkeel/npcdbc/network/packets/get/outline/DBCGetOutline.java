package kamkeel.npcdbc.network.packets.get.outline;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketGet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public final class DBCGetOutline extends AbstractPacket {
    public static final String packetName = "NPC|GetOutline";
    private int outlineID;

    public DBCGetOutline(int outlineID) {
        this.outlineID = outlineID;
    }

    public DBCGetOutline() {
    }

    @Override
    public Enum getType() {
        return EnumPacketGet.Outline;
    }

    @Override
    public PacketChannel getChannel() {
        return PacketHandler.GET_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.outlineID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int outlineID = in.readInt();
        NBTTagCompound compound = new NBTTagCompound();
        if (outlineID != -1 && OutlineController.getInstance().has(outlineID)) {
            Outline outline = (Outline) OutlineController.getInstance().get(outlineID);
            if (outline != null) {
                compound = outline.writeToNBT();
                compound.setString("Type", "ViewOutline");
            }
        }
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }
}
