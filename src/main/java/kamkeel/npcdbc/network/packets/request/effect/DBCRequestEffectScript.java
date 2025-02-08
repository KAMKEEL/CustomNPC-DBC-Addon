package kamkeel.npcdbc.network.packets.request.effect;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import net.minecraft.entity.player.EntityPlayer;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;

public class DBCRequestEffectScript extends AbstractPacket {
    public static final String packetName = "NPC|GetEffectScript";

    private int id;

    public DBCRequestEffectScript(int id) {
        this.id = id;
    }
    public DBCRequestEffectScript() {

    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.EffectScriptGet;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(id);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        StatusEffect effect = StatusEffectController.getInstance().get(in.readInt());
        if (effect instanceof CustomEffect) {
            CustomEffect effect1 = (CustomEffect) effect;
            NetworkUtility.getScripts(effect1.script, (EntityPlayerMP) player);
        }
    }
}
