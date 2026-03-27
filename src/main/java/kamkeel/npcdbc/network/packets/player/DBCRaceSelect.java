package kamkeel.npcdbc.network.packets.player;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.LogWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Client -> Server packet sent during character creator finalize
 * when the player selected a custom addon race.
 * <p>
 * The vanilla DBC finalize has already been clamped to Human (race 0)
 * by the mixin, so DBC sees a valid vanilla race. This packet carries
 * the real custom race key so the server can persist it in addon data.
 * <p>
 * An empty raceKey means the player selected a vanilla race and any
 * existing addon race should be cleared.
 */
public final class DBCRaceSelect extends AbstractPacket {

    private String raceKey;

    /** String-key constructor — preferred for new callers. */
    public DBCRaceSelect(String raceKey) {
        this.raceKey = raceKey;
    }

    /** No-arg constructor — required for packet registration. */
    public DBCRaceSelect() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.RaceSelect;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        String toSend = raceKey != null ? raceKey : "";
        byte[] bytes = toSend.getBytes(StandardCharsets.UTF_8);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int len = in.readInt();
        byte[] bytes = new byte[len];
        in.readBytes(bytes);
        raceKey = new String(bytes, StandardCharsets.UTF_8);
        if (raceKey.isEmpty()) raceKey = null;

        // Validate: either null (clear) or a registered custom race name
        if (raceKey != null && !RaceController.getInstance().hasName(raceKey)) {
            LogWriter.error("[NPCDBC] Player " + player.getCommandSenderName()
                    + " sent invalid addon race key: " + raceKey);
            return;
        }

        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
        DBCData data = DBCData.get(player);
        Race race = raceKey != null ? RaceController.getInstance().getByName(raceKey) : null;
        if (race != null) {
            info.setCurrentRace(race);
        } else {
            info.clearCurrentRace();
        }
        info.setSelectedFormBranch(0);
        data.currentRaceKey = raceKey;
        info.updateClient();
    }
}
