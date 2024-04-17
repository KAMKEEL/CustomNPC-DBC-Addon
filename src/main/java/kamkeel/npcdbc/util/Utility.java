package kamkeel.npcdbc.util;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import noppes.npcs.Server;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.scripted.NpcAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static noppes.npcs.NoppesUtilServer.sendScrollData;

public class Utility {
    public static boolean isServer() {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
    }

    public static void sendMessage(EntityPlayer player, String message) {
        player.addChatMessage(new ChatComponentText(message));
    }

    public static Entity getEntityFromID(World w, String s) { //s is basically getEntityID
        if (!s.isEmpty()) {
            if (Boolean.parseBoolean(s.split(",")[1])) //if player
                return Utility.getEntityByUUID(w, s.split(",")[0]);
            else
                return w.getEntityByID(Integer.parseInt(s.split(",")[0]));
        }
        return null;

    }

    public static String getEntityID(Entity p) {
        if (p instanceof EntityPlayer)
            return p.getUniqueID().toString() + ",true"; //true as in "entity is player"
        else if (p != null)
            return p.getEntityId() + ",false";
        else
            return "";
    }

    public static Entity getEntityByUUID(World w, String s) {
        if (s == null)
            return null;

        List<Entity> allEntity = w.loadedEntityList;
        for (Entity player : allEntity)
            if (player.getUniqueID().toString().equals(s))
                return player;

        return null;

    }

    public static void printStackTrace() {
        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
            System.out.println(ste);
    }

    public static double percent(double n, double perc) {
        return n / 100 * perc;
    }

    public static float percent(int n, float perc) {
        return n / 100 * perc;
    }

    public static boolean percentBetween(double n, double maxN, double minPerc, double maxPerc) {
        return n >= Utility.percent(maxN, minPerc) && n < Utility.percent(maxN, maxPerc);
    }

    public static IPlayer getIPlayer(EntityPlayer p) {
        return (IPlayer) NpcAPI.Instance().getIEntity(p);
    }

    @SideOnly(Side.CLIENT)
    public static PlayerCustomFormData getFormDataClient() {
        return ((IPlayerFormData) PlayerData.get(Minecraft.getMinecraft().thePlayer)).getCustomFormData();
    }

    public static PlayerCustomFormData getFormData(EntityPlayer p) {
        return Utility.getFormData(PlayerDataController.Instance.getPlayerData(p));

    }

    public static PlayerCustomFormData getFormData(PlayerData playerData) {
        return ((IPlayerFormData) playerData).getCustomFormData();
    }

    public static void sendPlayerFormData(EntityPlayerMP player) {
        PlayerCustomFormData data = ((IPlayerFormData) PlayerDataController.Instance.getPlayerData(player)).getCustomFormData();

        Map<String,Integer> map = new HashMap<String,Integer>();
        for(int formID :data.unlockedForms.keySet()){
            CustomForm form = (CustomForm) FormController.getInstance().get(formID);
            if(form != null){
                map.put(form.name, form.id);
            }
        }
        sendScrollData(player, map);
    }

    public static int guiX(GuiNPCInterface gui, double i) {
        return gui.guiLeft + (int) (gui.xSize * i);
    }

    public static int guiY(GuiNPCInterface gui, double i) {
        return gui.guiTop + (int) (gui.ySize * i);
    }

    public static int guiWidth(GuiNPCInterface gui, double i) {
        return (int) (gui.xSize * i);
    }

    public static int guiHeight(GuiNPCInterface gui, double i) {
        return (int) (gui.ySize * i);
    }
}
