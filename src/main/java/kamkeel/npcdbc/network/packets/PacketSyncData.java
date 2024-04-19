package kamkeel.npcdbc.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.data.SyncedData.PerfectSync;
import kamkeel.npcdbc.network.AbstractMessage;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class PacketSyncData extends AbstractMessage<PacketSyncData> {
    private String s;
    private NBTTagCompound data;

    public PacketSyncData() {
    }


    public PacketSyncData(Entity entity, String id, NBTTagCompound data) {
        s = id;
        if (Utility.isServer(entity))
            this.data = data;

    }

    @Override
    public void process(EntityPlayer p, Side side) {
        if (side == Side.SERVER) {
            updateServerData(p);
        } else {
            if (s.contains("DBCData")) {
                String playerName = s.split(":")[1];
                if (!DBCData.has(playerName))
                    DBCData.putCache(playerName, new DBCData(Utility.getFromNameClient(playerName)));
                DBCData.get(playerName).loadNBTData(data);
            }
        }
    }


    public void updateServerData(EntityPlayer p) {
        if (!s.contains("updateServer"))
            return;
        String[] data = s.split(";");
        String dataName = data[1];
        String nbtTag = data[2];
        String tagType = data[3];
        String tagValue = data[4];

        Entity e = s.contains("Entity") ? Utility.getEntityFromID(p.worldObj, s.split(";")[5]) : p;
        NBTTagCompound cmpd = PerfectSync.getCache(e, dataName).compound();

        if (tagType.equals("Int"))
            cmpd.setInteger(nbtTag, Integer.parseInt(tagValue));
        else if (tagType.equals("Float"))
            cmpd.setFloat(nbtTag, Float.parseFloat(tagValue));
        else if (tagType.equals("Double"))
            cmpd.setDouble(nbtTag, Double.parseDouble(tagValue));
        else if (tagType.equals("Long"))
            cmpd.setLong(nbtTag, Long.parseLong(tagValue));
        else if (tagType.equals("Byte"))
            cmpd.setByte(nbtTag, Byte.parseByte(tagValue));
        else if (tagType.equals("Boolean"))
            cmpd.setBoolean(nbtTag, Boolean.parseBoolean(tagValue));
        else if (tagType.equals("String"))
            cmpd.setString(nbtTag, tagValue);

        PerfectSync.getCache(e, dataName).loadFromNBT(false);

    }


    @Override
    public void read(PacketBuffer buffer) throws IOException {
        s = ByteBufUtils.readUTF8String(buffer);
        data = buffer.readNBTTagCompoundFromBuffer();
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        ByteBufUtils.writeUTF8String(buffer, s);
        buffer.writeNBTTagCompoundToBuffer(data);
    }
}
