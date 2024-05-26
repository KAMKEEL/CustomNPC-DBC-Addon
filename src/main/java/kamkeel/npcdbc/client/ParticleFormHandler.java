package kamkeel.npcdbc.client;

import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import JinRyuu.JRMCore.entity.EntityCusPar;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.data.IAuraData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

public class ParticleFormHandler {
    public static void spawnAura2D(DBCDisplay display) {
        if (display != null && display.auraEntity != null)
            spawnAura2D(display.npc, display.auraEntity.auraData, display.npc.height);
    }


    public static void spawnAura2D(Entity entity, IAuraData data, float height) {
        if (!entity.worldObj.isRemote || !JGConfigClientSettings.CLIENT_DA8 || data == null || data.getAuraEntity() == null)
            return;

        EnumAuraTypes2D types = data.getAuraEntity().type2D;
        int color = data.getAuraEntity().color1;
        float numberOfParticles = EnumAuraTypes2D.getParticleWidth(data);
        
        switch (types) {
            case Base:
                for (int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                    double posY = entity.posY + (entity instanceof EntityPlayerSP ? 2 : 0.0);
                    float red = color >> 16 & 255;
                    float green = color >> 8 & 255;
                    float blue = color & 255;

                    for (float i = 0; i < numberOfParticles; ++i) {
                        float life = 0.8f * height;
                        float extra_scale = 1.0F + (height > 2.1F ? height / 2.0F : 0.0F) / 5.0F;
                        float width = height * 0.7f;
                        double x = (Math.random() - 0.5) * (width * 1.3D);
                        double y = Math.random() * (height * 1.4D) - (height / 2.0D) - 0.30000001192092896 + 0.5;
                        double z = (Math.random() - 0.5) * (width * 1.3D);
                        double motx = Math.random() * 0.019999999552965164 - 0.009999999776482582;
                        double moty = (Math.random() * 0.8999999761581421 + 0.8999999761581421) * (double) (life * extra_scale) * 0.07;
                        double motz = Math.random() * 0.019999999552965164 - 0.009999999776482582;
                        Entity entity2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, motx, moty, motz, 0.0F, (int) (Math.random() * 3.0) + 32, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", (int) (30.0F * life * 0.5F), 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.2F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, entity);
                        entity.worldObj.spawnEntityInWorld(entity2);
                        entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, motx, moty, motz, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", (int) (30.0F * life * 0.5F), 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.1F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, entity);
                        entity.worldObj.spawnEntityInWorld(entity2);
                    }
                }
                break;
            case SaiyanGod:
                for (int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                    double posY = entity.posY + 1.6;
                    float red, green, blue, alpha = 0;
                    double x, y, z;
                    float life = 0.8F * height;
                    int i;


                    float offset = 0;
                    float width = height * 1f;
                    red = 255.0F;
                    green = 220.0F;
                    blue = 200.0F;
                    EntityCusPar particle2;
                    for (i = 0; i < 2; ++i) { //super light orange sparkles
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, 0.05 + Math.random() * 0.10000000149011612, 0.0, 0.0F, (int) (Math.random() * 8.0) + 48, 48, 8, 32, false, 0.0F, false, 0.0F, 1, "", 25, 0, 0.003F + (float) (Math.random() * 0.006000000052154064), 0.0F, 0.0F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.8F, 0.0F, 0.9F, 0.95F, 0.02F, false, -1, false, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }
                    red = 215.0F;
                    green = 107.0F;
                    blue = 61f;
                    for (i = 0; i < 1; ++i) { //orange
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;

                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, alpha, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2.worldObj.spawnEntityInWorld(particle2);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, alpha, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }
                    red = 250;
                    green = 244;
                    blue = 240;
                    alpha = 0.9f;
                    for (i = 0; i < 2; ++i) { //white orange 
                        life = 0.3F * height;
                        width = height * 0.5f;
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        Entity particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, alpha, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;

                        particle.worldObj.spawnEntityInWorld(particle);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, alpha, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        particle.worldObj.spawnEntityInWorld(particle2);
                    }

                    red = 218.0F;
                    green = 209.0F;
                    blue = 71.0F;
                    offset = 0.1f;
                    for (i = 0; i < 1; ++i) { //yellow
                        life = 0.5F * height;
                        width = height * 0.6f;
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        Entity particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;

                        particle.worldObj.spawnEntityInWorld(particle);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        particle.worldObj.spawnEntityInWorld(particle2);
                    }
                    red = 179F;
                    green = 2F;
                    blue = 2;
                    offset = 0.1f;
                    width = height * 1f;
                    for (i = 0; i < 1; ++i) { //red
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        Entity particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        x = -offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = -offset + (Math.random() - 0.5) * width;

                        particle.worldObj.spawnEntityInWorld(particle);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        particle.worldObj.spawnEntityInWorld(particle2);
                    }
                }
                break;
            case SaiyanBlue:
                for (int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                    double posY = entity.posY + 1.6;
                    float red, green, blue, alpha = 0;
                    double x, y, z;
                    float life = 0.8F * height;
                    float offset = 0;
                    float width = height * 1f;
                    EntityCusPar particle2;
                    for (int i = 0; i < 3; ++i) {
                        float spe2 = 1.3F;
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, 0.05 + Math.random() * 0.10000000149011612, 0.0, 0.0F, (int) (Math.random() * 8.0) + 48, 48, 8, 32, false, 0.0F, false, 0.0F, 1, "", 25, 0, 0.003F + (float) (Math.random() * 0.006000000052154064), 0.0F, 0.0F, 0, 160.0F, 220.0F, 255.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.8F, 0.0F, 0.9F, 0.95F, 0.02F, false, -1, false, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }
                    for (int i = 0; i < 2; ++i) {
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 48.0F, 208.0F, 232.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2.worldObj.spawnEntityInWorld(particle2);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 48.0F, 208.0F, 232.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }
                    red = 250;
                    green = 244;
                    blue = 240;
                    alpha = 0.9f;
                    for (int i = 0; i < 2; ++i) { //white  
                        life = 0.4F * height;
                        width = height * 0.5f;
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        Entity particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, alpha, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;

                        particle.worldObj.spawnEntityInWorld(particle);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, alpha, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        particle.worldObj.spawnEntityInWorld(particle2);
                    }
                    red = 46.0F;
                    green = 46.0F;
                    blue = 211.0F;
                    offset = 0.05f;
                    for (int i = 0; i < 1; ++i) {   //dark blue
                        width = height * 1.2f;
                        life = 0.6F * height;
                        x = offset + (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        x = -offset + (Math.random() - 0.5) * width;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = -offset + (Math.random() - 0.5) * width;
                        particle2.worldObj.spawnEntityInWorld(particle2);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }
                    red = 76;
                    green = 59;
                    blue = 238;
                    offset = 0.05f;
                    for (int i = 0; i < 0; ++i) {   //dark purple
                        width = height * 1f;
                        life = 0.3F * height;
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2.worldObj.spawnEntityInWorld(particle2);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }
                    red = 223.0F;
                    green = 94;
                    blue = 243;
                    life = height * 0.6f;
                    offset = 0.05f;
                    for (int i = 0; i < 1; ++i) {   //pink
                        width = height * 0.8f;
                        life = 0.3F * height;
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2.worldObj.spawnEntityInWorld(particle2);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }
                    red = 127;
                    green = 88;
                    blue = 242;
                    life = height * 0.6f;
                    offset = 0.05f;
                    for (int i = 0; i < 0; ++i) {   //purple
                        width = height * 0.6f;
                        life = 0.3F * height;
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2.worldObj.spawnEntityInWorld(particle2);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }
                    red = 179F;
                    green = 2F;
                    blue = 2;
                    offset = 0.1f;
                    width = height * 0.5f;
                    life = 0.3F * height;
                    for (int i = 0; i < 0; ++i) { //red
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        Entity particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        x = -offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = -offset + (Math.random() - 0.5) * width;

                        particle.worldObj.spawnEntityInWorld(particle);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        particle.worldObj.spawnEntityInWorld(particle2);
                    }
                    x = offset + (Math.random() - 0.5) * width;
                    y = Math.random() * (double) height - 0.5;
                    z = offset + (Math.random() - 0.5) * width;
                    Entity particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 160.0F, 220.0F, 255.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                    x = offset + (Math.random() - 0.5) * width;
                    y = Math.random() * (double) height - 0.5;
                    z = offset + (Math.random() - 0.5) * width;
                    particle.worldObj.spawnEntityInWorld(particle);
                    particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 160.0F, 220.0F, 255.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                   // particle.worldObj.spawnEntityInWorld(particle2);
                }
                break;
            case SaiyanBlueEvo:
                for (int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                    float red, green, blue,alpha;
                    double x, y, z;

                    float life = 0.8F * height;
                    float width = height * 0.7f;

                    Entity particle2;
                    double posY = entity.posY + (entity instanceof EntityPlayerSP ? -1.6f : 0.0);

                    red = 80.0F;
                    green = 179.0F;
                    blue = 215.0F;

                    red = 160.0F;
                    green = 220.0F;
                    blue = 255.0F;
                    red = 246;
                    green = 246;
                    blue = 246;
                    float offset = 0;
                    alpha = 0.75f;
                    for (int i = 0; i < 3; ++i) { //super light blue particles
                        // x = Math.random() * 1.600000023841858 - 0.800000011920929;
                        width = height * 0.6f * 0.8f;
                        life = 0.5F * height;
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, alpha, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        x = offset + (Math.random() - 0.5) * width;
                        y = Math.random() * (double) height - 0.5;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2.worldObj.spawnEntityInWorld(particle2);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2,alpha, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }


                    red = 46.0F;
                    green = 46.0F;
                    blue = 211.0F;
                    life = height * 0.6f;
                    offset = 0.1f;
                    for (int i = 0; i < 1; ++i) {   //dark blue
                        width = height * 0.9f;
                        life = 0.8F * height;

                        x = offset + (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        x = -offset + (Math.random() - 0.5) * width;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = -offset + (Math.random() - 0.5) * width;
                        particle2.worldObj.spawnEntityInWorld(particle2);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }

                    red = 80.0F;
                    green = 179.0F;
                    blue = 215.0F;
                    offset = 0.15f;
                    for (int i = 0; i < 1; ++i) {   //cyan
                        width = height * 0.7f;
                        x = offset + (Math.random() - 0.5) * width;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = offset + (Math.random() - 0.5) * width;
                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        x = -offset + (Math.random() - 0.5) * width;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = -offset + (Math.random() - 0.5) * width;
                        particle2.worldObj.spawnEntityInWorld(particle2);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }
                }
                break;
            case SaiyanRose:
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        float out = 1.6F;
                        float in = 1.0F;
                        float life = 0.8F * height;
                        double x;
                        double y;
                        double z;
                        EntityCusPar particle2;
                        for(int gh = 0; gh < 2; ++gh) {
                            x = Math.random() * (double)out - (double)(out / 2.0F);
                            y = Math.random() * (double) height - 0.5;
                            z = Math.random() * (double)out - (double)(out / 2.0F);
                            particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 186.0F, 37.0F, 197.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) entity);
                            x = Math.random() * (double)out - (double)(out / 2.0F);
                            y = Math.random() * (double) height - 0.5;
                            z = Math.random() * (double)out - (double)(out / 2.0F);
                            particle2.worldObj.spawnEntityInWorld(particle2);
                            particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 186.0F, 37.0F, 197.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) entity);
                            particle2.worldObj.spawnEntityInWorld(particle2);
                        }

                        out *= 1.4F;
                        x = Math.random() * (double)out - (double)(out / 2.0F);
                        y = Math.random() * (double) height - 0.5;
                        z = Math.random() * (double)out - (double)(out / 2.0F);
                        Entity particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 140.0F, 8.0F, 62.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) entity);
                        x = Math.random() * (double)out - (double)(out / 2.0F);
                        y = Math.random() * (double) height - 0.5;
                        z = Math.random() * (double)out - (double)(out / 2.0F);
                        particle.worldObj.spawnEntityInWorld(particle);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 140.0F, 8.0F, 62.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) entity);
                        particle.worldObj.spawnEntityInWorld(particle2);
                        x = Math.random() * (double)in - (double)(in / 2.0F);
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = Math.random() * (double)in - (double)(in / 2.0F);
                        particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 184.0F, 147.0F, 241.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) entity);
                        x = Math.random() * (double)in - (double)(in / 2.0F);
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = Math.random() * (double)in - (double)(in / 2.0F);
                        particle.worldObj.spawnEntityInWorld(particle);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 184.0F, 147.0F, 241.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) entity);
                        particle.worldObj.spawnEntityInWorld(particle2);
                        in *= 1.2F;
                        x = Math.random() * (double)in - (double)(in / 2.0F);
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = Math.random() * (double)in - (double)(in / 2.0F);
                        particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 93.0F, 3.0F, 177.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) entity);
                        x = Math.random() * (double)in - (double)(in / 2.0F);
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = Math.random() * (double)in - (double)(in / 2.0F);
                        particle.worldObj.spawnEntityInWorld(particle);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 93.0F, 3.0F, 177.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) entity);
                        particle.worldObj.spawnEntityInWorld(particle2);
                    }
                break;

            case Legendary:
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        for(int i = 0; i < 5; ++i) {
                            float red = 183.0F;
                            float green = 205.0F;
                            float blue = 97.0F;
                            float life = 0.8F * height;
                            float extra_scale = 1.0F + (height > 2.1F ? height / 2.0F : 0.0F) / 5.0F;
                            float width = entity.width * 3.0F;
                            double x = (Math.random() - 0.5) * (double)(width * 1.2F);
                            double y = Math.random() * (double) (height * 1.4F) - (double) (height / 2.0F) - 0.30000001192092896;
                            double z = (Math.random() - 0.5) * (double)(width * 1.2F);
                            double motx = Math.random() * 0.019999999552965164 - 0.009999999776482582;
                            double moty = (Math.random() * 0.8999999761581421 + 0.8999999761581421) * (double)(life * extra_scale) * 0.07;
                            double motz = Math.random() * 0.019999999552965164 - 0.009999999776482582;
                            Entity particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY + (double) (0.0F), entity.posZ, x, y, z, motx, moty, motz, 0.0F, (int) (Math.random() * 3.0) + 32, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", (int) (30.0F * life * 0.5F), 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.2F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, entity);
                            entity.worldObj.spawnEntityInWorld(particle2);
                            particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY + (double) (0.0F), entity.posZ, x, y, z, motx, moty, motz, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", (int) (30.0F * life * 0.5F), 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.1F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, entity);
                            entity.worldObj.spawnEntityInWorld(particle2);
                        }
                    }
                break;
            case SaiyanSuper:
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        for(int i = 0; i < 5; ++i) {
                            float red = 255.0F;
                            float green = 217.0F;
                            float blue = 25.0F;
                            float life = 0.8F * height;
                            float extra_scale = 1.0F + (height > 2.1F ? height / 2.0F : 0.0F) / 5.0F;
                            float width = entity.width * 3.0F;
                            double x = (Math.random() - 0.5) * (double)(width * 1.2F);
                            double y = Math.random() * (double) (height * 1.4F) - (double) (height / 2.0F) - 0.30000001192092896;
                            double z = (Math.random() - 0.5) * (double)(width * 1.2F);
                            double motx = Math.random() * 0.019999999552965164 - 0.009999999776482582;
                            double moty = (Math.random() * 0.8999999761581421 + 0.8999999761581421) * (double)(life * extra_scale) * 0.07;
                            double motz = Math.random() * 0.019999999552965164 - 0.009999999776482582;
                            Entity particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY + (double) (0.0F), entity.posZ, x, y, z, motx, moty, motz, 0.0F, (int) (Math.random() * 3.0) + 32, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", (int) (30.0F * life * 0.5F), 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.2F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, entity);
                            entity.worldObj.spawnEntityInWorld(particle2);
                            particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY + (double) (0.0F), entity.posZ, x, y, z, motx, moty, motz, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", (int) (30.0F * life * 0.5F), 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.1F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, entity);
                            entity.worldObj.spawnEntityInWorld(particle2);
                        }
                    }
                break;


            case UltimateArco:
                for (int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                    float out = 1.6F;
                    float life = 0.8F * height;
                    int i;
                    double x;
                    double y;
                    double z;
                    EntityCusPar particle2;
                    for (i = 0; i < 2; ++i) {
                        x = Math.random() * (double) out - (double) (out / 2.0F);
                        y = Math.random() * (double) height - 0.5;
                        z = Math.random() * (double) out - (double) (out / 2.0F);
                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 249.0F, 212.0F, 33.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        x = Math.random() * (double) out - (double) (out / 2.0F);
                        y = Math.random() * (double) height - 0.5;
                        z = Math.random() * (double) out - (double) (out / 2.0F);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 249.0F, 212.0F, 33.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }

                    for (i = 0; i < 2; ++i) {
                        out *= 1.3F;
                        x = Math.random() * (double) out - (double) (out / 2.0F);
                        y = Math.random() * (double) height - 0.5;
                        z = Math.random() * (double) out - (double) (out / 2.0F);
                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 234.0F, 134.0F, 34.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        x = Math.random() * (double) out - (double) (out / 2.0F);
                        y = Math.random() * (double) height - 0.5;
                        z = Math.random() * (double) out - (double) (out / 2.0F);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 234.0F, 134.0F, 34.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);
                    }

                    x = Math.random() - 0.5;
                    y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                    z = Math.random() - 0.5;
                    Entity particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 255.0F, 255.0F, 208.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                    x = Math.random() - 0.5;
                    y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                    z = Math.random() - 0.5;
                    particle.worldObj.spawnEntityInWorld(particle);
                    particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 255.0F, 255.0F, 208.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, entity);
                    particle.worldObj.spawnEntityInWorld(particle2);
                }
                break;
            case UI:
                for (int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                    float red, red2, green2, blue3, green, blue, alpha = 0.7f;
                    double x, y, z;
                    float life = 0.8F * height;
                    float offset = 0;

                    float width = height * 0.7f;
                    boolean isMUI = data.isForm(DBCForm.MasteredUltraInstinct);

                    double posY = entity.posY + (entity instanceof EntityPlayerSP ? -1.6f : 0.0);

                    if (isMUI) {
                        red = 87.0F;
                        green = 200.0F;
                        blue = 208.0F;
                        red2 = 203.0F;
                        green2 = 137.0F;
                        red = 234.0F;
                        green = 245.0F;
                        blue = 250.0F;
                        blue3 = 252.0F;
                    } else {
                        red = 141.0F;
                        green = 158.0F;
                        blue = 210.0F;
                        red2 = 215.0F;
                        green2 = 152.0F;
                        red = 219.0F;
                        green = 243.0F;
                        blue = 247.0F;
                        blue3 = 250.0F;
                    }

                    float extra_scale = 1.5F;
                    float outNew = 1.6F;
                    float target_fullsize_one1 = 0.32F;
                    float targetsizeMin = height * (8.0F / target_fullsize_one1) * 0.01F;
                    float target_fullsize_one2 = 0.32F;
                    float targetsizeMax = height * (26.0F / target_fullsize_one2) * 0.01F;

                    EntityCusPar particle;
                    for (int i = 0; i < 4; ++i) {
                        outNew = 1.7600001F;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 150.0F, 186.0F, 255.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.3F * alpha, 0.35F * alpha, 0.01F * alpha, false, -1, true, entity);
                        particle.worldObj.spawnEntityInWorld(particle);
                    }

                    for (int i = 0; i < 4; ++i) {
                        outNew = 1.7600001F;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.3F * alpha, 0.35F * alpha, 0.01F * alpha, false, -1, true, entity);
                        particle.worldObj.spawnEntityInWorld(particle);
                    }


                    for (int i = 0; i < 3; ++i) { //checked
                        outNew = 0.6F;
                        x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929 * 0.6000000238418579 - 0.30000001192092896;
                        z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.03999999910593033 + 0.01, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 243.0F, 247.0F, 250.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.2F * alpha, 0.25F * alpha, 0.005F * alpha, false, -1, true, entity);
                        particle.worldObj.spawnEntityInWorld(particle);

                        for (int j = 0; j < 2; ++j) {
                            y = Math.random() * (double) (targetsizeMax - targetsizeMin) + (double) targetsizeMin;
                            y -= 0.30000001192092896;
                            outNew = 1.26F;
                            x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                            z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                            particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.03999999910593033 + 0.01, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 32, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, green, blue, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.3F * alpha, 0.35F * alpha, 0.02F * alpha, false, -1, true, entity);
                            particle.worldObj.spawnEntityInWorld(particle);
                        }
                    }
                    if (isMUI) { //checked
                        outNew = 1.8000001F;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red2, green2, red, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * alpha, 0.45F * alpha, 0.015F * alpha, false, -1, true, entity);
                        particle.worldObj.spawnEntityInWorld(particle);

                        for (int i = 0; i < 1 + (isMUI ? 1 : 0); ++i) {
                            y = Math.random() * (double) (targetsizeMax - targetsizeMin) + (double) targetsizeMin;
                            y -= 0.30000001192092896;
                            outNew = 1.9499999F;
                            x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                            z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                            particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red2, green2, red, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * alpha, 0.45F * alpha, 0.015F * alpha, false, -1, true, entity);
                            particle.worldObj.spawnEntityInWorld(particle);
                        }
                    }
                    if (entity.ticksExisted % (isMUI ? 1 : 4) == 0) {//checked
                        x = Math.random() * 1.5 - 0.75;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = Math.random() * 1.5 - 0.75;
                        particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.019999999552965164) + 0.02F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red2, green2, red, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * (isMUI ? 1.2F : 1.0F) * alpha, 0.45F * (isMUI ? 1.2F : 1.0F) * alpha, 0.015F * (isMUI ? 1.2F : 1.0F) * alpha, false, -1, true, entity);
                        particle.worldObj.spawnEntityInWorld(particle);
                    }
                    if (entity.ticksExisted % 4 == 0) { //checked
                        x = Math.random() * 0.6000000238418579 - 0.30000001192092896;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = Math.random() * 0.6000000238418579 - 0.30000001192092896;
                        particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.029999999329447746 + 0.01, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, green, blue, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * alpha, 0.45F * alpha, 0.015F * alpha, false, -1, true, entity);
                        particle.worldObj.spawnEntityInWorld(particle);
//
                        y = Math.random() * (double) (targetsizeMax - targetsizeMin) + (double) targetsizeMin;
                        y -= 0.30000001192092896;
                        outNew = 1.26F;
                        x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.029999999329447746 + 0.01, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.019999999552965164) + 0.02F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, green, blue, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * alpha, 0.45F * alpha, 0.015F * alpha, false, -1, true, entity);
                        particle.worldObj.spawnEntityInWorld(particle);

                        x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        y = Math.random() * (double) height - 0.5;
                        z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                        particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * alpha, 0.45F * alpha, 0.015F * alpha, false, -1, true, entity);
                        particle.worldObj.spawnEntityInWorld(particle);
                    }
                    float spe2 = 1.3F;


                    for (int i = 0; i < 2; ++i) { //checked
                        x = Math.random() * (double) spe2 - (double) (spe2 / 2.0F);
                        double posZOth = -0.30000001192092896;
                        z = Math.random() * (double) spe2 - (double) (spe2 / 2.0F);
                        particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, posZOth, z, 0.0, 0.05 + Math.random() * 0.10000000149011612, 0.0, 0.0F, (int) (Math.random() * 8.0) + 48, 48, 8, 32, false, 0.0F, false, 0.0F, 1, "", 20, 0, 0.003F + (float) (Math.random() * 0.006000000052154064), 0.0F, 0.0F, 0, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.8F, 0.0F, 0.9F, 0.95F, 0.05F, false, -1, true, entity);
                        particle.worldObj.spawnEntityInWorld(particle);
                    }


                    x = offset + (Math.random() - 0.5) * width;
                    y = (Math.random() * (double) height - 0.2);
                    z = offset + (Math.random() - 0.5) * width;
                    particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 100, 2, (float) (Math.random() * 0.05000000074505806), (float) (Math.random() * 0.07999999821186066) + 0.1F, 0.1F, 2, 105.0F, 40.0F, 148.0F, 0.0F, 0.0F, 0.0F, 105.0F, 40.0F, 148.0F, 1, 0.5F, 0.0F, 0.0F, 0.0F, -0.01F, false, -1, true, entity);
                    particle.worldObj.spawnEntityInWorld(particle);

                    x = offset + (Math.random() - 0.5) * width;
                    y = (Math.random() * (double) height - 0.2);
                    z = offset + (Math.random() - 0.5) * width;
                    double posXOth = Math.random() * 0.029999999329447746 + 0.0010000000474974513;
                    int image = (int) (Math.random() * 8.0) + 32;
                    float sizem = (float) (Math.random() * 0.029999999329447746);
                    float sizemm = (float) (Math.random() * 0.029999999329447746) + 0.05F;
                    particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, posXOth, 0.0, 0.0F, image, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 100, 2, sizem, sizemm, 0.1F, 0, 80.0F, 156.0F, 186.0F, 0.0F, 0.0F, 0.0F, 80.0F, 156.0F, 186.0F, 1, 0.8F, 0.0F, 0.0F, 0.0F, -0.01F, false, -1, true, entity);
                    particle.worldObj.spawnEntityInWorld(particle);
                    particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, posXOth, 0.0, 0.0F, image, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 100, 2, sizem * 0.9F, sizemm * 0.9F, 0.1F, 1, 1.0F, 1.0F, 1.0F, -0.03F, -0.02F, -0.01F, 80.0F, 156.0F, 186.0F, 1, 0.65F, 0.0F, 0.0F, 0.0F, -0.01F, false, -1, true, entity);
                    particle.worldObj.spawnEntityInWorld(particle);

                    x = offset + (Math.random() - 0.5) * width;
                    y = (Math.random() * (double) height - 0.2);
                    z = offset + (Math.random() - 0.5) * width;
                    particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.004999999888241291, 0.0, 0.0F, 8, 8, 1, 32, false, 0.0F, false, 0.0F, 1, "", 100, 2, (float) (Math.random() * 0.10000000149011612), (float) (Math.random() * 0.20000000298023224) + 0.5F, 0.1F, 2, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1, 0.4F, 0.0F, 0.0F, 0.0F, -0.01F, false, -1, true, entity);
                    particle.worldObj.spawnEntityInWorld(particle);

                    x = offset + (Math.random() - 0.5) * width;
                    y = (Math.random() * (double) height - 0.2);
                    z = offset + (Math.random() - 0.5) * width;
                    particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 100, 2, (float) (Math.random() * 0.029999999329447746), (float) (Math.random() * 0.029999999329447746) + 0.05F, 0.1F, 2, 189.0F, 138.0F, 227.0F, 0.0F, 0.0F, 0.0F, 189.0F, 138.0F, 227.0F, 1, 0.7F, 0.0F, 0.0F, 0.0F, -0.01F, false, -1, true, entity);
                    particle.worldObj.spawnEntityInWorld(particle);
                }
                break;
            case GoD:
                for (int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                    double x, y, z;
                    float width = height * 0.8f;
                    float offset = 0;
                    double posY = entity.posY + (entity instanceof EntityPlayerSP ? -1.6f : 0.0);
                    float life = 0.35f * height;
                    for (int i = 0; i < 3; ++i) {
                        x = offset + (Math.random() - 0.5) * width;
                        y = (Math.random() * (double) height - 0.2);
                        z = offset + (Math.random() - 0.5) * width;
                        Entity particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 40, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 2, 168.0F, 50.0F, 214.0F, 0.0F, 0.0F, 0.0F, 175.0F, 55.0F, 228.0F, 3, 0.5F, 0.0F, 0.0F, 0.0F, -0.1F, false, -1, false, entity);
                        particle.worldObj.spawnEntityInWorld(particle);

                        x = offset + (Math.random() - 0.5) * width;
                        y = (Math.random() * (double) height - 0.2);
                        z = offset + (Math.random() - 0.5) * width;
                        particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 40, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 2, 168.0F, 50.0F, 214.0F, 0.0F, 0.0F, 0.0F, 175.0F, 55.0F, 228.0F, 3, 0.5F, 0.0F, 0.0F, 0.0F, -0.1F, false, -1, false, entity);
                        particle.worldObj.spawnEntityInWorld(particle);
                    }
                }
                    
                break;
            case GoDToppo:
                for (int r = 0; r < JGConfigClientSettings.get_da1(); ++r) {
                    for (int i = 0; i < 3; ++i) {
                        double x = Math.random() * 2.5 - 1.25;
                        double y = Math.random() * (double) height - 0.20000000298023224;
                        double z = Math.random() * 2.5 - 1.25;
                        Entity particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 30, 2, (float) (Math.random() * 0.029999999329447746) * 3.0F, ((float) (Math.random() * 0.029999999329447746) + 0.05F) * 3.0F, 0.3F, 2, 168.0F, 50.0F, 214.0F, 0.0F, 0.0F, 0.0F, 175.0F, 55.0F, 228.0F, 3, 0.25F, 0.0F, 0.0F, 0.0F, -0.05F, false, -1, false, entity);
                        particle.worldObj.spawnEntityInWorld(particle);
                        x = Math.random() * 2.5 - 1.25;
                        y = Math.random() * (double) height - 0.20000000298023224;
                        z = Math.random() * 2.5 - 1.25;
                        particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, entity.posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 30, 2, (float) (Math.random() * 0.029999999329447746) * 3.0F, ((float) (Math.random() * 0.029999999329447746) + 0.05F) * 3.0F, 0.3F, 2, 168.0F, 50.0F, 214.0F, 0.0F, 0.0F, 0.0F, 175.0F, 55.0F, 228.0F, 3, 0.25F, 0.0F, 0.0F, 0.0F, -0.05F, false, -1, false, entity);
                        particle.worldObj.spawnEntityInWorld(particle);
                    }
                }
                break;
            case Jiren:
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        boolean bol4a = true;
                        double posXOth = entity.posX;
                        double posYOth = entity.posY + (double) (0.0F);
                        double posZOth = entity.posZ;
                        float red;
                        float green;
                        float blue;
                        float red2;
                        float green2;
                        float blue2;
                        float red3;
                        float green3;
                        float blue3;
                        red = 189.0F;
                        green = 26.0F;
                        blue = 47.0F;
                        red2 = 189.0F;
                        green2 = 26.0F;
                        blue2 = 47.0F;
                        red3 = 248.0F;
                        green3 = 231.0F;
                        blue3 = 236.0F;

                        float life = 0.8F * height;
                        float outNew = 1.6F;
                        float target_fullsize_one1 = 0.32F;
                        float targetsizeMin = height * (8.0F / target_fullsize_one1) * 0.01F;
                        float target_fullsize_one2 = 0.32F;
                        float targetsizeMax = height * (26.0F / target_fullsize_one2) * 0.01F;
                        double x;
                        double y;
                        double z;
                        int i;
                        EntityCusPar particle2;
                        for (i = 0; i < 4; ++i) {
                            outNew = 1.7600001F;
                            y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.18F, 0.21000001F, 0.006F, false, -1, true, entity);
                            particle2.worldObj.spawnEntityInWorld(particle2);
                        }
                        for (i = 0; i < 4; ++i) {
                            y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.18F, 0.21000001F, 0.006F, false, -1, true, entity);
                            particle2.worldObj.spawnEntityInWorld(particle2);
                        }
                        if (entity.ticksExisted % 4 == 0) {
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            y = Math.random() * (double) height - 0.5;
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.24000001F, 0.27F, 0.009000001F, false, -1, true, entity);
                            particle2.worldObj.spawnEntityInWorld(particle2);
                        }

                        EntityCusPar particle;
                        outNew = 1.8000001F;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                        z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                        particle2 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red2, green2, blue2, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.24000001F, 0.27F, 0.009000001F, false, -1, true, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);

                        for (i = 0; i < 1 + 1; ++i) {
                            y = Math.random() * (double)(targetsizeMax - targetsizeMin) + (double)targetsizeMin;
                            y -= 0.30000001192092896;
                            outNew = 1.9499999F;
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red2, green2, blue2, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.24000001F, 0.27F, 0.009000001F, false, -1, true, entity);
                            particle.worldObj.spawnEntityInWorld(particle);
                        }

                        x = Math.random() * 1.5 - 0.75;
                        y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                        z = Math.random() * 1.5 - 0.75;
                        particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.019999999552965164) + 0.02F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red2, green2, blue2, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * 1.2F * 0.6F, 0.45F * 1.2F * 0.6F, 0.015F * 1.2F * 0.6F, false, -1, true, entity);
                        particle2.worldObj.spawnEntityInWorld(particle2);


                        for (i = 0; i < 3; ++i) {
                            outNew = 0.6F;
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            y = (Math.random() * (double) height - 0.5) * 0.800000011920929 * 0.6000000238418579 - 0.30000001192092896;
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.03999999910593033 + 0.01, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 243.0F, 247.0F, 250.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.120000005F, 0.15F, 0.003F, false, -1, true, entity);
                            particle.worldObj.spawnEntityInWorld(particle);

                            for(int inner = 0; inner < 2; ++inner) {
                                y = Math.random() * (double)(targetsizeMax - targetsizeMin) + (double)targetsizeMin;
                                y -= 0.30000001192092896;
                                outNew = 1.26F;
                                x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                                z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                                Entity entity1 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.03999999910593033 + 0.01, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 32, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red3, green3, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.18F, 0.21000001F, 0.012F, false, -1, true, entity);
                                entity1.worldObj.spawnEntityInWorld(entity1);
                            }
                        }

                        if (entity.ticksExisted % 4 == 0) {
                            x = Math.random() * 0.6000000238418579 - 0.30000001192092896;
                            y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
                            z = Math.random() * 0.6000000238418579 - 0.30000001192092896;
                            particle2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.029999999329447746 + 0.01, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red3, green3, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.24000001F, 0.27F, 0.009000001F, false, -1, true, entity);
                            particle2.worldObj.spawnEntityInWorld(particle2);
                            y = Math.random() * (double)(targetsizeMax - targetsizeMin) + (double)targetsizeMin;
                            y -= 0.30000001192092896;
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.029999999329447746 + 0.01, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.019999999552965164) + 0.02F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red3, green3, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.24000001F, 0.27F, 0.009000001F, false, -1, true, entity);
                            particle.worldObj.spawnEntityInWorld(particle);
                        }
                    }
                break;
        }

    }

    public static void UI(Entity entity, EnumAuraTypes2D types, int color, float height, float numberOfParticles) {
        float red, red2, green2, blue3, green, blue, alpha = 0.7f;
        double x, y, z;
        float life = 0.8F * height;
        float width = height * 0.7f;
        boolean isMUI = false;

        double posY = entity.posY + (entity instanceof EntityPlayerSP ? -1.6f : 0.0);

        if (isMUI) {
            red = 87.0F;
            green = 200.0F;
            blue = 208.0F;
            red2 = 203.0F;
            green2 = 137.0F;
            red = 234.0F;
            green = 245.0F;
            blue = 250.0F;
            blue3 = 252.0F;
        } else {
            red = 141.0F;
            green = 158.0F;
            blue = 210.0F;
            red2 = 215.0F;
            green2 = 152.0F;
            red = 219.0F;
            green = 243.0F;
            blue = 247.0F;
            blue3 = 250.0F;
        }

        float extra_scale = 1.5F;
        float outNew = 1.6F;
        float target_fullsize_one1 = 0.32F;
        float targetsizeMin = height * (8.0F / target_fullsize_one1) * 0.01F;
        float target_fullsize_one2 = 0.32F;
        float targetsizeMax = height * (26.0F / target_fullsize_one2) * 0.01F;

        EntityCusPar particle;
        for (int i = 0; i < 4; ++i) {
            outNew = 1.7600001F;
            y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
            x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 150.0F, 186.0F, 255.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.3F * alpha, 0.35F * alpha, 0.01F * alpha, false, -1, true, entity);
            particle.worldObj.spawnEntityInWorld(particle);
        }

        for (int i = 0; i < 4; ++i) {
            outNew = 1.7600001F;
            y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
            x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.3F * alpha, 0.35F * alpha, 0.01F * alpha, false, -1, true, entity);
            particle.worldObj.spawnEntityInWorld(particle);
        }


        for (int i = 0; i < 3; ++i) {
            outNew = 0.6F;
            x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            y = (Math.random() * (double) height - 0.5) * 0.800000011920929 * 0.6000000238418579 - 0.30000001192092896;
            z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.03999999910593033 + 0.01, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 243.0F, 247.0F, 250.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.2F * alpha, 0.25F * alpha, 0.005F * alpha, false, -1, true, entity);
            particle.worldObj.spawnEntityInWorld(particle);

            for (int j = 0; j < 2; ++j) {
                y = Math.random() * (double) (targetsizeMax - targetsizeMin) + (double) targetsizeMin;
                y -= 0.30000001192092896;
                outNew = 1.26F;
                x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                Entity entity1 = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.03999999910593033 + 0.01, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 32, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, green, blue, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.3F * alpha, 0.35F * alpha, 0.02F * alpha, false, -1, true, entity);
                entity1.worldObj.spawnEntityInWorld(entity1);
            }
        }
        if (isMUI) {
            outNew = 1.8000001F;
            y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
            x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red2, green2, red, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * alpha, 0.45F * alpha, 0.015F * alpha, false, -1, true, entity);
            particle.worldObj.spawnEntityInWorld(particle);

            for (int i = 0; i < 1 + (isMUI ? 1 : 0); ++i) {
                y = Math.random() * (double) (targetsizeMax - targetsizeMin) + (double) targetsizeMin;
                y -= 0.30000001192092896;
                outNew = 1.9499999F;
                x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
                particle = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red2, green2, red, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * alpha, 0.45F * alpha, 0.015F * alpha, false, -1, true, entity);
                particle.worldObj.spawnEntityInWorld(particle);
            }
        }
        if (entity.ticksExisted % (isMUI ? 1 : 4) == 0) {
            x = Math.random() * 1.5 - 0.75;
            y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
            z = Math.random() * 1.5 - 0.75;
            particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.019999999552965164) + 0.02F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red2, green2, red, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * (isMUI ? 1.2F : 1.0F) * alpha, 0.45F * (isMUI ? 1.2F : 1.0F) * alpha, 0.015F * (isMUI ? 1.2F : 1.0F) * alpha, false, -1, true, entity);
            particle.worldObj.spawnEntityInWorld(particle);
        }
        if (entity.ticksExisted % 4 == 0) {
            x = Math.random() * 0.6000000238418579 - 0.30000001192092896;
            y = (Math.random() * (double) height - 0.5) * 0.800000011920929;
            z = Math.random() * 0.6000000238418579 - 0.30000001192092896;
            particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.029999999329447746 + 0.01, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, green, blue, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * alpha, 0.45F * alpha, 0.015F * alpha, false, -1, true, entity);
            particle.worldObj.spawnEntityInWorld(particle);

            y = Math.random() * (double) (targetsizeMax - targetsizeMin) + (double) targetsizeMin;
            y -= 0.30000001192092896;
            outNew = 1.26F;
            x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.029999999329447746 + 0.01, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.019999999552965164) + 0.02F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, green, blue, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * alpha, 0.45F * alpha, 0.015F * alpha, false, -1, true, entity);
            particle.worldObj.spawnEntityInWorld(particle);

            x = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            y = Math.random() * (double) height - 0.5;
            z = Math.random() * (double) outNew - (double) (outNew / 2.0F);
            particle = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, entity.posX, posY, entity.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F * 0.5F, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * alpha, 0.45F * alpha, 0.015F * alpha, false, -1, true, entity);
            particle.worldObj.spawnEntityInWorld(particle);
        }
    }


}
