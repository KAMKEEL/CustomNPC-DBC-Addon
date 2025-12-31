package kamkeel.npcdbc.network.packets.get.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketGet;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class DBCGetAbility extends AbstractPacket {
    public static final String packetName = "NPC|GetAura";
    private int abilityID;

    public DBCGetAbility(int abilityID) {
        this.abilityID = abilityID;
    }

    public DBCGetAbility() {
    }

    @Override
    public Enum getType() {
        return EnumPacketGet.Ability;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.GET_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.abilityID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int abilityID = in.readInt();
        NBTTagCompound compound = new NBTTagCompound();
        if (abilityID != -1 && AbilityController.getInstance().has(abilityID)) {
            Ability ability = (Ability) AbilityController.getInstance().get(abilityID);
            if (ability != null) {
                compound = ability.writeToNBT(false);
            }
        }
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
    }
}
