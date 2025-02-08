package kamkeel.npcdbc.network.packets.request.effect;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.Server;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCEFFECT;

public class DBCRemoveEffect extends AbstractPacket {
    public static final String packetName = "NPC|RemEffect";

    private int effectID;

    public DBCRemoveEffect(int outlineID) {
        this.effectID = outlineID;
    }

    public DBCRemoveEffect() {

    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.EffectRemove;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.effectID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCEFFECT))
            return;

        effectID = in.readInt();
        StatusEffectController.getInstance().delete(effectID);
        NetworkUtility.sendCustomEffectDataAll((EntityPlayerMP) player);
        NBTTagCompound compound = (new CustomEffect()).writeToNBT(false);
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
    }
}
