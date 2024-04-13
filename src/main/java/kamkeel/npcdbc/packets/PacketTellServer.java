package kamkeel.npcdbc.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.packets.AbstractMessage.AbstractServerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class PacketTellServer extends AbstractServerMessage<PacketTellServer> {
    private String idString;

    public PacketTellServer() {
    }

    public PacketTellServer(String info) {
        idString = info;
    }

    @Override
    public void process(EntityPlayer p, Side side) {
        NBTTagCompound nbt = p.getEntityData().getCompoundTag("PlayerPersisted");
        goThroughStringIDs(idString, nbt, (EntityPlayerMP) p);


    }

    public void goThroughStringIDs(String idString, NBTTagCompound nbt, EntityPlayerMP p) {
        if (idString.contains(":")) {
            if (idString.startsWith("Transform:")) {
            }
        }
        if (idString.startsWith("createForm")){
            System.out.println("yee");
        }


    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        idString = ByteBufUtils.readUTF8String(buffer);

    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        ByteBufUtils.writeUTF8String(buffer, idString);
    }

}
