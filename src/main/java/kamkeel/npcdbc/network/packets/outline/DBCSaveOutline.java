package kamkeel.npcdbc.network.packets.outline;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.Server;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCAURA;

public class DBCSaveOutline extends AbstractPacket {
    public static final String packetName = "NPC|SaveOutline";

    private String prevName;
    private NBTTagCompound outline;

    public DBCSaveOutline(NBTTagCompound compound, String prev){
        this.outline = compound;
        this.prevName = prev;
    }

    public DBCSaveOutline() {

    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        Server.writeString(out, prevName);
        Server.writeNBT(out, outline);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        String prevName = Server.readString(in);
        if(!prevName.isEmpty()){
            OutlineController.getInstance().deleteOutlineFile(prevName);
        }
        Outline outline = new Outline();
        outline.readFromNBT(Server.readNBT(in));
        OutlineController.getInstance().saveOutline(outline);
        NetworkUtility.sendCustomOutlineDataAll((EntityPlayerMP) player);
    }
}
