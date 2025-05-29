package kamkeel.npcdbc.network.packets.player.aura;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public final class DBCRequestAura extends AbstractPacket {
    public static final String packetName = "NPC|RequestAura";
    private int auraID;
    private boolean onlyPlayers;

    public DBCRequestAura(int auraID, boolean players) {
        this.auraID = auraID;
        this.onlyPlayers = players;

    }

    public DBCRequestAura() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AuraList;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.auraID);
        out.writeBoolean(this.onlyPlayers);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        this.auraID = in.readInt();
        this.onlyPlayers = in.readBoolean();
        if (auraID != -1) {
            Aura aura = (Aura) AuraController.getInstance().get(auraID);
            if (aura != null) {
                NBTTagCompound compound = aura.writeToNBT();
                compound.setString("PACKETTYPE", "Aura");
                GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
            }
        } else if (onlyPlayers) {
            NetworkUtility.sendPlayersAuras((EntityPlayerMP) player);
        } else {
            NetworkUtility.sendCustomAuraDataAll((EntityPlayerMP) player);
        }
    }
}
