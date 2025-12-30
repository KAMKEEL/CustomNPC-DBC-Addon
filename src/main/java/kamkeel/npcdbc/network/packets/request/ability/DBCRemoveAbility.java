package kamkeel.npcdbc.network.packets.request.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class DBCRemoveAbility extends AbstractPacket {
    public static final String packetName = "NPC|RemAbility";

    private int abilityID;

    public DBCRemoveAbility(int abilityID) {
        this.abilityID = abilityID;
    }

    public DBCRemoveAbility() {
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.AbilityRemove;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.abilityID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
//        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
//            return;

        int abilityID = in.readInt();
        AbilityController.getInstance().delete(abilityID);
        NetworkUtility.sendAbilityDataAll((EntityPlayerMP) player);
        NBTTagCompound compound = (new Aura()).writeToNBT();
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
    }
}
