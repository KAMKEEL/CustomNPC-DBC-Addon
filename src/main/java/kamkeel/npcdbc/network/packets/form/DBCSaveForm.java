package kamkeel.npcdbc.network.packets.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.Server;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCFORM;

public class DBCSaveForm extends AbstractPacket {
    public static final String packetName = "NPC|SaveForm";

    private String prevName;
    private NBTTagCompound form;

    public DBCSaveForm(NBTTagCompound compound, String prevName){
        this.form = compound;
        this.prevName = prevName;
    }

    public DBCSaveForm() {

    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeString(out, prevName);
        ByteBufUtils.writeNBT(out, form);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCFORM))
            return;
        String prevName = ByteBufUtils.readString(in);

        Form form = new Form();
        form.readFromNBT(ByteBufUtils.readNBT(in));


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
            if(newParentForm != -1)
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
        if(!prevName.isEmpty() && !prevName.equals(form.name)){
            FormController.getInstance().deleteFormFile(prevName);
        }

        NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
    }
}
