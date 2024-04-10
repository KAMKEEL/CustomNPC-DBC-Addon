package kamkeel.npcdbc.util;

import JinRyuu.JRMCore.JRMCoreH;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class StatHelper {

    public static int[] getStats(EntityPlayer player) {
        NBTTagCompound nbt = JRMCoreH.nbt(player);
        int[] stats = new int[6];
        String[] attr = { "jrmcStrI", "jrmcDexI", "jrmcCnsI", "jrmcWilI", "jrmcIntI", "jrmcCncI" };

        for (int i = 0; i < attr.length; i++) {
            stats[i] = nbt.getInteger(attr[i]);
        }

        return stats;
    }

    public static int[] getFusionStats(EntityPlayer player1, EntityPlayer player2){
        int[] stats1 = getStats(player1);
        int[] stats2 = getStats(player2);

        int[] fusedStats = new int[6];

        for(int i = 0; i < stats1.length; i++){
            fusedStats[i] = Math.min(stats1[i], stats2[i]) * 2;
        }

        return fusedStats;
    }
}
