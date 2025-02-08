package kamkeel.npcdbc.network;

import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.FormWheelData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import kamkeel.npcdbc.network.packets.player.SendChat;
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

    public static void sendCustomOutlineDataAll(EntityPlayerMP player) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Outline outline : OutlineController.getInstance().customOutlines.values()) {
            map.put(outline.name, outline.id);
        }
        sendScrollData(player, map);
    }

    public static void sendCustomEffectDataAll(EntityPlayerMP player) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (CustomEffect effect : StatusEffectController.getInstance().customEffects.values()) {
            map.put(effect.name, effect.id);
        }
        sendScrollData(player, map);
    }

    public static void sendPlayersForms(EntityPlayer player, boolean useMenuName) {
        PlayerDataUtil.sendFormDBCInfo((EntityPlayerMP) player, useMenuName);
        PlayerDBCInfo data = ((IPlayerDBCInfo) PlayerDataController.Instance.getPlayerData(player)).getPlayerDBCInfo();
        NBTTagCompound compound = new NBTTagCompound();
        if (data != null && data.selectedForm != -1) {
            Form customForm = (Form) FormController.getInstance().get(data.selectedForm);
            if (customForm != null)
                compound = customForm.writeToNBT();
        }
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }

    public static void sendPlayerFormWheel(EntityPlayer player) {
        PlayerDataUtil.sendFormDBCInfo((EntityPlayerMP) player, false);
        PlayerDBCInfo data = ((IPlayerDBCInfo) PlayerDataController.Instance.getPlayerData(player)).getPlayerDBCInfo();
        NBTTagCompound compound = new NBTTagCompound();
        if (data != null) {
            for (int i = 0; i < 6; i++) {
                FormWheelData wheelData = data.formWheel[i];
                if (wheelData.formID != -1 && !wheelData.isDBC && !FormController.getInstance().has(wheelData.formID))
                    wheelData.formID = -1;
                wheelData.writeToNBT(compound);
            }
        }
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }

    public static void sendPlayersAuras(EntityPlayer player) {
        PlayerDataUtil.sendAuraDBCInfo((EntityPlayerMP) player);
        PlayerDBCInfo data = ((IPlayerDBCInfo) PlayerDataController.Instance.getPlayerData(player)).getPlayerDBCInfo();
        NBTTagCompound compound = new NBTTagCompound();
        if (data != null && data.selectedAura != -1) {
            Aura customAura = (Aura) AuraController.getInstance().get(data.selectedAura);
            if (customAura != null)
                compound = customAura.writeToNBT();
        }
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }

    public static void sendServerMessage(EntityPlayer player, Object... message) {
        PacketHandler.Instance.sendToPlayer(new SendChat(message), (EntityPlayerMP) player);
    }

    public static void sendInfoMessage(EntityPlayer player, Object... message) {
        PacketHandler.Instance.sendToPlayer(new SendChat(true, message), (EntityPlayerMP) player);
    }
}
