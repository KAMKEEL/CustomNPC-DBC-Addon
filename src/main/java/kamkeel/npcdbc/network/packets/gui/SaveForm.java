package kamkeel.npcdbc.network.packets.gui;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
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
        int newParentForm = in.readInt();
        int newChildForm = in.readInt();

        if(form.parentID != newParentForm || form.childID != newChildForm){
            IForm parent = FormController.getInstance().get(form.parentID);
            IForm child = FormController.getInstance().get(form.childID);

            if(newParentForm == -1)
                form.removeParentForm();
            else
                form.linkParent(newParentForm);

            if(newChildForm == -1)
                form.removeChildForm();
            else
                form.linkChild(newChildForm);

            if(parent != null)
                parent.save();

            if(child != null)
                child.save();
        }
        form = (Form) FormController.getInstance().saveForm(form);

        System.out.printf("Saved form %s (%s, %s)\n", form.name, parentForm>=0, childForm>=0);
        NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
    }
}
