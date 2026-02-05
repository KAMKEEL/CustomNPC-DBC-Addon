package kamkeel.npcdbc.combat;

import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.PlaySound;
import net.minecraft.entity.Entity;

import java.util.Random;

public class Dodge {
    public static float kiCost = 1;

    public static boolean instantTransDodge(Entity p, Entity tar, double a, double lookinga, double distance, int r1,
                                            int r2, int type) {
        if (InstantTransmission.instantTrans(p, tar, a, lookinga, distance, r1, r2, type)) {
            int random1 = new Random().nextInt(3) + 1;
            SoundSource soundSource = new SoundSource("jinryuudragonbc:DBC4.dodge" + random1,
                p);
            DBCPacketHandler.Instance.sendTracking(new PlaySound(soundSource),
                soundSource.entity);
            return true;
        }

        return false;
    }

    public static boolean dodge(Entity p, Entity t) {
        if (t == null)
            return false;

        for (int i = 0; i < 30; i++) {
            if (instantTransDodge(p, t, 10, -180, 1.0, 0, 5, 1))
                return true;
            if (instantTransDodge(p, t, 20, -180, 1.15, 5, 10, 2))
                return true;
            if (instantTransDodge(p, t, 30, -180, 1.0, 10, 15, 3))
                return true;
            if (instantTransDodge(p, t, 60, -180, 1.15, 20, 25, 4))
                return true;
            if (instantTransDodge(p, t, 90, -180, 1.35, 25, 30, 5))
                return true;
            if (instantTransDodge(p, t, -10, -180, 1.0, 30, 35, 6))
                return true;
            if (instantTransDodge(p, t, -20, -180, 1.15, 35, 40, 7))
                return true;
            if (instantTransDodge(p, t, -30, -180, 1.0, 40, 45, 8))
                return true;
            if (instantTransDodge(p, t, -30, -180, 1.25, 45, 50, 9))
                return true;
            if (instantTransDodge(p, t, -40, -180, 1.15, 50, 55, 10))
                return true;
            if (instantTransDodge(p, t, -40, -180, 1.65, 55, 60, 11))
                return true;
            if (instantTransDodge(p, t, -80, -180, 1.75, 60, 65, 12))
                return true;
            if (instantTransDodge(p, t, -60, -180, 1.25, 65, 70, 13))
                return true;
            if (instantTransDodge(p, t, 80, -180, 1.75, 70, 75, 14))
                return true;
            if (instantTransDodge(p, t, -180, -180, 1.75, 75, 80, 15))
                return true;
            if (instantTransDodge(p, t, 140, -180, 1.25, 80, 85, 16))
                return true;
            if (instantTransDodge(p, t, -140, -180, 1.25, 85, 90, 17))
                return true;
            if (instantTransDodge(p, t, 120, -180, 1.75, 90, 95, 18))
                return true;
            if (instantTransDodge(p, t, -120, -180, 1.75, 95, 100, 19))
                return true;
        }
        return false;
    }


}
