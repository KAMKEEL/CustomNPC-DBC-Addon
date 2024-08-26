package kamkeel.npcdbc.network.packets;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class TurboPacket extends AbstractPacket {
    public static final String packetName = "NPC|ToggleTurbo";

    private final boolean isOn;

    public TurboPacket(){
        this.isOn = false;
    }

    public TurboPacket(boolean on) {
        this.isOn = on;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeBoolean(isOn);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            return;

        DBCData.get(player).setTurboState(in.readBoolean());
    }
}
