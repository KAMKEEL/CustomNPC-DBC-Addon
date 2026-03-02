package kamkeel.npcdbc.network.packets.player;

import JinRyuu.DragonBC.common.DBCKiTech;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public final class DBCSetAllowFlight extends AbstractPacket {
    public static final String packetName = "NPC|SetAllowFlight";
    private boolean allowFlight;

    public DBCSetAllowFlight(boolean allowFlight) {
        this.allowFlight = allowFlight;
    }

    public DBCSetAllowFlight() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AllowFlight;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeBoolean(this.allowFlight);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        allowFlight = in.readBoolean();

        DBCData clientData = DBCData.getClient();
        clientData.flightEnabled = allowFlight;
        clientData.getRawCompound().setBoolean("DBCFlightEnabled", allowFlight);

        // If disabling flight while currently flying, stop flight
        if (!allowFlight && DBCKiTech.floating) {
            DBCKiTech.floating = false;
            clientData.isFlying = false;
        }
    }
}
