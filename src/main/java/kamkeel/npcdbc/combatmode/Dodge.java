package kamkeel.npcdbc.combatmode;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class Dodge {
    public static float kiCost = 1;

    public static boolean instantTransDodge(Entity p, Entity tar, double a, double lookinga, double distance, int r1,
                                            int r2, int type) {

        if (InstantTransmission.instantTrans(p, tar, a, lookinga, distance, r1, r2, type)) {
            if (p instanceof EntityPlayer) {
                //DBCUtils.kiCostAsPercentOfMax((EntityPlayer) p, kiCost);
            }
            return true;
        }

        return false;
    }

    public static void dodge(Entity p, Entity t) {
        if (t == null)
            return;

        for (int i = 0; i < 30; i++) {
            if (instantTransDodge(p, t, 10, -180, 1.0, 0, 5, 1))
                break;
            if (instantTransDodge(p, t, 20, -180, 1.15, 5, 10, 2))
                break;
            if (instantTransDodge(p, t, 30, -180, 1.0, 10, 15, 3))
                break;
            if (instantTransDodge(p, t, 60, -180, 1.15, 20, 25, 4))
                break;
            if (instantTransDodge(p, t, 90, -180, 1.35, 25, 30, 5))
                break;
            if (instantTransDodge(p, t, -10, -180, 1.0, 30, 35, 6))
                break;
            if (instantTransDodge(p, t, -20, -180, 1.15, 35, 40, 7))
                break;
            if (instantTransDodge(p, t, -30, -180, 1.0, 40, 45, 8))
                break;
            if (instantTransDodge(p, t, -30, -180, 1.25, 45, 50, 9))
                break;
            if (instantTransDodge(p, t, -40, -180, 1.15, 50, 55, 10))
                break;
            if (instantTransDodge(p, t, -40, -180, 1.65, 55, 60, 11))
                break;
            if (instantTransDodge(p, t, -80, -180, 1.75, 60, 65, 12))
                break;
            if (instantTransDodge(p, t, -60, -180, 1.25, 65, 70, 13))
                break;
            if (instantTransDodge(p, t, 80, -180, 1.75, 70, 75, 14))
                break;
            if (instantTransDodge(p, t, -180, -180, 1.75, 75, 80, 15))
                break;
            if (instantTransDodge(p, t, 140, -180, 1.25, 80, 85, 16))
                break;
            if (instantTransDodge(p, t, -140, -180, 1.25, 85, 90, 17))
                break;
            if (instantTransDodge(p, t, 120, -180, 1.75, 90, 95, 18))
                break;
            if (instantTransDodge(p, t, -120, -180, 1.75, 95, 100, 19))
                break;
        }
    }


}
