package kamkeel.npcdbc.network.packets.player.race;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcs.util.ByteBufUtils;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.properties.RaceProperty;
import kamkeel.npcdbc.data.race.properties.RacePropertyData;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.LogWriter;

import java.io.IOException;
import java.util.Map;

/**
 * Client -> Server packet sent during character creator finalize
 * to persist the player's custom race property selections.
 * <p>
 * Carries the full property map as NBT. Serialization is fully delegated
 * to each {@link RaceProperty} — no type-switching. The server validates
 * each value against the race's property definitions before applying.
 */
public final class DBCSaveRaceProperties extends AbstractPacket {

    private NBTTagCompound payload;

    public DBCSaveRaceProperties(String raceKey, Map<String, Object> racePropertyValues) {
        this.payload = serializeValues(raceKey, racePropertyValues);
    }

    public DBCSaveRaceProperties() {}

    @Override
    public Enum getType() {
        return EnumPacketPlayer.RacePropertiesSave;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeBigNBT(out, payload);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        payload = ByteBufUtils.readBigNBT(in);

        DBCData data = DBCData.get(player);
        Race race = data.addonRace.getRace();
        if (race == null) {
            LogWriter.error("[NPCDBC] Player " + player.getCommandSenderName()
                + " sent race properties but has no custom race.");
            return;
        }

        RacePropertyData propertyData = data.addonRace.properties;
        propertyData.initDefaults(race);
        applyValidated(propertyData, race, payload);

        PlayerDataUtil.getDBCInfo(player).updateClient();
        data.saveNBTData(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static NBTTagCompound serializeValues(String raceKey, Map<String, Object> values) {
        DataCompound data = DataCompound.create();
        Race race = RaceController.getInstance().getByName(raceKey);
        if (race == null) {
            LogWriter.error("[NPCDBC] Race '" + raceKey + "' not found.");
            return data.toNbt();
        }

        for (RaceProperty<?> property : race.properties.getAll()) {
            writeProperty(data, property, values.get(property.key));
        }
        return data.toNbt();
    }

    @SuppressWarnings("unchecked")
    private static <T> void writeProperty(DataCompound data, RaceProperty<T> property, Object value) {
        T typed = value != null ? (T) value : property.getDefault();
        property.write(data, typed);
    }

    @SuppressWarnings("unchecked")
    private static void applyValidated(RacePropertyData propertyData, Race race, NBTTagCompound nbt) {
        DataCompound data = DataCompound.ofNbt(nbt);

        for (RaceProperty<?> property : race.properties.getAll()) {
            if (!data.has(property.key)) continue;

            RaceProperty<Object> prop = (RaceProperty<Object>) property;
            Object value = prop.read(data);

            if (prop.isValid(value)) {
                propertyData.set(prop, value);
            } else {
                LogWriter.error("[NPCDBC] Invalid value for race property '" + property.key + "' — using default.");
            }
        }
    }
}
