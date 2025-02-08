package kamkeel.npcdbc.network.packets.player.effect;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public final class DBCRequestEffect extends AbstractPacket {
    public static final String packetName = "NPC|RequestEffect";
    private int effectID;

    public DBCRequestEffect(int outlineID) {
        this.effectID = outlineID;
    }

    public DBCRequestEffect() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.EffectList;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.effectID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        this.effectID = in.readInt();

        if (effectID != -1) {
            CustomEffect effect = (CustomEffect) StatusEffectController.getInstance().get(effectID);
            if (effect != null) {
                NBTTagCompound compound = effect.writeToNBT(false);
                compound.setString("PACKETTYPE", "Effect");
                GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
            }
        } else {
            NetworkUtility.sendCustomEffectDataAll((EntityPlayerMP) player);
        }
    }
}
