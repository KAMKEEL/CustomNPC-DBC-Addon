package kamkeel.npcdbc.network.packets.player.form;

import JinRyuu.JRMCore.JRMCoreH;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCSettings;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

import static kamkeel.npcdbc.constants.DBCForm.GodOfDestruction;
import static kamkeel.npcdbc.constants.DBCForm.Kaioken;
import static kamkeel.npcdbc.constants.DBCForm.Kaioken6;
import static kamkeel.npcdbc.constants.DBCForm.Mystic;
import static kamkeel.npcdbc.constants.DBCForm.UltraInstinct;

public final class DBCSelectForm extends AbstractPacket {
    public static final String packetName = "NPC|SelectForm";

    private int formID;
    private boolean isDBC;
    private String formKey;

    public DBCSelectForm(Form form) {
        this.isDBC = false;
        if (form == null) {
            this.formID = -1;
            this.formKey = "";
        } else {
            this.formID = form.id;
            this.formKey = form.key != null ? form.key.toString() : "";
        }
    }

    public DBCSelectForm(int formID, boolean isDBC) {
        this.formID = formID;
        this.isDBC = isDBC;
        this.formKey = "";
    }

    public DBCSelectForm() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.FormSelect;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(formID);
        out.writeBoolean(isDBC);
        if (!isDBC && formID != -1)
            ByteBufUtils.writeUTF8String(out, formKey);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int formID = in.readInt();
        boolean isDBC = in.readBoolean();
        String formKey = (!isDBC && formID != -1) ? ByteBufUtils.readUTF8String(in) : "";

        PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(playerData);
        NBTTagCompound compound = new NBTTagCompound();

        if (formID == -1) {
            if (formData.selectedDBCForm == -1 && !formData.hasSelectedForm())
                return;
            formData.clearSelectedForm();
            NetworkUtility.sendServerMessage(player, "§9", "npcdbc.clearedSelection");
        } else if (isDBC) {
            if (formID == formData.selectedDBCForm)
                return;

            DBCData dbc = DBCData.get(player);
            int selected = formData.selectedDBCForm = formData.tempSelectedDBCForm = formID;
            formData.clearSelectedForm();
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
            NetworkUtility.sendServerMessage(player, "§a", "npcdbc.formSelect", " ", DBCForm.getMenuName(dbc.Race, formID, dbc.isForm(DBCForm.Divine)));
        } else {
            if (!formKey.isEmpty() && formKey.equals(formData.getSelectedFormKey()))
                return;

            Form form = FormController.getInstance().getFromKey(formKey);
            if (form == null)
                form = (Form) FormController.getInstance().get(formID);

            if (form != null && formData.hasFormUnlocked(form.id)) {
                formData.setSelectedForm(form);
                NetworkUtility.sendServerMessage(player, "§a", "npcdbc.formSelect", " ", form.getMenuName());
                compound = form.writeToNBT();
            }
        }

        formData.updateClient();
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
        DBCData.get(player).saveNBTData(true);
    }
}
