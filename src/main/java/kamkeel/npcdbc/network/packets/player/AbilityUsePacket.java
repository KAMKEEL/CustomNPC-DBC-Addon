package kamkeel.npcdbc.network.packets.player;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AbilityData;
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
    private int abilityId;
    private boolean isDBC;

    public AbilityUsePacket(int abilityId, boolean isDBC) {
        this.abilityId = abilityId;
        this.isDBC = isDBC;
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
        out.writeInt(this.abilityId);
        out.writeBoolean(this.isDBC);
    }

    // TODO find out why ts not working on multiplayer
    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (player == null)
            return;

        int abilityId = in.readInt();
        boolean isDBC = in.readBoolean();

        Ability ability = AbilityController.getInstance().get(abilityId, isDBC);
        if (ability == null)
            return;

        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
        AbilityData data = isDBC ? info.dbcAbilityData : info.customAbilityData;

        if (data.hasAbilityUnlocked(abilityId) && ability.onUse(player)) {
            if (ability.getType() == Ability.Type.Toggle) {
                if (data.toggledAbilities.contains(abilityId))
                    data.toggledAbilities.remove(abilityId);
                else
                    data.toggledAbilities.add(abilityId);
            }

            info.updateClient();
        }
    }
}
