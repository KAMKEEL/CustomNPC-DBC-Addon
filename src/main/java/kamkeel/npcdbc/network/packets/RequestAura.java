package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;

import java.io.IOException;

public final class RequestAura extends AbstractPacket {
    public static final String packetName = "NPCDBC|RequestAura";
    private int auraID;
    private boolean onlyPlayers;

    public RequestAura(int auraID, boolean players) {
        this.auraID = auraID;
        this.onlyPlayers = players;
    }

    public RequestAura() {}

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.auraID);
        out.writeBoolean(this.onlyPlayers);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        this.auraID = in.readInt();
        this.onlyPlayers = in.readBoolean();
        if(auraID != -1){
            Aura aura = (Aura) AuraController.getInstance().get(auraID);
            if(aura != null){
                NBTTagCompound compound = aura.writeToNBT();
                compound.setString("PACKETTYPE", "Aura");
                Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
            }
        }
        else if(onlyPlayers){
            Utility.sendPlayerAuraData((EntityPlayerMP) player);
        }
        else {
            NetworkUtility.sendCustomAuraDataAll((EntityPlayerMP) player);
        }
    }
}
