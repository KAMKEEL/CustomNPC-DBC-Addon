package kamkeel.npcdbc.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.packets.AbstractMessage.AbstractClientMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class PacketTellClient extends AbstractClientMessage<PacketTellClient> {
    private String s;

    public PacketTellClient() {
    }

    public PacketTellClient(EntityPlayer p, String id) {
        s = id;


    }

    @Override
    public void process(EntityPlayer p, Side side) {
        p.worldObj.getEntityByID(0);
        goThroughStringIDs(p, s);

    }

    public void goThroughStringIDs(EntityPlayer p, String idString) {
        if (idString.contains(":")) {
            Boolean value = Boolean.parseBoolean(idString.split(":")[1]);
            if (idString.startsWith("")) {

            }


        }


    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        s = ByteBufUtils.readUTF8String(buffer);

    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        ByteBufUtils.writeUTF8String(buffer, s);


    }

}