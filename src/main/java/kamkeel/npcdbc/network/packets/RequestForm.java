package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;

import java.io.IOException;

public final class RequestForm extends AbstractPacket {
    public static final String packetName = "NPC|RequestForm";
    private int formID;
    private boolean onlyPlayers;

    public RequestForm(int formID, boolean players) {
        this.formID = formID;
        this.onlyPlayers = players;
    }

    public RequestForm() {}

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.formID);
        out.writeBoolean(this.onlyPlayers);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        this.formID = in.readInt();
        this.onlyPlayers = in.readBoolean();
        if(formID != -1){
            Form customForm = (Form) FormController.getInstance().get(formID);
            if(customForm != null){
                NBTTagCompound compound = customForm.writeToNBT();
                compound.setString("PACKETTYPE", "Form");
                Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
            }
        } else if(onlyPlayers){
            NetworkUtility.sendPlayersForms((EntityPlayerMP) player);
        }
        else {
            NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
        }
    }
}
