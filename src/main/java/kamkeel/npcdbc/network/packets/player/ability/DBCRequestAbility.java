package kamkeel.npcdbc.network.packets.player.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcs.network.PacketUtil;
import kamkeel.npcs.network.enums.EnumItemPacketType;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.CustomEffectController;
import noppes.npcs.controllers.data.CustomEffect;

import java.io.IOException;

public class DBCRequestAbility extends AbstractPacket {
    public static final String packetName = "NPC|RequestAbility";
    private int abilityID;
    private boolean onlyPlayers;
    private boolean menuName;

    public DBCRequestAbility(int abilityID, boolean players, boolean menuName) {
        this.abilityID = abilityID;
        this.onlyPlayers = players;
        this.menuName = menuName;
    }

    public DBCRequestAbility() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AbilityList;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.abilityID);
        out.writeBoolean(this.onlyPlayers);
        out.writeBoolean(this.menuName);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (!(player instanceof EntityPlayerMP))
            return;

        this.abilityID = in.readInt();
        this.onlyPlayers = in.readBoolean();
        this.menuName = in.readBoolean();


        if (abilityID != -1) {
            CustomEffect effect = CustomEffectController.getInstance().get(abilityID);
            if (effect != null) {
                NBTTagCompound compound = effect.writeToNBT(false);
                compound.setString("PACKETTYPE", "Effect");
                GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
            }
        } else if (onlyPlayers) {
            NetworkUtility.sendPlayersAbilities(player, menuName);
        } else {
            NetworkUtility.sendAbilityDataAll((EntityPlayerMP) player);
        }
    }
}
