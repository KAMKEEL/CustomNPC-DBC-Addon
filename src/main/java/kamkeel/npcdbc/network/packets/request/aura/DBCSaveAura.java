package kamkeel.npcdbc.network.packets.request.aura;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCAURA;

public class DBCSaveAura extends AbstractPacket {
    public static final String packetName = "NPC|SaveAura";

    private String prevName;
    private NBTTagCompound aura;

    public DBCSaveAura(NBTTagCompound compound, String prev) {
        this.aura = compound;
        this.prevName = prev;
    }

    public DBCSaveAura() {

    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.AuraSave;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeString(out, prevName);
        ByteBufUtils.writeNBT(out, aura);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        String prevName = ByteBufUtils.readString(in);

        Aura aura = new Aura();
        aura.readFromNBT(ByteBufUtils.readNBT(in));

        AuraController.getInstance().saveAura(aura);

        if (!prevName.isEmpty() && !prevName.equals(aura.name)) {
            AuraController.getInstance().deleteAuraFile(prevName);
        }

        NetworkUtility.sendCustomAuraDataAll((EntityPlayerMP) player);
    }
}
