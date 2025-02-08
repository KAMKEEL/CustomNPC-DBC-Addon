package kamkeel.npcdbc.network.packets.request.effect;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.data.statuseffect.custom.EffectScriptHandler;
import kamkeel.npcdbc.network.DBCPacketClient;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcdbc.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import kamkeel.npcdbc.network.LargeAbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.LogWriter;
import noppes.npcs.config.ConfigDebug;

import java.io.IOException;

public class DBCSaveEffectScript extends LargeAbstractPacket {
    public static final String packetName = "NPC|SaveEffectScript";

    private int id, page, maxSize;
    private NBTTagCompound compound;

    public DBCSaveEffectScript() {

    }

    private DBCSaveEffectScript(int id, int pageID, int maxSize, NBTTagCompound compound) {
        this.id = id;
        this.page = pageID;
        this.maxSize = maxSize;
        this.compound = compound;
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.EffectScriptSave;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }


    @Override
    protected byte[] getData() throws IOException {
        ByteBuf out = Unpooled.buffer();
        out.writeInt(this.id);
        out.writeInt(this.page);
        out.writeInt(this.maxSize);
        ByteBufUtils.writeNBT(out, this.compound);
        return out.array();
    }

    @Override
    protected void handleCompleteData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!(player instanceof EntityPlayerMP))
            return;

        CustomEffect effect = (CustomEffect) StatusEffectController.getInstance().get(in.readInt());
        if (effect == null)
            return;
        EffectScriptHandler data = effect.script;
        data.saveScript(in);
        if(ConfigDebug.PlayerLogging && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
            LogWriter.script(String.format("[%s] (Player) %s SAVED EFFECT %s [%s]", "EFFECT SCRIPTS", player.getCommandSenderName(), effect.getName()));
        }
    }

    public static void Save(int id, int page, int maxSize, NBTTagCompound compound) {
        DBCPacketClient.sendClient(new DBCSaveEffectScript(id, page, maxSize, compound));
    }
}
