package kamkeel.npcdbc.controllers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;

import java.util.HashMap;
import java.util.Iterator;

public class EntityLightController {
    public final Entity entity;
    public HashMap<Vec3, Block> litPositions = new HashMap<>();
    public int lightLevel = 15;
    public int maxDistance = 15;

    public EntityLightController(Entity entity) {
        this.entity = entity;

    }

    public EntityLightController(Entity entity, int lightLevel) {
        this(entity);
        this.lightLevel = lightLevel;

    }

    public void addLitBlockUnder() {
        addLitBlock((int) entity.posX, (int) (entity.posY - entity.yOffset - 1), (int) entity.posZ);
    }

    public void addLitBlock(int posX, int posY, int posZ) {
        Block block = entity.worldObj.getBlock(posX, posY, posZ);
        if (litPositions.containsKey(getVector(posX, posY, posZ)) || block.getMaterial() == Material.air)
            return;
        litPositions.put(Vec3.createVectorHelper(posX, posY, posZ), block);

        entity.worldObj.setLightValue(EnumSkyBlock.Block, posX, posY, posZ, lightLevel);
        entity.worldObj.markBlockRangeForRenderUpdate(posX, posY, posX, 12, 12, 12);
        entity.worldObj.markBlockForUpdate(posX, posY, posZ);
        entity.worldObj.updateLightByType(EnumSkyBlock.Block, posX, posY + 1, posZ);
        entity.worldObj.updateLightByType(EnumSkyBlock.Block, posX, posY - 1, posZ);
        entity.worldObj.updateLightByType(EnumSkyBlock.Block, posX + 1, posY, posZ);
        entity.worldObj.updateLightByType(EnumSkyBlock.Block, posX - 1, posY, posZ);
        entity.worldObj.updateLightByType(EnumSkyBlock.Block, posX, posY, posZ + 1);
        entity.worldObj.updateLightByType(EnumSkyBlock.Block, posX, posY, posZ - 1);
    }

    public void onUpdate() {
        maxDistance = 8;
        Iterator<Vec3> iter = litPositions.keySet().iterator();
        while (iter.hasNext()) {
            Vec3 pos = iter.next();
            double distance = pos.distanceTo(Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ));
            if (distance >= maxDistance) {
                removeLitBlock((int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord);
                iter.remove();
            }


        }
    }

    public Vec3 getVector(int posX, int posY, int posZ) {
        Iterator<Vec3> iter = litPositions.keySet().iterator();
        while (iter.hasNext()) {
            Vec3 pos = iter.next();
            if (pos.xCoord == posX && pos.yCoord == posY && pos.zCoord == posZ)
                return pos;

        }
        return null;
    }

    public void removeLitBlock(int posX, int posY, int posZ) {
        entity.worldObj.updateLightByType(EnumSkyBlock.Block, posX, posY, posZ);

    }
}
