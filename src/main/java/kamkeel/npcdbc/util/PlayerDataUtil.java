package kamkeel.npcdbc.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.NpcAPI;

import java.util.HashMap;
import java.util.Map;

import static noppes.npcs.NoppesUtilServer.sendScrollData;

public class PlayerDataUtil {

    /**
     * Since PlayerDBCInfo is a Mixin Class designed to append to
     * PlayerData it is simpler to call a UTIL Function. Calling functions
     * directly from Mixin Classes itself, are sloppy.
     *
     * PlayerDBCInfo is intended to hold valuable PlayerData information on the CNPC+
     * side. Unlocked Aura, Forms IDs. Form Mastery, selected/current Aura and Forms.
     * These are then outsourced to DBC Data to save on their player persisted.
     * Other Clients Rendering a Player will naturally be sent others DBC Data when
     * rendering, so the Form/Aura Information is bundled with it. However, this means
     * the SOURCE of DBC Info is CustomNPC+ PlayerData file and not Minecraft PlayerData.
     */

    @SideOnly(Side.CLIENT)
    public static PlayerDBCInfo getClientDBCInfo() {
        if (Minecraft.getMinecraft().thePlayer == null)
            return null;
        IPlayerDBCInfo formData = (IPlayerDBCInfo) PlayerData.get(Minecraft.getMinecraft().thePlayer);
        if (formData == null)
            return null;
        return formData.getPlayerDBCInfo();
    }

    public static PlayerDBCInfo getDBCInfo(EntityPlayer player) {
        return getDBCInfo(PlayerDataController.Instance.getPlayerData(player));
    }

    public static PlayerDBCInfo getDBCInfo(PlayerData playerData) {
        return ((IPlayerDBCInfo) playerData).getPlayerDBCInfo();
    }


    /**
     * Compress Player information intpo CNPC+ Pre-Built
     * Scroll Packets for Inventory Menu
     */
    public static void sendFormDBCInfo(EntityPlayerMP player) {
        PlayerDBCInfo data = ((IPlayerDBCInfo) PlayerDataController.Instance.getPlayerData(player)).getPlayerDBCInfo();

        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int formID : data.unlockedForms) {
            Form form = (Form) FormController.getInstance().get(formID);
            if (form != null) {
                map.put(form.name, form.id);
            }
        }
        sendScrollData(player, map);
    }

    public static void sendAuraDBCInfo(EntityPlayerMP player) {
        PlayerDBCInfo data = ((IPlayerDBCInfo) PlayerDataController.Instance.getPlayerData(player)).getPlayerDBCInfo();

        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int auraID : data.unlockedAuras) {
            Aura aura = (Aura) AuraController.getInstance().get(auraID);
            if (aura != null) {
                map.put(aura.name, aura.id);
            }
        }
        sendScrollData(player, map);
    }

    /**
     * Converting EntityPlayer to IPlayer for Scripting API
     */
    public static IPlayer getIPlayer(EntityPlayer p) {
        return (IPlayer) NpcAPI.Instance().getIEntity(p);
    }

    public static Form getForm(Entity entity) {
        if (entity instanceof EntityPlayer)
            return DBCData.get((EntityPlayer) entity).getForm();
        else if (entity instanceof EntityNPCInterface)
            return ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay().getForm();

        return null;
    }

    public static Aura getToggledAura(Entity entity) {
        if (entity instanceof EntityPlayer)
            return DBCData.get((EntityPlayer) entity).getToggledAura();
        else if (entity instanceof EntityNPCInterface)
            return ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay().getToggledAura();

        return null;
    }

    public static boolean useStencilBuffer(Entity entity) {
        boolean auraOn = false, outlineOn = false;
        if (entity instanceof EntityPlayer) {
            auraOn = DBCData.get((EntityPlayer) entity).isAuraOn();
            outlineOn = DBCData.get((EntityPlayer) entity).getOutline() != null;
        } else if (entity instanceof EntityNPCInterface) {
            auraOn = ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay().isAuraOn();
            outlineOn = ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay().getOutline() != null;
        }

        return auraOn || outlineOn;
    }
}
