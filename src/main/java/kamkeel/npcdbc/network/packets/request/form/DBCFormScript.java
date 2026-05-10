package kamkeel.npcdbc.network.packets.request.form;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormScript;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.network.PacketUtil;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.LogWriter;
import noppes.npcs.config.ConfigDebug;
import noppes.npcs.config.ConfigScript;
import noppes.npcs.controllers.data.IScriptHandler;

import java.io.IOException;

public class DBCFormScript extends AbstractPacket {
    public static String packetName = "NPC|FormScript";

    private Action type;
    private int id;
    private int page;
    private int maxSize;
    private NBTTagCompound compound;

    public DBCFormScript() {
    }

    public DBCFormScript(Action type, int id, int page, int maxSize, NBTTagCompound compound) {
        this.type = type;
        this.id = id;
        this.page = page;
        this.maxSize = maxSize;
        this.compound = compound;
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.FormScript;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(type.ordinal());
        out.writeInt(id);

        if (type == Action.SAVE) {
            out.writeInt(this.page);
            out.writeInt(this.maxSize);
            ByteBufUtils.writeNBT(out, this.compound);
        }
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!(player instanceof EntityPlayerMP))
            return;

        if (!ConfigScript.canScript(player, CustomNpcsPermissions.SCRIPT))
            return;

        Action requestedAction = Action.values()[in.readInt()];
        Form form = (Form) FormController.getInstance().get(in.readInt());
        if (form == null)
            return;

        FormScript data = form.getOrCreateScriptHandler();
        if (requestedAction == Action.GET) {
            PacketUtil.getScripts((IScriptHandler) data, (EntityPlayerMP) player);
        } else {
            data.saveScript(in);
            if (ConfigDebug.PlayerLogging && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                LogWriter.script(String.format("[%s] (Player) %s SAVED FORM %s [%s]", "FORM SCRIPTS", player.getCommandSenderName(), form.getName(), form.id));
            }
        }
    }

    public static void Save(int formId, int id, int maxSize, NBTTagCompound compound) {
        DBCPacketHandler.Instance.sendToServer(new DBCFormScript(Action.SAVE, formId, id, maxSize, compound));
    }

    public static void Get(int formId) {
        DBCPacketHandler.Instance.sendToServer(new DBCFormScript(Action.GET, formId, -1, -1, new NBTTagCompound()));
    }

    private enum Action {
        GET,
        SAVE
    }
}
