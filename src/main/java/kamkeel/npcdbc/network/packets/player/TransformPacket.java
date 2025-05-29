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
    private EntityPlayer player;
    private int state;
    private boolean ascend;

    public TransformPacket(EntityPlayer player, int state, boolean ascend) {
        this.player = player;
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
        ByteBufUtils.writeUTF8String(out, this.player.getCommandSenderName());
        out.writeInt(this.state);
        out.writeBoolean(ascend);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        String playerName = ByteBufUtils.readUTF8String(in);
        EntityPlayer sendingPlayer = getPlayerByName(playerName);
        if (sendingPlayer == null)
            return;

        int state = in.readInt();
        boolean ascend = in.readBoolean();
        if (ascend)
            TransformController.handleFormAscend(sendingPlayer, state);
        else
            TransformController.handleFormDescend(sendingPlayer, state);

    }
}
