package kamkeel.npcdbc.network.packets.player.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.AbilityWheelData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.AbilityController;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

/**
 * Packet sent from client to server when player selects an ability from the wheel.
 * Sets the selected ability index in PlayerAbilityData.
 */
public final class DBCSelectAbility extends AbstractPacket {
    public static final String packetName = "NPC|SelectAbility";
    private int selectedIndex;

    public DBCSelectAbility() {}

    public DBCSelectAbility(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AbilitySelect;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.selectedIndex);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int index = in.readInt();

        PlayerData data = PlayerDataController.Instance.getPlayerData(player);
        if (data == null || data.abilityData == null) return;

        if (index >= 0) {
            data.abilityData.setSelectedIndex(index);

            // Send selection message to the player
            PlayerDBCInfo dbcInfo = PlayerDataUtil.getDBCInfo(data);
            if (dbcInfo != null && index >= 0 && index < dbcInfo.abilityWheel.length) {
                AbilityWheelData wheelData = dbcInfo.abilityWheel[index];
                if (wheelData != null && !wheelData.isEmpty()) {
                    Ability ability = AbilityController.Instance.resolveAbility(wheelData.abilityKey);
                    String displayName = ability != null && ability.getName() != null
                        ? ability.getName() : wheelData.abilityKey;
                    NetworkUtility.sendServerMessage(player, "\u00A7a", "npcdbc.abilitySelect", " ", displayName);
                }
            }
        } else {
            // Clearing selection
            NetworkUtility.sendServerMessage(player, "\u00A79", "npcdbc.clearedSelection");
        }
    }
}
