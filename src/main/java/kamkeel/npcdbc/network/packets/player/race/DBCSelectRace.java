package kamkeel.npcdbc.network.packets.player.race;

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
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.LogWriter;

import java.io.IOException;

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
public final class DBCSelectRace extends AbstractPacket {

    private String raceKey;

    /** String-key constructor — preferred for new callers. */
    public DBCSelectRace(String raceKey) {
        this.raceKey = raceKey;
    }

    /** No-arg constructor — required for packet registration. */
    public DBCSelectRace() {
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
        ByteBufUtils.writeUTF8String(out, raceKey != null ? raceKey : "");
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        raceKey = ByteBufUtils.readUTF8String(in);
        if (raceKey == null || raceKey.isEmpty()) raceKey = null;

        // Validate: either null (clear) or a registered custom race name
        if (raceKey != null && !RaceController.getInstance().hasName(raceKey)) {
            LogWriter.error("[NPCDBC] Player " + player.getCommandSenderName()
                    + " sent invalid addon race key: " + raceKey);
            return;
        }

        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
        DBCData data = DBCData.get(player);
        if (info == null || data == null)
            return;

        // Only valid while the character is still being created. DBC gates every one of its
        // own creation writes on jrmcAccept == 0 (JRMCorePacHanS.handleChar), and all three
        // send sites for this packet are in the creator, so mirror that gate here — without
        // it any client could swap race at will and skip DBC's race-change flow entirely.
        if (data.Accept != 0) {
            LogWriter.error("[NPCDBC] Player " + player.getCommandSenderName()
                    + " sent a race selection outside character creation; ignoring.");
            return;
        }

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
