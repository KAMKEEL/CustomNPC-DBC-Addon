package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCClass;
import kamkeel.npcdbc.controllers.CapsuleController;
import kamkeel.npcdbc.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;
import java.util.HashMap;

public final class ChargingDexInfo extends AbstractPacket {
    public static final String packetName = "NPCDBC|ChargingDexInfo";
    private float ma = 0;
    private float spi = 0;
    private float war = 0;
    private boolean enabled = false;

    public ChargingDexInfo(){
        this.enabled = ConfigDBCGameplay.EnableChargingDex;
        this.ma = ConfigDBCGameplay.MartialArtistCharge;
        this.spi = ConfigDBCGameplay.SpiritualistCharge;
        this.war = ConfigDBCGameplay.WarriorCharge;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeBoolean(this.enabled);
        out.writeFloat(this.ma);
        out.writeFloat(this.spi);
        out.writeFloat(this.war);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(player.worldObj.isRemote){
            ClientCache.hasChargingDex = in.readBoolean();
            float martialArtist = in.readFloat();
            float spiritualist = in.readFloat();
            float warrior = in.readFloat();

            ClientCache.chargingDexValues.put(DBCClass.MartialArtist, martialArtist);
            ClientCache.chargingDexValues.put(DBCClass.Spiritualist, spiritualist);
            ClientCache.chargingDexValues.put(DBCClass.Warrior, warrior);
        }
    }
}
