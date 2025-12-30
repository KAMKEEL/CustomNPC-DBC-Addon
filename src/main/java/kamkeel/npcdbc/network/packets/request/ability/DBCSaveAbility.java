package kamkeel.npcdbc.network.packets.request.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class DBCSaveAbility extends AbstractPacket {
    public static final String packetName = "NPC|SaveAbility";

    private String prevName;
    private NBTTagCompound ability;

    public DBCSaveAbility(NBTTagCompound compound, String prev) {
        this.ability = compound;
        this.prevName = prev;
    }

    public DBCSaveAbility() {
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.AbilitySave;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeString(out, prevName);
        ByteBufUtils.writeNBT(out, ability);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
//        if (!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
//            return;

        String prevName = ByteBufUtils.readString(in);

        Ability ability = new Ability();
        ability.readFromNBT(ByteBufUtils.readNBT(in));

        AbilityController.getInstance().saveAbility(ability);

        if (!prevName.isEmpty() && !prevName.equals(ability.name)) {
            AbilityController.getInstance().deleteAbilityFile(prevName);
        }

        NetworkUtility.sendAbilityDataAll((EntityPlayerMP) player);
    }
}
