package kamkeel.npcdbc.network.packets.player;

import JinRyuu.DragonBC.common.DBCClient;
import JinRyuu.DragonBC.common.DBCKiTech;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import net.minecraft.entity.player.EntityPlayer;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public final class DBCSetFlight extends AbstractPacket {
    public static final String packetName = "NPC|SetFlight";
    private boolean flightOn;

    public DBCSetFlight(boolean flightOn) {
        this.flightOn = flightOn;
    }

    public DBCSetFlight() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.Flight;
    }

    @Override
    public PacketChannel getChannel() {
        return PacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeBoolean(this.flightOn);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        flightOn = in.readBoolean();

        if (flightOn && player.onGround)
            DBCClient.mc.thePlayer.motionY = 0.5;

        DBCKiTech.floating = flightOn;
    }
}
