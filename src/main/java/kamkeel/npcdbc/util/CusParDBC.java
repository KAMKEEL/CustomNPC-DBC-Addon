package kamkeel.npcdbc.util;

import JinRyuu.JRMCore.entity.EntityCusPar;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

public class CusParDBC {
    public int color, color2 = -1, color3 = -1, age = 50;
    public Entity entity;
    public EntityCusPar particle;
    public float width, height, offset, extraScale = 0.5f, life, posY, alpha;
    public double motX, motY, motZ;

    public boolean rotate;
    public float rotationSpeed, maxRotationSpeed;
    public int u, v;

    public CusParDBC(Entity entity) {
        this.entity = entity;

        setLife(0.8F * height).setMotionY(0.05 + Math.random() * 0.1);
        posY = (float) (entity.posY + (entity instanceof EntityPlayerSP ? -1.62 : 0.0));

    }

    public void spawn() {
        float red = color >> 16 & 255;
        float green = color >> 8 & 255;
        float blue = color & 255;

        float red2 = 0, green2 = 0, blue2 = 0, red3 = 0, green3 = 0, blue3 = 0;
        if (color2 != -1) {
            red2 = color2 >> 16 & 255;
            green2 = color2 >> 8 & 255;
            blue2 = color2 & 255;
        }
        if (color3 != -1) {
            red3 = color3 >> 16 & 255;
            green3 = color3 >> 8 & 255;
            blue3 = color3 & 255;
        }

        double x = offset + (Math.random() - 0.5) * width;
        double y = Math.random() * (double) height - 0.5;
        double z = offset + (Math.random() - 0.5) * width;
        particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, motX, motY, motZ, 0.0F, (int) (Math.random() * 3.0) + 32, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", (int) (age * life), 2, ((float) (Math.random() * 0.03) + 0.03F) * life * extraScale, ((float) (Math.random() * 0.01) + 0.02F) * life * extraScale, 0.2F * life * extraScale, 0, red, green, blue, red2, green2, blue2, red3, green3, blue3, 2, alpha, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, entity);
        entity.worldObj.spawnEntityInWorld(particle);

        x = -offset + (Math.random() - 0.5) * width;
        y = Math.random() * (double) height - 0.5;
        z = -offset + (Math.random() - 0.5) * width;
        particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, motX, motY, motZ, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", (int) (age * life), 2, ((float) (Math.random() * 0.03) + 0.03F) * life * extraScale, ((float) (Math.random() * 0.01) + 0.02F) * life * extraScale, 0.1F * life * extraScale, 0, red, green, blue, red2, green2, blue2, red3, green3, blue3, 2, alpha, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, entity);
        entity.worldObj.spawnEntityInWorld(particle);
    }

    public CusParDBC setMotionY(double motY) {
        this.motY = motY;
        return this;
    }

    public CusParDBC setLife(float life) {
        this.life = life;
        return this;
    }

    public CusParDBC setHeight(float height) {
        this.height = height;
        return this;
    }

    public CusParDBC setWidth(float width) {
        this.width = width;
        return this;
    }

    public CusParDBC setColor(int color) {
        this.color = color;
        return this;
    }

    public CusParDBC setAge(int age) {
        this.age = age;
        return this;
    }
}
