package kamkeel.npcdbc.network.packets.request.aura;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCAURA;

public class DBCRemoveAura extends AbstractPacket {
    public static final String packetName = "NPC|RemAura";

    private int auraID;

    public DBCRemoveAura(int auraID){
        this.auraID = auraID;
    }

    public DBCRemoveAura() {

    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.AuraRemove;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.auraID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        int auraID = in.readInt();
        AuraController.getInstance().delete(auraID);
        NetworkUtility.sendCustomAuraDataAll((EntityPlayerMP) player);
        NBTTagCompound compound = (new Aura()).writeToNBT();
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
    }
}
