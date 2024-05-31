package kamkeel.npcdbc.network.packets.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.Server;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCFORM;

public class DBCSaveForm extends AbstractPacket {
    public static final String packetName = "NPC|SaveForm";

    private NBTTagCompound form;

    public DBCSaveForm(NBTTagCompound compound){
        this.form = compound;
    }

    public DBCSaveForm() {

    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        Server.writeNBT(out, form);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCFORM))
            return;

        Form form = new Form();
        form.readFromNBT(Server.readNBT(in));

        int oldParentForm = -1;
        int oldChildForm = -1;

        IForm original = FormController.getInstance().get(form.id);
        if(original != null){
            oldParentForm = original.getParentID();
            oldChildForm = original.getChildID();
        }

        int newParentForm = form.parentID;
        int newChildForm = form.childID;

        if(newParentForm != oldParentForm || newChildForm != oldChildForm){
            form.removeParentForm();
            if(newParentForm == -1)
                form.linkParent(newParentForm);

            form.removeChildForm();
            if(newChildForm != -1)
                form.linkChild(newChildForm);

            IForm parent = FormController.getInstance().get(form.parentID);
            IForm child = FormController.getInstance().get(form.childID);
            IForm oldParent = FormController.getInstance().get(oldParentForm);
            IForm oldChild = FormController.getInstance().get(oldChildForm);

            if(parent != null)
                parent.save();

            if(child != null)
                child.save();

            if(oldParent != null)
                oldParent.save();

            if(oldChild != null)
                oldChild.save();
        }
        FormController.getInstance().saveForm(form);
        NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
    }
}
