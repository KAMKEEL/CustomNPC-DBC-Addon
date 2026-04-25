package kamkeel.npcdbc.network.packets.request.overlaychain;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.OverlayChainController;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCOVERLAY_CHAIN;

public class OverlayChainClonePacket extends AbstractPacket {
    public static final String packetName = "NPC|CloneOverlayChain";

    private String overlayKey = "";

    public OverlayChainClonePacket() {
    }

    public OverlayChainClonePacket(String overlayKey) {
        this.overlayKey = overlayKey != null ? overlayKey : "";
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.OverlayChainClone;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission() {
        return GLOBAL_DBCOVERLAY_CHAIN;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeString(out, this.overlayKey);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCOVERLAY_CHAIN))
            return;

        String key = ByteBufUtils.readString(in);
        OverlayChainController.getInstance().clone(key);
    }
}
