package kamkeel.npcdbc.util;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.ISound;
import noppes.npcs.scripted.NpcAPI;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static kamkeel.npcdbc.util.PlayerDataUtil.getIPlayer;

public class Utility {
    public static boolean isServer() {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
    }

    public static boolean isServer(Entity entity) {
        return entity != null && entity.worldObj != null && !entity.worldObj.isRemote;
    }

    public static boolean isServer(World worldObj) {
        return worldObj != null && !worldObj.isRemote;
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

    public static void playSound(Entity p, String soundid, int range) {
        playSound(p, soundid, range, 1);
    }

    public static void playSound(Entity p, String soundid, int range, float volume) {
        ISound IT = NpcAPI.Instance().createSound(soundid);
        IT.setVolume(1);
        IT.setPitch(1);

        if (p instanceof EntityPlayer) {
            IPlayer<?> pl = getIPlayer((EntityPlayer) p);
            IT.setEntity(pl);
            pl.playSound(1, IT);
        }

        if (range == 0)
            return;

        IEntity<?> y = NpcAPI.Instance().getIEntity(p);
        IEntity<?>[] pls = y.getSurroundingEntities(range);
        for (IEntity<?> pe : pls)
            if (pe instanceof IPlayer && pe != p)
                ((IPlayer<?>) pe).playSound(1, IT);

    }

    public static String getEntityID(Entity p) {
        if (p instanceof EntityPlayer)
            return p.getUniqueID().toString() + ",true"; //true as in "entity is player"
        else if (p != null)
            return p.getEntityId() + ",false";
        else
            return "";
    }

    public static UUID getUUID(Entity entity) {
        return entity instanceof EntityPlayer ? ((EntityPlayer) entity).getGameProfile().getId() : entity.getUniqueID();
    }

    public static Entity getFromUUID(UUID id, World w) {
        List<Entity> allEntity = w.loadedEntityList;
        for (Entity entity : allEntity)
            if (entity instanceof EntityPlayer) {
                if (((EntityPlayer) entity).getGameProfile().getId().equals(id))
                    return entity;
            } else if (entity.getUniqueID().equals(id))
                return entity;

        return null;
    }

    @SideOnly(Side.CLIENT)
    public static Entity getFromNameClient(String name) {
        return getFromName(name, Minecraft.getMinecraft().theWorld);
    }

    public static Entity getFromName(String name, World w) {
        List<Entity> allEntity = w.loadedEntityList;
        for (Entity entity : allEntity)
            if (entity.getCommandSenderName().equals(name))
                return entity;


        return null;
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

    public static boolean stackTraceContains(String text) {
        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
            if (ste.toString().toLowerCase().contains(text.toLowerCase()))
                return true;
        return false;
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


    public static String removeBoldColorCode(String s) {
        for (int i = 0; i < s.length() - 2; i++) {
            if (s.charAt(i) == '\u00A7' && (s.charAt(i + 1) == 'l')) {
                return s.substring(0, i) + s.substring(i + 2);
            }
        }
        return s;
    }

    public static String removeColorCodes(String s) {
        StringBuilder sb = new StringBuilder();
        boolean skipNext = false;

        for (int i = 0; i < s.length(); i++) {
            if (skipNext) {
                skipNext = false;
                continue;
            }

            if (s.charAt(i) == '\u00A7' && i + 1 < s.length()) {
                char code = s.charAt(i + 1);
                if (Character.isDigit(code) || "abcdefklmnor".indexOf(code) != -1) {
                    skipNext = true;
                    continue;
                }
            }

            sb.append(s.charAt(i));
        }

        return sb.toString();
    }

    public static HashMap<String, String> darkCodes = new HashMap<>();
    public static String getDarkColorCode(String s) {
        if(darkCodes.isEmpty()){
            darkCodes.put("§a", "§2");
            darkCodes.put("§b", "§9");
            darkCodes.put("§c", "§c");
            darkCodes.put("§e", "§6");
            darkCodes.put("§7", "§8");
            darkCodes.put("§d", "§5");
            darkCodes.put("§f", "§0");
        }
        if(darkCodes.containsKey(s))
            return darkCodes.get(s);
        return s;
    }
}
