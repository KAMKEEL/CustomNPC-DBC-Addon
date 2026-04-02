package kamkeel.npcdbc.network.packets.request.race;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.race.Race;
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

public class DBCSaveRace extends AbstractPacket {
    public static final String packetName = "NPC|SaveRace";

    private String prevName;
    private NBTTagCompound raceData;

    public DBCSaveRace(NBTTagCompound compound, String prevName) {
        this.raceData = compound;
        this.prevName = prevName;
    }

    public DBCSaveRace() {
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.RaceSave;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeString(out, prevName);
        ByteBufUtils.writeBigNBT(out, raceData);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCRACE))
            return;

        String prevName = ByteBufUtils.readString(in);
        NBTTagCompound nbt = ByteBufUtils.readBigNBT(in);

        Race race = new Race();
        race.readFromNBT(nbt);

        if (!prevName.isEmpty() && !prevName.equals(race.getName())) {
            RaceController.getInstance().deleteRaceFile(race);
        }

        RaceController.getInstance().save(race);

        NetworkUtility.sendCustomRaceDataAll((EntityPlayerMP) player);
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, race.writeToNBT());
    }
}
