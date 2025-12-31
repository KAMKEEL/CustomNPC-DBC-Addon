package kamkeel.npcdbc.network.packets.player.ability;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AbilityData;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;


public final class DBCSelectAbility extends AbstractPacket {
    public static final String packetName = "NPC|SelectAbility";
    private int abilityID;
    private boolean isDBC;

    public DBCSelectAbility(int abilityID, boolean isDBC) {
        this.abilityID = abilityID;
        this.isDBC = isDBC;
    }

    public DBCSelectAbility() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.AbilitySelect;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.abilityID);
        out.writeBoolean(this.isDBC);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int abilityID = in.readInt();
        boolean isDBC = in.readBoolean();

        PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
        PlayerDBCInfo dbcInfo = PlayerDataUtil.getDBCInfo(playerData);
        AbilityData data = isDBC ? dbcInfo.dbcAbilityData : dbcInfo.customAbilityData;
        NBTTagCompound compound = new NBTTagCompound();

        if (abilityID == -1 && data.selectedAbility == -1)
            return;

        if (isDBC && abilityID != -1) {
            if (abilityID == data.selectedAbility)
                return;

            // TODO figure out a nice way to implement a controller for DBC abilities
            data.selectedAbility = abilityID;
        } else if(abilityID != -1 && AbilityController.getInstance().has(abilityID)) {
            if (abilityID == data.selectedAbility)
                return;

            Ability ability = (Ability) AbilityController.getInstance().get(abilityID);
            if (ability != null && data.hasAbilityUnlocked(abilityID)) {
                data.selectedAbility = abilityID;
                NetworkUtility.sendServerMessage(player, "§a", "npcdbc.abilitySelect", " ", ability.getMenuName());
                compound = ability.writeToNBT(false);
            }
        } else {
            data.selectedAbility = -1;
            NetworkUtility.sendServerMessage(player, "§9", "npcdbc.clearedSelection");
        }

        dbcInfo.updateClient();
        GuiDataPacket.sendGuiData((EntityPlayerMP) player, compound);
        DBCData.get(player).saveNBTData(true);
    }
}
