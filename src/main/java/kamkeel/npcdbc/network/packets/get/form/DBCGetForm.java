package kamkeel.npcdbc.network.packets.get.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketGet;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public final class DBCGetForm extends AbstractPacket {
    public static final String packetName = "NPC|GetForm";
    private int formID;

    public DBCGetForm(int formID) {
        this.formID = formID;
    }

    public DBCGetForm() {}

    @Override
    public Enum getType() {
        return EnumPacketGet.Form;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.GET_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.formID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int formID = in.readInt();
        NBTTagCompound compound = new NBTTagCompound();
        if (formID != -1 && FormController.getInstance().has(formID)){
            Form form = (Form) FormController.getInstance().get(formID);
            if(form != null){
                compound = form.writeToNBT();
                compound.setString("Type", "ViewForm");
            }
        }
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
    }
}
