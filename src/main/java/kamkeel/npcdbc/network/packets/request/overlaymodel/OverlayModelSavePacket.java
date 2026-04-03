package kamkeel.npcdbc.network.packets.request.overlaymodel;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.OverlayModelController;
import kamkeel.npcdbc.data.overlay.ScriptOverlayModel;
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

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCOVERLAY_MODEL;

public class OverlayModelSavePacket extends AbstractPacket {
    public static final String packetName = "NPC|SaveOverlayModel";

    private String prevName;
    private NBTTagCompound modelData;

    public OverlayModelSavePacket() {
    }

    public OverlayModelSavePacket(NBTTagCompound modelData, String prevName) {
        this.modelData = modelData;
        this.prevName = prevName != null ? prevName : "";
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.OverlayModelSave;
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
        ByteBufUtils.writeString(out, prevName);
        ByteBufUtils.writeBigNBT(out, modelData);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCOVERLAY_MODEL))
            return;

        String prev = ByteBufUtils.readString(in);
        NBTTagCompound nbt = ByteBufUtils.readBigNBT(in);

        ScriptOverlayModel model = new ScriptOverlayModel();
        model.deserialize(DataCompound.ofNbt(nbt));
        OverlayModelController.getInstance().save(model);

        if (!prev.isEmpty() && !prev.equals(model.key))
            OverlayModelController.getInstance().delete(prev);
    }
}
