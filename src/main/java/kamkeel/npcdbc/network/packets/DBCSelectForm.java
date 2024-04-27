package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

public final class DBCSelectForm extends AbstractPacket {
    public static final String packetName = "NPCDBC|SelectForm";
    private int formID;

    public DBCSelectForm(int formID) {
        this.formID = formID;
    }

    public DBCSelectForm() {}

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.formID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int formID = in.readInt();
        PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(playerData);
        formData.selectedForm = -1;
        NBTTagCompound compound = new NBTTagCompound();
        if (formID != -1 && FormController.getInstance().has(formID)){
            if(formData.hasFormUnlocked(formID)){
                Form form = (Form) FormController.getInstance().get(formID);
                formData.selectedForm = formID;
                Utility.sendMessage(player, String.format("§aForm %s §aSelected", form.getMenuName()));
                compound = form.writeToNBT();
            }
        } else {
            Utility.sendMessage(player, "§cCleared form selection");
        }

        formData.updateClient();
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
        DBCData.get(player).saveNBTData(true);
    }
}
