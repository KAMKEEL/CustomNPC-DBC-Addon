package kamkeel.npcdbc.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import noppes.npcs.api.IPos;
import noppes.npcs.scripted.NpcAPI;

import java.util.*;

public class RaycastUtil {

    public static Entity[] getEntitiesNear(World world, double x, double y, double z, double range) {
        List<Entity> list = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(
            x - range, y - range, z - range,
            x + range, y + range, z + range));

        IPos position = NpcAPI.Instance().getIPos(x, y, z);

        list.sort((e1, e2) -> {
            IPos pos1 = NpcAPI.Instance().getIPos(e1.posX, e1.posY, e1.posZ);
            IPos pos2 = NpcAPI.Instance().getIPos(e2.posX, e2.posY, e2.posZ);

            double dist1 = pos1.distanceTo(position);
            double dist2 = pos2.distanceTo(position);

            if (dist1 > dist2) {
                return 1;
            } else if (dist1 < dist2) {
                return -1;
            }
            return 0;
        });

        return list.toArray(new Entity[0]);
    }

    public static Entity[] getLookingAtEntities(EntityLivingBase entity, Entity[] ignoreEntities, int maxDistance, double offset, double range, boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
        Vec3 lookVec = entity.getLookVec();
        double[] startPos = new double[]{entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ};
        double[] lookVector = new double[]{lookVec.xCoord, lookVec.yCoord, lookVec.zCoord};

        if (ignoreEntities == null) {
            ignoreEntities = new Entity[0];
        }
        ArrayList<Entity> entities = new ArrayList<>();
        entities.add(entity);
        Collections.addAll(entities, ignoreEntities);

        return rayCastEntities(entity.worldObj, entities.toArray(new Entity[0]), startPos, lookVector, maxDistance, offset, range, stopOnBlock, stopOnLiquid, stopOnCollision);
    }

    public static Vec3 getLookingAtPos(EntityLivingBase entity, int maxDistance, boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
        Vec3 lookVec = entity.getLookVec();
        return rayCastPos(
            entity.worldObj,
            new double[]{entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ},
            new double[]{lookVec.xCoord, lookVec.yCoord, lookVec.zCoord},
            maxDistance, stopOnBlock, stopOnLiquid, stopOnCollision);
    }


    public static MovingObjectPosition rayCast(World world, Vec3 startVec, Vec3 endVec, boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
        if (!Double.isNaN(startVec.xCoord) && !Double.isNaN(startVec.yCoord) && !Double.isNaN(startVec.zCoord)) {
            if (!Double.isNaN(endVec.xCoord) && !Double.isNaN(endVec.yCoord) && !Double.isNaN(endVec.zCoord)) {
                int endX = MathHelper.floor_double(endVec.xCoord);
                int endY = MathHelper.floor_double(endVec.yCoord);
                int endZ = MathHelper.floor_double(endVec.zCoord);
                int l = MathHelper.floor_double(startVec.xCoord);
                int i1 = MathHelper.floor_double(startVec.yCoord);
                int j1 = MathHelper.floor_double(startVec.zCoord);
                Block block = world.getBlock(l, i1, j1);
                int k1 = world.getBlockMetadata(l, i1, j1);

                if (block.canCollideCheck(k1, false)) {
                    MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, l, i1, j1, startVec, endVec);

                    if (movingobjectposition != null) {
                        return movingobjectposition;
                    }
                }

                k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(startVec.xCoord) || Double.isNaN(startVec.yCoord) || Double.isNaN(startVec.zCoord)) {
                        return null;
                    }

                    if (l == endX && i1 == endY && j1 == endZ) {
                        return null;
                    }

                    boolean flag6 = true;
                    boolean flag3 = true;
                    boolean flag4 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (endX > l) {
                        d0 = (double) l + 1.0D;
                    } else if (endX < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag6 = false;
                    }

                    if (endY > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (endY < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag3 = false;
                    }

                    if (endZ > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (endZ < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag4 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = endVec.xCoord - startVec.xCoord;
                    double d7 = endVec.yCoord - startVec.yCoord;
                    double d8 = endVec.zCoord - startVec.zCoord;

                    if (flag6) {
                        d3 = (d0 - startVec.xCoord) / d6;
                    }

                    if (flag3) {
                        d4 = (d1 - startVec.yCoord) / d7;
                    }

                    if (flag4) {
                        d5 = (d2 - startVec.zCoord) / d8;
                    }

                    byte b0;

                    if (d3 < d4 && d3 < d5) {
                        if (endX > l) {
                            b0 = 4;
                        } else {
                            b0 = 5;
                        }

                        startVec.xCoord = d0;
                        startVec.yCoord += d7 * d3;
                        startVec.zCoord += d8 * d3;
                    } else if (d4 < d5) {
                        if (endY > i1) {
                            b0 = 0;
                        } else {
                            b0 = 1;
                        }

                        startVec.xCoord += d6 * d4;
                        startVec.yCoord = d1;
                        startVec.zCoord += d8 * d4;
                    } else {
                        if (endZ > j1) {
                            b0 = 2;
                        } else {
                            b0 = 3;
                        }

                        startVec.xCoord += d6 * d5;
                        startVec.yCoord += d7 * d5;
                        startVec.zCoord = d2;
                    }

                    Vec3 vec32 = Vec3.createVectorHelper(startVec.xCoord, startVec.yCoord, startVec.zCoord);
                    l = (int) (vec32.xCoord = (double) MathHelper.floor_double(startVec.xCoord));

                    if (b0 == 5) {
                        --l;
                        ++vec32.xCoord;
                    }

                    i1 = (int) (vec32.yCoord = (double) MathHelper.floor_double(startVec.yCoord));

                    if (b0 == 1) {
                        --i1;
                        ++vec32.yCoord;
                    }

                    j1 = (int) (vec32.zCoord = (double) MathHelper.floor_double(startVec.zCoord));

                    if (b0 == 3) {
                        --j1;
                        ++vec32.zCoord;
                    }

                    Block block1 = world.getBlock(l, i1, j1);
                    int l1 = world.getBlockMetadata(l, i1, j1);

                    MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, l, i1, j1, startVec, endVec);
                    if (movingobjectposition1 != null) {
                        if (block1.canCollideCheck(l1, false) && stopOnBlock ||
                            block1.getMaterial().isLiquid() && stopOnLiquid ||
                            !(block1 instanceof BlockAir) && block1.getCollisionBoundingBoxFromPool(world, l, i1, j1) == null && !stopOnCollision) {
                            return movingobjectposition1;
                        }
                    }
                }

                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Vec3 rayCastPos(World world, double[] startPos, double[] lookVector, int maxDistance, boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
        if (startPos.length != 3 || lookVector.length != 3) {
            return null;
        }

        Vec3 startVec = Vec3.createVectorHelper(startPos[0], startPos[1], startPos[2]);
        Vec3 endVec = startVec.addVector(lookVector[0] * maxDistance, lookVector[1] * maxDistance, lookVector[2] * maxDistance);

        if (!stopOnBlock && !stopOnLiquid && !stopOnCollision) {
            return endVec;
        }

        MovingObjectPosition mob = rayCast(world, startVec, endVec, stopOnBlock, stopOnLiquid, stopOnCollision);
        return mob != null ? Vec3.createVectorHelper(mob.blockX, mob.blockY, mob.blockZ) : endVec;
    }

    public static Entity[] rayCastEntities(World world, Entity[] ignoreEntities, double[] startPos, double[] lookVector,
                                     int maxDistance, double offset, double range,
                                     boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
        if (ignoreEntities == null) {
            ignoreEntities = new Entity[0];
        }

        Vec3 startVec = Vec3.createVectorHelper(startPos[0], startPos[1], startPos[2]);
        Vec3 endVec = startVec.addVector(lookVector[0] * maxDistance, lookVector[1] * maxDistance, lookVector[2] * maxDistance);
        startVec = startVec.addVector(lookVector[0] * offset, lookVector[1] * offset, lookVector[2] * offset);

        LinkedHashSet<Entity> ignoredEntitiesSet = new LinkedHashSet<>();
        Collections.addAll(ignoredEntitiesSet, ignoreEntities);

        Set<Entity> entities = rayCastEntities(world, ignoredEntitiesSet, startVec, endVec, range, stopOnBlock, stopOnLiquid, stopOnCollision);
        return entities.toArray(new Entity[0]);
    }

    public static Set<Entity> rayCastEntities(World world, LinkedHashSet<Entity> ignoredEntitiesSet, Vec3 startVec, Vec3 endVec, double range, boolean stopOnBlock, boolean stopOnLiquid, boolean stopOnCollision) {
        LinkedHashSet<Entity> entities = new LinkedHashSet<>();

        if (!Double.isNaN(startVec.xCoord) && !Double.isNaN(startVec.yCoord) && !Double.isNaN(startVec.zCoord)) {
            if (!Double.isNaN(endVec.xCoord) && !Double.isNaN(endVec.yCoord) && !Double.isNaN(endVec.zCoord)) {
                int endX = MathHelper.floor_double(endVec.xCoord);
                int endY = MathHelper.floor_double(endVec.yCoord);
                int endZ = MathHelper.floor_double(endVec.zCoord);
                int l = MathHelper.floor_double(startVec.xCoord);
                int i1 = MathHelper.floor_double(startVec.yCoord);
                int j1 = MathHelper.floor_double(startVec.zCoord);
                Block block = world.getBlock(l, i1, j1);
                int k1 = world.getBlockMetadata(l, i1, j1);

                Entity[] surrounding = getEntitiesNear(world, l, i1, j1, range);
                for (Entity entity : surrounding) {
                    if (!ignoredEntitiesSet.contains(entity)) {
                        entities.add(entity);
                    }
                }

                if (block.canCollideCheck(k1, false)) {
                    MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, l, i1, j1, startVec, endVec);

                    if (movingobjectposition != null) {
                        return entities;
                    }
                }

                k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(startVec.xCoord) || Double.isNaN(startVec.yCoord) || Double.isNaN(startVec.zCoord)) {
                        return entities;
                    }

                    if (l == endX && i1 == endY && j1 == endZ) {
                        return entities;
                    }

                    boolean flag6 = true;
                    boolean flag3 = true;
                    boolean flag4 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (endX > l) {
                        d0 = (double) l + 1.0D;
                    } else if (endX < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag6 = false;
                    }

                    if (endY > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (endY < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag3 = false;
                    }

                    if (endZ > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (endZ < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag4 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = endVec.xCoord - startVec.xCoord;
                    double d7 = endVec.yCoord - startVec.yCoord;
                    double d8 = endVec.zCoord - startVec.zCoord;

                    if (flag6) {
                        d3 = (d0 - startVec.xCoord) / d6;
                    }

                    if (flag3) {
                        d4 = (d1 - startVec.yCoord) / d7;
                    }

                    if (flag4) {
                        d5 = (d2 - startVec.zCoord) / d8;
                    }

                    byte b0;

                    if (d3 < d4 && d3 < d5) {
                        if (endX > l) {
                            b0 = 4;
                        } else {
                            b0 = 5;
                        }

                        startVec.xCoord = d0;
                        startVec.yCoord += d7 * d3;
                        startVec.zCoord += d8 * d3;
                    } else if (d4 < d5) {
                        if (endY > i1) {
                            b0 = 0;
                        } else {
                            b0 = 1;
                        }

                        startVec.xCoord += d6 * d4;
                        startVec.yCoord = d1;
                        startVec.zCoord += d8 * d4;
                    } else {
                        if (endZ > j1) {
                            b0 = 2;
                        } else {
                            b0 = 3;
                        }

                        startVec.xCoord += d6 * d5;
                        startVec.yCoord += d7 * d5;
                        startVec.zCoord = d2;
                    }

                    Vec3 vec32 = Vec3.createVectorHelper(startVec.xCoord, startVec.yCoord, startVec.zCoord);
                    l = (int) (vec32.xCoord = (double) MathHelper.floor_double(startVec.xCoord));

                    if (b0 == 5) {
                        --l;
                        ++vec32.xCoord;
                    }

                    i1 = (int) (vec32.yCoord = (double) MathHelper.floor_double(startVec.yCoord));

                    if (b0 == 1) {
                        --i1;
                        ++vec32.yCoord;
                    }

                    j1 = (int) (vec32.zCoord = (double) MathHelper.floor_double(startVec.zCoord));

                    if (b0 == 3) {
                        --j1;
                        ++vec32.zCoord;
                    }

                    Block block1 = world.getBlock(l, i1, j1);
                    int l1 = world.getBlockMetadata(l, i1, j1);

                    Entity[] surroundingEntities = getEntitiesNear(world, l, i1, j1, range);
                    for (Entity entity : surroundingEntities) {
                        if (!ignoredEntitiesSet.contains(entity)) {
                            entities.add(entity);
                        }
                    }

                    MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, l, i1, j1, startVec, endVec);
                    if (movingobjectposition1 != null) {
                        if (block1.canCollideCheck(l1, false) && stopOnBlock ||
                            block1.getMaterial().isLiquid() && stopOnLiquid ||
                            !(block1 instanceof BlockAir) && block1.getCollisionBoundingBoxFromPool(world, l, i1, j1) == null && !stopOnCollision) {
                            return entities;
                        }
                    }
                }

                return entities;
            } else {
                return entities;
            }
        } else {
            return entities;
        }
    }
}
