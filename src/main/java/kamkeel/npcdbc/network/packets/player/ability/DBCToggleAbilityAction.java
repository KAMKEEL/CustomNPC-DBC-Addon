package kamkeel.npcdbc.network.packets.player.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcs.controllers.AbilityController;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

/**
 * Packet sent from client to server when player activates a toggle ability from the wheel.
 * Uses the base ability toggle system for state tracking and client sync.
 */
public final class DBCToggleAbilityAction extends AbstractPacket {
    public static final String packetName = "NPC|ToggleAbility";
    private String abilityKey;

    public DBCToggleAbilityAction() {
    }

    public DBCToggleAbilityAction(String abilityKey) {
        this.abilityKey = abilityKey;
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AbilityToggle;
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
        if (key == null || key.isEmpty()) return;
        if (AbilityController.Instance == null) return;

        // Validate ability exists and is toggleable
        Ability ability = AbilityController.Instance.resolveAbility(key);
        if (ability == null || !ability.isToggleable()) return;

        // Use the base toggle system on PlayerAbilityData
        PlayerData playerData = PlayerData.get(player);
        if (playerData == null || playerData.abilityData == null) return;

        boolean nowActive = playerData.abilityData.toggleAbility(key);

        String displayName = ability.getDisplayName();
        if (nowActive) {
            NetworkUtility.sendServerMessage(player, "\u00A7a", displayName, " ", "gui.enabled");
        } else {
            NetworkUtility.sendServerMessage(player, "\u00A7c", displayName, " ", "gui.disabled");
        }
    }
}
