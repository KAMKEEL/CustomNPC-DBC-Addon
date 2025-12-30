package kamkeel.npcdbc.network.packets.request.ability;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AbilityScript;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketClient;
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

public class DBCAbilityScript extends AbstractPacket {
    public static String packetName = "Request|AbilityScript";

    private DBCAbilityScript.Action type;
    private int id;
    private int page;
    private int maxSize;
    private NBTTagCompound compound;

    public DBCAbilityScript(DBCAbilityScript.Action type, int id, int page, int maxSize, NBTTagCompound compound) {
        this.type = type;
        this.id = id;
        this.page = page;
        this.maxSize = maxSize;
        this.compound = compound;
    }

    public DBCAbilityScript() {
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.AbilityScript;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission() {
        return CustomNpcsPermissions.SCRIPT_PLAYER;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(type.ordinal());
        out.writeInt(id);

        if (type == DBCAbilityScript.Action.SAVE) {
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

        DBCAbilityScript.Action requestedAction = DBCAbilityScript.Action.values()[in.readInt()];
        Ability ability = AbilityController.getInstance().get(in.readInt());
        if (ability == null)
            return;

        AbilityScript data = ability.abilityData.getOrCreateScriptHandler();
        if (requestedAction == DBCAbilityScript.Action.GET) {
            PacketUtil.getScripts((IScriptHandler) data, (EntityPlayerMP) player);
        } else {
            data.saveScript(in);
            if (ConfigDebug.PlayerLogging && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                LogWriter.script(String.format("[%s] (Player) %s SAVED ABILITY %s [%s]", "ABILITY SCRIPTS", player.getCommandSenderName(), ability.getName()));
            }
        }
    }

    public static void Save(int effectID, int id, int maxSize, NBTTagCompound compound) {
        DBCPacketClient.sendClient(new DBCAbilityScript(DBCAbilityScript.Action.SAVE, effectID, id, maxSize, compound));
    }

    public static void Get(int effectID) {
        DBCPacketClient.sendClient(new DBCAbilityScript(DBCAbilityScript.Action.GET, effectID, -1, -1, new NBTTagCompound()));
    }

    private enum Action {
        GET,
        SAVE
    }
}
