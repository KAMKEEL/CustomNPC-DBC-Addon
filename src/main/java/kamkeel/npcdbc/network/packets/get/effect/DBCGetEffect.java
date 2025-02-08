package kamkeel.npcdbc.network.packets.get.effect;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public final class DBCGetEffect extends AbstractPacket {
    public static final String packetName = "NPC|GetEffect";
    private int effectID;

    public DBCGetEffect(int EffectID) {
        this.effectID = EffectID;
    }

    public DBCGetEffect() {
    }

    @Override
    public Enum getType() {
        return null;
    }

    @Override
    public PacketChannel getChannel() {
        return PacketHandler.GET_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.effectID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        effectID = in.readInt();
        NBTTagCompound compound = new NBTTagCompound();
        if (effectID != -1 && StatusEffectController.getInstance().has(effectID)) {
            CustomEffect Effect = (CustomEffect) StatusEffectController.getInstance().get(effectID);
            if (Effect != null) {
                compound = Effect.writeToNBT(false);
                compound.setString("Type", "ViewEffect");
            }
        }
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }
}
