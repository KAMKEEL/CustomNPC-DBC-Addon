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
    private EntityPlayer player;
    private int abilityId;
    private boolean isDBC;

    public AbilityUsePacket(EntityPlayer player, int abilityId, boolean isDBC) {
        this.player = player;
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
        ByteBufUtils.writeUTF8String(out, this.player.getCommandSenderName());
        out.writeInt(this.abilityId);
        out.writeBoolean(this.isDBC);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        String playerName = ByteBufUtils.readUTF8String(in);
        EntityPlayer sendingPlayer = getPlayerByName(playerName);
        if (sendingPlayer == null)
            return;

        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
        int abilityId = in.readInt();
        boolean isDBC = in.readBoolean();

        if (AbilityController.Instance.has(abilityId, isDBC)) {
            Ability ability = AbilityController.getInstance().get(abilityId, isDBC);
            AbilityData data = isDBC ? info.dbcAbilityData : info.customAbilityData;

            if (ability != null && data.hasAbilityUnlocked(abilityId) && ability.callEvent(player)) {
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
}
