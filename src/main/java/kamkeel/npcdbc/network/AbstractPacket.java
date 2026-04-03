package kamkeel.npcdbc.network;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.EntityNPCInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public abstract class AbstractPacket {

    public EntityNPCInterface npc;

    public FMLProxyPacket generatePacket() {
        PacketChannel packetChannel = getChannel();
        ByteBuf buf = Unpooled.buffer();
        try {
            buf.writeInt(packetChannel.getChannelType().ordinal());
            buf.writeInt(getType().ordinal());
            sendData(buf);
            return new FMLProxyPacket(buf, packetChannel.getChannelName());
        } catch (Exception e) {
            // For debugging, you might want to log it
            e.printStackTrace();
        }
        return null;
    }

    public List<FMLProxyPacket> generatePackets() {
        FMLProxyPacket single = generatePacket();
        if (single == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(single);
    }

    public abstract Enum getType();

    public abstract PacketChannel getChannel();

    public CustomNpcsPermissions.Permission getPermission() {
        return null;
    }

    public boolean needsNPC() {
        return false;
    }

    public void setNPC(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public abstract void sendData(ByteBuf out) throws IOException;
    
public void sendData1(ByteBuf out) throws IOException {
    
    // Create a temporary buffer to hold compressed data
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
        //gzip.write(jsonBytes);
    }
    byte[] compressed = baos.toByteArray();

    // Write the length of compressed data so the receiver knows how much to read
    out.writeInt(compressed.length);
    out.writeBytes(compressed);
}

    //"player" on the server side is the client who sent this packet
    //"player" on the client side is the client player
    public abstract void receiveData(ByteBuf in, EntityPlayer player) throws IOException;
}
