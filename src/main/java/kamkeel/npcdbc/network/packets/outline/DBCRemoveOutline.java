package kamkeel.npcdbc.network.packets.outline;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCAURA;

public class DBCRemoveOutline extends AbstractPacket {
    public static final String packetName = "NPC|RemOutline";

    private int outlineID;

    public DBCRemoveOutline(int outlineID){
        this.outlineID = outlineID;
    }

    public DBCRemoveOutline() {

    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.outlineID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        int outlineID = in.readInt();
        OutlineController.getInstance().delete(outlineID);
        NetworkUtility.sendCustomOutlineDataAll((EntityPlayerMP) player);
        NBTTagCompound compound = (new Outline()).writeToNBT();
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
    }
}
