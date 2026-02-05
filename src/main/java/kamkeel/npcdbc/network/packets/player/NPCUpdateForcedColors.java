package kamkeel.npcdbc.network.packets.player;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;

import java.io.IOException;

public final class NPCUpdateForcedColors extends AbstractPacket {
    public static final String packetName = "NPC|PingFormCustom";

    private NBTTagCompound clientData;
    private EntityNPCInterface npc;

    public NPCUpdateForcedColors() {
    }


    public NPCUpdateForcedColors(EntityNPCInterface npc, NBTTagCompound dataNeededOnClient) {
        this.npc = npc;
        this.clientData = dataNeededOnClient;
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.NPCPingForm;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(npc.getEntityId());
        ByteBufUtils.writeNBT(out, this.clientData);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int entityID = in.readInt();
        Entity ent = player.getEntityWorld().getEntityByID(entityID);
        if (ent instanceof EntityNPCInterface) {
            DBCDisplay display = ((INPCDisplay) ((EntityNPCInterface) ent).display).getDBCDisplay();
            display.loadClientSideFormColorData(ByteBufUtils.readNBT(in));
        }
    }
}
