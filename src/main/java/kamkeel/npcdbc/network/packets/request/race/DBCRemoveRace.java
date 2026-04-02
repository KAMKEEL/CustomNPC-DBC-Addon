package kamkeel.npcdbc.network.packets.request.race;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCRACE;

public class DBCRemoveRace extends AbstractPacket {
    public static final String packetName = "NPC|RemRace";

    private String raceName;

    public DBCRemoveRace(String raceName) {
        this.raceName = raceName;
    }

    public DBCRemoveRace() {
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.RaceRemove;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeString(out, raceName);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCRACE))
            return;

        String raceName = ByteBufUtils.readString(in);
        RaceController.getInstance().delete(raceName);
        NetworkUtility.sendCustomRaceDataAll((EntityPlayerMP) player);
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, new NBTTagCompound());
    }
}
