package kamkeel.npcdbc.network.packets.request.effect;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import kamkeel.npcdbc.client.gui.global.effects.GuiDBCEffectScript;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcdbc.util.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import kamkeel.npcdbc.network.LargeAbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class DBCReceiveEffectScript extends LargeAbstractPacket {
    public static final String packetName = "NPC|ApplyEffectScript";
    private NBTTagCompound compound;

    public DBCReceiveEffectScript() {

    }

    public DBCReceiveEffectScript(NBTTagCompound compound) {
        this.compound = compound;
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.EffectScriptReceive;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }


    @Override
    protected byte[] getData() throws IOException {
        ByteBuf byteBuf = Unpooled.buffer();
        ByteBufUtils.writeNBT(byteBuf, compound);
        return byteBuf.array();
    }

    @Override
    protected void handleCompleteData(ByteBuf data, EntityPlayer player) throws IOException {
        NBTTagCompound scriptData = ByteBufUtils.readNBT(data);
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof GuiDBCEffectScript) {
            GuiDBCEffectScript scriptGui = (GuiDBCEffectScript) screen;
            scriptGui.setGuiData(scriptData);
        }
    }
}
