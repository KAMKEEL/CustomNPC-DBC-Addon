package kamkeel.npcdbc.network.packets.gui;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.Server;

import java.io.IOException;

public class SaveForm extends AbstractPacket {
    public static final String packetName = "NPC|SaveForm";
    private int parentForm = -1;
    private int childForm = -1;

    private Form form = new Form();

    public SaveForm(Form customForm, int parentID, int childID){
        this.form = customForm;
        this.parentForm = parentID;
        this.childForm = childID;
    }

    public SaveForm() {

    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        Server.writeNBT(out, form.writeToNBT());
        out.writeInt(parentForm);
        out.writeInt(childForm);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        //@TODO check permissions

        Form form = new Form();
        form.readFromNBT(Server.readNBT(in));
        form = (Form) FormController.getInstance().saveForm(form);

        parentForm = in.readInt();
        if(parentForm == -1)
            form.removeParentForm();
        else
            form.linkParent(parentForm);

        childForm = in.readInt();
        if(childForm == -1)
            form.removeChildForm();
        else
            form.linkChild(childForm);

        System.out.printf("Saved form %s (%s, %s)\n", form.name, parentForm>=0, childForm>=0);
        NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);

    }
}
