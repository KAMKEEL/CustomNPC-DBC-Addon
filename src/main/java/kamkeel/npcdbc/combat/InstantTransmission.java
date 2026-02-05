package kamkeel.npcdbc.combat;

import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.scripted.NpcAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InstantTransmission {
    public static Map<Entity, Integer> itType = new HashMap<Entity, Integer>();

    public static boolean instantTrans(Entity p, Entity tar, double a, double lookinga, double distance, int r1, int r2,
                                       int type) {

        if (!differentFromLast(p, type))
            return false;

        Random random = new Random();
        int rand = random.nextInt(100) + 1;
        if (rand >= r1 && rand <= r2) {
            double angle = tar.rotationYaw + a;
            double dx = -Math.sin((angle * Math.PI) / 180) * distance;
            double dz = Math.cos((angle * Math.PI) / 180) * distance;
            if (blockSuitable(tar, (int) (tar.posX + dx), (int) tar.posY, (int) (tar.posZ + dz))) {
                IEntity<?> pl = NpcAPI.Instance().getIEntity(p);
                if (ConfigDBCGameplay.DodgeTeleport) {
                    pl.setPosition(tar.posX + dx, tar.posY, tar.posZ + dz); // y needs fixing
                    if (pl.getDimension() != tar.dimension)
                        pl.setDimension(tar.dimension);
                }
                if (ConfigDBCGameplay.DodgeCameraLock) pl.setRotation((float) (angle + lookinga));


                itType.replace(p, type);
                return true;
            }
        }
        return false;
    }

    public static boolean blockSuitable(Entity tar, int x, int y, int z) {
        World w = tar.worldObj;
        Block b1 = w.getBlock(x, y, z);
        Block b2 = w.getBlock(x, y + 1, z);

        boolean bo1 = b1.getMaterial() == Material.air || b1 instanceof BlockTallGrass;
        boolean bo2 = b2.getMaterial() == Material.air || b2 instanceof BlockTallGrass;

        if (bo1 && bo2)
            return true;

        return false;
    }

    public static void doIT(Entity p) {
        Entity t = null;//Target.getCurrentTar(p);
        if (t == null) {
            Utility.sendMessage(p, "No target to teleport to.");
            return;
        }

        for (int i = 0; i < 30; i++) {
            if (instantTrans(p, t, 0d, -180d, 2.0d, 0, 25, 1))
                break;
            if (instantTrans(p, t, -180, -180, 2.0, 25, 50, 2))
                break;
            if (instantTrans(p, t, 90, -180, 2.0, 50, 75, 3))
                break;
            if (instantTrans(p, t, -90, -180, 2.0, 75, 100, 4))
                break;
        }
    }

    public static boolean differentFromLast(Entity p, int type) {
        if (!itType.containsKey(p))
            itType.put(p, 0);

        if (itType.get(p) != type)
            return true;

        return false;
    }
}
