package kamkeel.npcdbc.network.packets.player;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.LogWriter;

import java.io.IOException;

/**
 * Client -> Server packet sent during character creator finalize
 * when the player selected a custom addon race.
 * <p>
 * The vanilla DBC finalize has already been clamped to Human (race 0)
 * by the mixin, so DBC sees a valid vanilla race. This packet carries
 * the real custom race ID so the server can persist it in addon data.
 * <p>
 * A raceID of -1 means the player selected a vanilla race and any
 * existing addon race should be cleared.
 */
public final class DBCRaceSelect extends AbstractPacket {

    private int raceID;

    /** Parameterized constructor — used client-side when sending. */
    public DBCRaceSelect(int raceID) {
        this.raceID = raceID;
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
        out.writeInt(this.raceID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        raceID = in.readInt();

        // Validate: either -1 (clear) or a registered custom race
        if (raceID != -1 && !RaceController.getInstance().has(raceID)) {
            LogWriter.error("[NPCDBC] Player " + player.getCommandSenderName()
                    + " sent invalid addon race ID: " + raceID);
            return;
        }
        
        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
        info.currentRace = raceID;
        info.updateClient();
    }
}
