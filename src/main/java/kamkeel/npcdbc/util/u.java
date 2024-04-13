package kamkeel.npcdbc.util;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.List;

//basically utilities, and 'u' is as short as it gets (i am lazy)
public class u {
    public static boolean isServer() {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
    }

    public static void sendMessage(EntityPlayer player, String message) {
        player.addChatMessage(new ChatComponentText(message));
    }

    public static Entity getEntityFromID(World w, String s) { //s is basically getEntityID
        if (!s.isEmpty()) {
            if (Boolean.parseBoolean(s.split(",")[1])) //if player
                return u.getEntityByUUID(w, s.split(",")[0]);
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

    public static double perc(double n, double perc) {
        return n / 100 * perc;
    }

    public static float perc(int n, float perc) {
        return n / 100 * perc;
    }

    public static boolean percBetween(double n, double maxN, double minPerc, double maxPerc) {
        return n >= u.perc(maxN, minPerc) && n < u.perc(maxN, maxPerc);
    }

}
