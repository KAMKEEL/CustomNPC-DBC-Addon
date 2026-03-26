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

public final class TransformPacket extends AbstractPacket {
    public static final String packetName = "NPC|Transform";
    
    public static final int DESCEND = -1; //once
    public static final int FULL_DESCEND = -10; //to base

    private boolean ascend;
    private String formKey;
    private int state;

    public TransformPacket(String formKey) {
        this.ascend = true;
        this.formKey = formKey;
    }

    public TransformPacket(int state) {
        this.ascend = false;
        this.state = state;
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
        out.writeBoolean(ascend);
        if (ascend)
            ByteBufUtils.writeUTF8String(out, formKey);
        else
            out.writeInt(state);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (player == null)
            return;

        boolean ascend = in.readBoolean();
        if (ascend)
            TransformController.handleFormAscend(player, ByteBufUtils.readUTF8String(in));
        else
            TransformController.handleFormDescend(player, in.readInt());
    }
}
