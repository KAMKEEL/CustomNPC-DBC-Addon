package kamkeel.npcdbc.network.packets.player.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class DBCAnimateAbility extends AbstractPacket {
    int abilityId = -1;
    boolean animate = false;

    public DBCAnimateAbility(int abilityId, boolean animate) {
        this.abilityId = abilityId;
    }

    public DBCAnimateAbility(){}

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
        out.writeBoolean(animate);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        this.abilityId = in.readInt();
        this.animate = in.readBoolean();

        PlayerDBCInfo dbcInfo = PlayerDataUtil.getDBCInfo(player);

        if (animate)
            dbcInfo.dbcAbilityData.animatingAbilities.add(abilityId);
        else
            dbcInfo.dbcAbilityData.animatingAbilities.remove(abilityId);

        dbcInfo.updateClient();
    }
}
