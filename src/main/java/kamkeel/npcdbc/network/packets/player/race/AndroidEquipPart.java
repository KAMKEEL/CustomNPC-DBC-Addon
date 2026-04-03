package kamkeel.npcdbc.network.packets.player.race;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.races.android.AndroidPartSlot;
import kamkeel.npcdbc.data.race.races.android.AndroidPartType;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.LogWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Client -> Server packet sent when the player equips or unequips
 * an android part in the HUDAndroidParts GUI.
 * <p>
 * An empty partId means unequip — the slot will be cleared.
 * Only processed if the player's currentRaceKey is "npcdbc:android".
 */
public final class AndroidEquipPart extends AbstractPacket {

    private AndroidPartSlot slot;
    private String partId; // empty = unequip

    public AndroidEquipPart(AndroidPartSlot slot, AndroidPartType part) {
        this.slot = slot;
        this.partId = part != null ? part.getId() : "";
    }

    public AndroidEquipPart() {}

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AndroidPartSelect;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(slot.ordinal());

        byte[] bytes = partId.getBytes(StandardCharsets.UTF_8);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int slotOrdinal = in.readInt();
        int len = in.readInt();
        byte[] bytes = new byte[len];
        in.readBytes(bytes);
        partId = new String(bytes, StandardCharsets.UTF_8);

        AndroidPartSlot[] slots = AndroidPartSlot.values();
        if (slotOrdinal < 0 || slotOrdinal >= slots.length) {
            LogWriter.error("[NPCDBC] Player " + player.getCommandSenderName()
                    + " sent invalid android slot ordinal: " + slotOrdinal);
            return;
        }

        slot = slots[slotOrdinal];

        if (!slot.isPhysical()) {
            LogWriter.error("[NPCDBC] Player " + player.getCommandSenderName()
                + " sent non-physical android slot: " + slot);
            return;
        }

        DBCData data = DBCData.get(player);
        if (!"android".equals(data.currentRaceKey)) {
            LogWriter.error("[NPCDBC] Player " + player.getCommandSenderName()
                    + " tried to equip android part but is not android");
            return;
        }

        if (!partId.isEmpty()) {
            AndroidPartType type = AndroidPartType.byId(partId);
            if (type == null) {
                LogWriter.error("[NPCDBC] Player " + player.getCommandSenderName()
                        + " sent unknown android part id: " + partId);
                return;
            }
            if (!type.fitsSlot(slot)) {
                LogWriter.error("[NPCDBC] Player " + player.getCommandSenderName()
                        + " tried to equip part " + partId + " in wrong slot: " + slot);
                return;
            }
            data.androidParts.equip(slot, type);
        } else {
            data.androidParts.unequip(slot);
        }

        data.saveNBTData(true);
    }
}
