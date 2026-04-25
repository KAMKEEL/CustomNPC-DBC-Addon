package kamkeel.npcdbc.network.packets.request.overlaychain;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.OverlayChainController;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCOVERLAY_CHAIN;

public class OverlayChainSavePacket extends AbstractPacket {
    public static final String packetName = "NPC|SaveOverlayChain";

    private String prevName;
    private NBTTagCompound overlayData;

    public OverlayChainSavePacket() {
    }

    public OverlayChainSavePacket(NBTTagCompound overlayData, String prevName) {
        this.overlayData = overlayData;
        this.prevName = prevName != null ? prevName : "";
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.OverlayChainSave;
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
        ByteBufUtils.writeString(out, prevName);
        ByteBufUtils.writeBigNBT(out, overlayData);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCOVERLAY_CHAIN))
            return;

        String prev = ByteBufUtils.readString(in);
        NBTTagCompound nbt = ByteBufUtils.readBigNBT(in);

        OverlayChain overlay = new OverlayChain();
        overlay.deserialize(DataCompound.ofNbt(nbt));
        OverlayChainController.getInstance().save(overlay);

        if (!prev.isEmpty() && !prev.equals(overlay.key))
            OverlayChainController.getInstance().delete(prev);
    }
}
