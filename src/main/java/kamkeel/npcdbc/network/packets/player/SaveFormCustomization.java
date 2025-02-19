package kamkeel.npcdbc.network.packets.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class SaveFormCustomization extends AbstractPacket {
    public static final String packetName = "DBC|SaveFormCustomization";

    private int formID;
    private NBTTagCompound bodyColorsCompound;

    public SaveFormCustomization() {

    }
    public SaveFormCustomization(Form form, FormDisplay.BodyColor colors) {
        formID = form.id;
        bodyColorsCompound = colors.writeToNBT(new NBTTagCompound());
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.FormSaveCustiomization;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(formID);
        ByteBufUtils.writeNBT(out, bodyColorsCompound);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int formID = in.readInt();
        FormDisplay.BodyColor colors = new FormDisplay.BodyColor();
        colors.readFromNBT(ByteBufUtils.readNBT(in));

        Form form = (Form) FormController.Instance.get(formID);
        if (form == null)
            return;

        DBCData dbcData = DBCData.get(player);
        PlayerDBCInfo dbcInfo = dbcData.getDBCInfo();
        if (form == dbcInfo.getCurrentForm() || dbcInfo.hasForm(form))
            dbcInfo.setFormColorConfig(form, colors);
    }
}
