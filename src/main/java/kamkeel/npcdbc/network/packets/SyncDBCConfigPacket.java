package kamkeel.npcdbc.network.packets;

import JinRyuu.DragonBC.common.DBCConfig;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class SyncDBCConfigPacket extends AbstractPacket {
    public static final String packetName = "DBC|ConfigFix";
    private double kiProtection;
    private double kiFist;

    public SyncDBCConfigPacket(double kiProtConfig, double kiFistConfig){
        this.kiProtection = kiProtConfig;
        this.kiFist = kiFistConfig;
    }
    public SyncDBCConfigPacket(){

    }
    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeDouble(this.kiProtection);
        out.writeDouble(this.kiFist);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        DBCConfig.cnfKDd = in.readDouble();
        DBCConfig.cnfKFd = in.readDouble();
    }
}
