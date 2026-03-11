package kamkeel.npcdbc.network.packets.request.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCFORM;

public class DBCCloneForm extends AbstractPacket {
    public static final String packetName = "NPC|CloneForm";

    private int id;

    public DBCCloneForm() {
    }

    public DBCCloneForm(int id) {
        this.id = id;
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.FormClone;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.id);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCFORM))
            return;

        int formId = in.readInt();
        Form clone = FormController.getInstance().cloneForm(formId);
        if (clone != null) {
            NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
            GuiDataPacket.sendGuiData((EntityPlayerMP) player, clone.writeToNBT());
        }
    }
}
