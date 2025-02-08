package kamkeel.npcdbc.network.packets.player.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public final class DBCRequestForm extends AbstractPacket {
    public static final String packetName = "NPC|RequestForm";
    private int formID;
    private boolean onlyPlayers;
    private boolean menuName;

    public DBCRequestForm(int formID, boolean players, boolean menuName) {
        this.formID = formID;
        this.onlyPlayers = players;
        this.menuName = menuName;
    }

    public DBCRequestForm() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.FormList;
    }

    @Override
    public PacketChannel getChannel() {
        return PacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.formID);
        out.writeBoolean(this.onlyPlayers);
        out.writeBoolean(this.menuName);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        this.formID = in.readInt();
        this.onlyPlayers = in.readBoolean();
        this.menuName = in.readBoolean();

        if (formID != -1) {
            Form customForm = (Form) FormController.getInstance().get(formID);
            if (customForm != null) {
                NBTTagCompound compound = customForm.writeToNBT();
                compound.setString("PACKETTYPE", "Form");
                Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
            }
        } else if (onlyPlayers) {
            NetworkUtility.sendPlayersForms(player, menuName);
        } else {
            NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
        }
    }
}
