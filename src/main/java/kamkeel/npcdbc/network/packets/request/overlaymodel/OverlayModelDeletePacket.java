package kamkeel.npcdbc.network.packets.request.overlaymodel;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.OverlayModelController;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCOVERLAY_MODEL;

/**
 * Overlay-model delete packet.
 * <p>
 * Removes a custom overlay model by key through the controller,
 * refreshes the GUI list, and returns an empty/reset compound.
 */
public class OverlayModelDeletePacket extends AbstractPacket {
    public static final String packetName = "NPC|RemOverlayModel";

    private String modelKey = "";

    public OverlayModelDeletePacket() {
    }

    /**
     * @param modelKey the key of the model to delete
     */
    public OverlayModelDeletePacket(String modelKey) {
        this.modelKey = modelKey != null ? modelKey : "";
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.OverlayModelRemove;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission() {
        return GLOBAL_DBCOVERLAY_MODEL;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeString(out, this.modelKey);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCOVERLAY_MODEL))
            return;

        String key = ByteBufUtils.readString(in);
        OverlayModelController.getInstance().delete(key);
    }
}
