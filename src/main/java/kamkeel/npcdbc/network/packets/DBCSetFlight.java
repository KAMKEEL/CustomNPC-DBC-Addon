package kamkeel.npcdbc.network.packets;

import JinRyuu.DragonBC.common.DBCClient;
import JinRyuu.DragonBC.common.DBCKiTech;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;

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
    public String getChannel() {
        return packetName;
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
