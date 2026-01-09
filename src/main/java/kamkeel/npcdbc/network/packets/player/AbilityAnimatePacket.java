package kamkeel.npcdbc.network.packets.player;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AbilityData;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class AbilityAnimatePacket extends AbstractPacket {
    public static final String packetName = "NPC|AbilityAnimate";
    private int abilityId;
    private boolean isDBC;
    private boolean animate;

    public AbilityAnimatePacket() {}

    public AbilityAnimatePacket(int abilityId, boolean isDBC, boolean animate) {
        this.abilityId = abilityId;
        this.isDBC = isDBC;
        this.animate = animate;
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AbilityAnimate;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(abilityId);
        out.writeBoolean(isDBC);
        out.writeBoolean(animate);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (player == null)
            return;

        int abilityId = in.readInt();
        boolean isDBC = in.readBoolean();
        boolean animate = in.readBoolean();

        Ability ability = (Ability) AbilityController.getInstance().get(abilityId, isDBC);
        if (ability == null || ability.getType() != Ability.Type.Animated)
            return;

        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
        AbilityData data = isDBC ? info.dbcAbilityData : info.customAbilityData;

        if (!data.hasAbilityUnlocked(abilityId))
            return;

        if (animate) {
            data.animatingAbility = abilityId;
//            NetworkUtility.sendServerMessage(player, "I AM INSIDE YOUR WALLS");
        } else {
            data.animatingAbility = -1;
//            NetworkUtility.sendServerMessage(player, "I AM OUTSIDE YOUR WALLS");
        }
        info.updateClient();
    }
}
