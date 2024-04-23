package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.constants.enums.EnumNBTType;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.ByteBufUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

import static noppes.npcs.NoppesUtilServer.getPlayerByName;

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
        PlayerDBCInfo formData = Utility.getFormData(playerData);
        formData.selectedForm = -1;
        if (formID != -1 && FormController.getInstance().has(formID)){
            if(formData.hasUnlocked(formID)){
                formData.selectedForm = formID;
                Utility.sendMessage(player, String.format("§cForm %s §cSelected", FormController.getInstance().get(formID).getMenuName()));
            }
        } else {
            Utility.sendMessage(player, "§Cleared form selection");
        }
        formData.updateClient();
    }
}
