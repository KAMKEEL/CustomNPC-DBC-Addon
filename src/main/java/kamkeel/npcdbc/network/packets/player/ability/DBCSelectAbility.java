package kamkeel.npcdbc.network.packets.player.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.AbilityWheelData;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcs.controllers.AbilityController;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.ChainedAbility;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

/**
 * Packet sent from client to server when player selects an ability from the wheel.
 * Sends the ability KEY string so the server can resolve the correct index.
 */
public final class DBCSelectAbility extends AbstractPacket {
    public static final String packetName = "NPC|SelectAbility";
    private String abilityKey;

    public DBCSelectAbility() {
    }

    public DBCSelectAbility(String abilityKey) {
        this.abilityKey = abilityKey;
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
        ByteBufUtils.writeString(out, abilityKey != null ? abilityKey : "");
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        String key = ByteBufUtils.readString(in);

        PlayerData data = PlayerDataController.Instance.getPlayerData(player);
        if (data == null || data.abilityData == null) return;

        if (key != null && !key.isEmpty()) {
            // Find index in unlocked list and set as selected
            int index = data.abilityData.getUnlockedAbilityList().indexOf(key);
            if (index >= 0) {
                data.abilityData.setSelectedIndex(index);
            }

            // Send selection message to the player
            String displayName = key;
            if (AbilityController.Instance != null) {
                if (key.startsWith(AbilityWheelData.CHAIN_PREFIX)) {
                    ChainedAbility chain = AbilityController.Instance.resolveChainedAbility(
                        key.substring(AbilityWheelData.CHAIN_PREFIX.length()));
                    if (chain != null) displayName = chain.getDisplayName();
                } else {
                    Ability ability = AbilityController.Instance.resolveAbility(key);
                    if (ability != null) displayName = ability.getDisplayName();
                }
            }
            NetworkUtility.sendServerMessage(player, "\u00A7a", "npcdbc.abilitySelect", " ", displayName);
        } else {
            // Clearing selection
            NetworkUtility.sendServerMessage(player, "\u00A79", "npcdbc.clearedSelection");
        }
    }
}
