package kamkeel.npcdbc.network.packets.player.effect;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
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
                Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
            }
        } else {
            NetworkUtility.sendCustomEffectDataAll((EntityPlayerMP) player);
        }
    }
}
