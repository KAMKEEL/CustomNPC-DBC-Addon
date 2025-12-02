package kamkeel.npcdbc.network.packets.player;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

import static noppes.npcs.NoppesUtilServer.getPlayerByName;

public final class TransformPacket extends AbstractPacket {
    public static final String packetName = "NPC|Transform";
    private int state;
    private boolean ascend;
    private int stackedFrom;

    public TransformPacket(int state, boolean ascend, int stackedFrom) {
        this.state = state;
        this.ascend = ascend;
        this.stackedFrom = stackedFrom;
    }

    public TransformPacket() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.Transform;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.state);
        out.writeBoolean(ascend);
        out.writeInt(this.stackedFrom);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (player == null)
            return;

        int state = in.readInt();
        boolean ascend = in.readBoolean();
        int stackedFrom = in.readInt();
        if (ascend)
            TransformController.handleFormAscend(player, state, stackedFrom);
        else
            TransformController.handleFormDescend(player, state, stackedFrom);

    }
}
