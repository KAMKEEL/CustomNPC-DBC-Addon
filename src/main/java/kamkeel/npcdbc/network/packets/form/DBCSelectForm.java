package kamkeel.npcdbc.network.packets.form;

import JinRyuu.JRMCore.JRMCoreH;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCSettings;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

import static kamkeel.npcdbc.constants.DBCForm.*;

public final class DBCSelectForm extends AbstractPacket {
    public static final String packetName = "NPC|SelectForm";
    private int formID;
    private boolean isDBC;

    public DBCSelectForm(int formID, boolean isDBC) {
        this.formID = formID;
        this.isDBC = isDBC;
    }

    public DBCSelectForm() {
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.formID);
        out.writeBoolean(this.isDBC);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int formID = in.readInt();
        boolean isDBC = in.readBoolean();
        PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(playerData);
        NBTTagCompound compound = new NBTTagCompound();
        if (formID == -1 && (isDBC ? formData.selectedDBCForm == -1 : formData.selectedForm == -1))
            return;

        if (isDBC && formID != -1) {
            if (formID == formData.selectedDBCForm)
                return;

            DBCData dbc = DBCData.get(player);
            int selected = 0;
            formData.selectedDBCForm = formData.tempSelectedDBCForm = selected = formID;
            formData.selectedForm = -1;
            if (selected == Mystic) {
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.KAIOKEN_ENABLED);
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.ULTRA_INSTINCT);
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.GOD_OF_DESTRUCTION);
                JRMCoreH.PlyrSettingsOn(player, DBCSettings.POTENTIAL_UNLEASHED);
            } else if (selected >= Kaioken && selected <= Kaioken6) {
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.POTENTIAL_UNLEASHED);
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.ULTRA_INSTINCT);
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.GOD_OF_DESTRUCTION);
                JRMCoreH.PlyrSettingsOn(player, DBCSettings.KAIOKEN_ENABLED);
            } else if (selected >= UltraInstinct && selected <= UltraInstinct + 10) {
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.KAIOKEN_ENABLED);
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.POTENTIAL_UNLEASHED);
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.GOD_OF_DESTRUCTION);
                JRMCoreH.PlyrSettingsOn(player, DBCSettings.ULTRA_INSTINCT);
            } else if (selected == GodOfDestruction) {
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.KAIOKEN_ENABLED);
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.POTENTIAL_UNLEASHED);
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.ULTRA_INSTINCT);
                JRMCoreH.PlyrSettingsOn(player, DBCSettings.GOD_OF_DESTRUCTION);
            } else {
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.KAIOKEN_ENABLED);
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.POTENTIAL_UNLEASHED);
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.ULTRA_INSTINCT);
                JRMCoreH.PlyrSettingsRem(player, DBCSettings.GOD_OF_DESTRUCTION);
            }
            if (formID != -1)
                NetworkUtility.sendServerMessage(player, "§a", "npcdbc.formSelect", " ", DBCForm.getMenuName(dbc.Race, formID, dbc.isForm(DBCForm.Divine)));
        } else if (formID != -1 && FormController.getInstance().has(formID)) {
            if (formID == formData.selectedForm)
                return;

            Form form = (Form) FormController.getInstance().get(formID);
            if (form != null && formData.hasFormUnlocked(formID)) {
                formData.selectedForm = formID;
                formData.selectedDBCForm = formData.tempSelectedDBCForm = -1;
                NetworkUtility.sendServerMessage(player, "§a", "npcdbc.formSelect", " ", form.getMenuName());
                compound = form.writeToNBT();
            }
        } else {
            formData.selectedForm = formData.selectedDBCForm = formData.tempSelectedDBCForm = -1;
            NetworkUtility.sendServerMessage(player, "§9", "npcdbc.clearedSelection");
        }


        formData.updateClient();
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
        DBCData.get(player).saveNBTData(true);
    }
}
