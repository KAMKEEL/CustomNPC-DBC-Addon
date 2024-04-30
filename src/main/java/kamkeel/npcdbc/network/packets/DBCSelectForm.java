package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
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
    public static final String packetName = "NPC|SelectForm";
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
        NBTTagCompound compound = new NBTTagCompound();
        if (formID != -1 && FormController.getInstance().has(formID)){
            Form form = (Form) FormController.getInstance().get(formID);
            if(form != null && formData.hasFormUnlocked(formID)){
                if(form.hasParent()){
                    if(ConfigDBCGameplay.InstantTransform){
                        boolean canInstant = form.mastery.canInstantTransform(formData.getFormLevel(formID));
                        if(!canInstant){
                            Utility.sendMessage(player, "§cYou do not have enough mastery to transform directly");
                            compound.setBoolean("Skip", true);
                            Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
                            return;
                        }
                    } else {
                        Utility.sendMessage(player, "§cYou cannot transform to this form directly");
                        compound.setBoolean("Skip", true);
                        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
                        return;
                    }
                }

                formData.selectedForm = formID;
                Utility.sendMessage(player, String.format("§aForm %s §aSelected", form.getMenuName()));
                compound = form.writeToNBT();
            }
        } else {
            formData.selectedForm = -1;
            Utility.sendMessage(player, "§cCleared selection");
        }

        formData.updateClient();
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
        DBCData.get(player).saveNBTData(true);
    }
}
