package kamkeel.npcdbc.network.packets.player;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

import static noppes.npcs.NoppesUtilServer.getPlayerByName;

public final class AbilityUsePacket extends AbstractPacket {
    public static final String packetName = "NPC|AbilityUse";
    private EntityPlayer player;
    private int abilityId;

    public AbilityUsePacket(EntityPlayer player, int abilityId) {
        this.player = player;
        this.abilityId = abilityId;
    }

    public AbilityUsePacket() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AbilityUse;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeUTF8String(out, this.player.getCommandSenderName());
        out.writeInt(this.abilityId);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        String playerName = ByteBufUtils.readUTF8String(in);
        EntityPlayer sendingPlayer = getPlayerByName(playerName);
        if (sendingPlayer == null)
            return;

        int abilityId = in.readInt();

        if (AbilityController.Instance.has(abilityId)) {
            Ability ability = AbilityController.getInstance().get(abilityId);

            if (PlayerDataUtil.getDBCInfo(player).hasAbilityUnlocked(abilityId)) {
                if (ability.abilityData.getType() == Ability.Type.Active) {
                    ability.abilityData.onActivate(player);
                } else if (ability.abilityData.getType() == Ability.Type.Toggle) {
                    ability.abilityData.onToggle(player);
                }
            }
        }
    }
}
