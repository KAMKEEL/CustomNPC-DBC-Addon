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
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCAURA;

public class DBCCloneAura extends AbstractPacket {
    public static final String packetName = "NPC|CloneAura";

    private int id;

    public DBCCloneAura() {
    }

    public DBCCloneAura(int id) {
        this.id = id;
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.AuraClone;
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
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        int auraId = in.readInt();
        Aura clone = AuraController.getInstance().cloneAura(auraId);
        if (clone != null) {
            NetworkUtility.sendCustomAuraDataAll((EntityPlayerMP) player);
            GuiDataPacket.sendGuiData((EntityPlayerMP) player, clone.writeToNBT());
        }
    }
}
