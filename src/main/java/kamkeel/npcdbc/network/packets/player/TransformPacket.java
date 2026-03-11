package kamkeel.npcdbc.network.packets.player;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public final class TransformPacket extends AbstractPacket {
    public static final String packetName = "NPC|Transform";
    private int state;
    private boolean ascend;

    public TransformPacket(int state, boolean ascend) {
        this.state = state;
        this.ascend = ascend;
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
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (player == null)
            return;

        int state = in.readInt();
        boolean ascend = in.readBoolean();
        if (ascend)
            TransformController.handleFormAscend(player, state);
        else
            TransformController.handleFormDescend(player, state);

    }
}
