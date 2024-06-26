package kamkeel.npcdbc.network;

import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import kamkeel.npcdbc.network.packets.SendChat;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.PlayerDataController;

import java.util.HashMap;
import java.util.Map;

import static noppes.npcs.NoppesUtilServer.sendScrollData;

public class NetworkUtility {

    public static void sendCustomFormDataAll(EntityPlayerMP player) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Form customForm : FormController.getInstance().customForms.values()) {
            map.put(customForm.name, customForm.id);
        }
        sendScrollData(player, map);
    }

    public static void sendCustomAuraDataAll(EntityPlayerMP player) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Aura customAura : AuraController.getInstance().customAuras.values()) {
            map.put(customAura.name, customAura.id);
        }
        sendScrollData(player, map);
    }

    public static void sendPlayersForms(EntityPlayer player){
        PlayerDataUtil.sendFormDBCInfo((EntityPlayerMP) player);
        PlayerDBCInfo data = ((IPlayerDBCInfo) PlayerDataController.Instance.getPlayerData(player)).getPlayerDBCInfo();
        NBTTagCompound compound = new NBTTagCompound();
        if(data != null &&  data.selectedForm != -1){
            Form customForm = (Form) FormController.getInstance().get(data.selectedForm);
            if(customForm != null)
                compound = customForm.writeToNBT();
        }
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }

    public static void sendPlayersAuras(EntityPlayer player){
        PlayerDataUtil.sendAuraDBCInfo((EntityPlayerMP) player);
        PlayerDBCInfo data = ((IPlayerDBCInfo) PlayerDataController.Instance.getPlayerData(player)).getPlayerDBCInfo();
        NBTTagCompound compound = new NBTTagCompound();
        if(data != null &&  data.selectedAura != -1){
            Aura customAura = (Aura) AuraController.getInstance().get(data.selectedAura);
            if(customAura != null)
                compound = customAura.writeToNBT();
        }
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }

    public static void sendServerMessage(EntityPlayer player, Object... message){
        PacketHandler.Instance.sendToPlayer(new SendChat(message).generatePacket(), (EntityPlayerMP) player);
    }

    public static void sendInfoMessage(EntityPlayer player, Object... message){
        PacketHandler.Instance.sendToPlayer(new SendChat(true, message).generatePacket(), (EntityPlayerMP) player);
    }
}
