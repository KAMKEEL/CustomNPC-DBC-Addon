package kamkeel.npcdbc.network.packets.player.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.ability.toggle.DBCToggleAbility;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.AbilityController;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

/**
 * Packet sent from client to server when player activates a toggle ability from the wheel.
 * Bypasses the full ability execution system - directly calls onToggle with no cooldown or events.
 */
public final class DBCToggleAbilityAction extends AbstractPacket {
    public static final String packetName = "NPC|ToggleAbility";
    private String abilityKey;

    public DBCToggleAbilityAction() {}

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

        Ability ability = AbilityController.Instance.resolveAbility(key);
        if (!(ability instanceof DBCToggleAbility)) return;

        DBCToggleAbility toggle = (DBCToggleAbility) ability;
        toggle.toggle(player);

        boolean nowActive = toggle.isActive(player);
        String displayName = ability.getName() != null ? ability.getName() : key;
        if (nowActive) {
            NetworkUtility.sendServerMessage(player, "\u00A7a", displayName, " ", "gui.enabled");
        } else {
            NetworkUtility.sendServerMessage(player, "\u00A7c", displayName, " ", "gui.disabled");
        }
    }
}
