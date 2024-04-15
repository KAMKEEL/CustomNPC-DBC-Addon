package kamkeel.npcdbc.client;

import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import JinRyuu.JRMCore.entity.EntityCusPar;
import kamkeel.npcdbc.data.EnumAuraTypes;
import net.minecraft.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;

public class ParticleFormHandler {
    public static void spawnParticle(EntityNPCInterface npc, EnumAuraTypes types){
        switch (types){
            case GoD:
                if (npc.worldObj.isRemote && JGConfigClientSettings.CLIENT_DA8) {
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        for(int i = 0; i < 5; ++i) {
                            double x = Math.random() - 0.5;
                            double y = Math.random() * (double)npc.height - 0.5;
                            double z = Math.random() - 0.5;
                            Entity entity = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 30, 2, (float)(Math.random() * 0.029999999329447746), (float)(Math.random() * 0.029999999329447746) + 0.05F, 0.1F, 2, 168.0F, 50.0F, 214.0F, 0.0F, 0.0F, 0.0F, 175.0F, 55.0F, 228.0F, 3, 0.5F, 0.0F, 0.0F, 0.0F, -0.1F, false, -1, false, npc);
                            entity.worldObj.spawnEntityInWorld(entity);
                            x = Math.random() - 0.5;
                            y = Math.random() * (double)npc.height - 0.5;
                            z = Math.random() - 0.5;
                            entity = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 30, 2, (float)(Math.random() * 0.029999999329447746), (float)(Math.random() * 0.029999999329447746) + 0.05F, 0.1F, 2, 168.0F, 50.0F, 214.0F, 0.0F, 0.0F, 0.0F, 175.0F, 55.0F, 228.0F, 3, 0.5F, 0.0F, 0.0F, 0.0F, -0.1F, false, -1, false, npc);
                            entity.worldObj.spawnEntityInWorld(entity);
                        }
                    }
                }
                break;
            case GoDToppo:
                if (npc.worldObj.isRemote && JGConfigClientSettings.CLIENT_DA8) {
                    for(int r = 0; r < JGConfigClientSettings.get_da1(); ++r) {
                        for(int i = 0; i < 3; ++i) {
                            double x = Math.random() * 2.5 - 1.25;
                            double y = Math.random() * (double)npc.height - 0.20000000298023224;
                            double z = Math.random() * 2.5 - 1.25;
                            Entity entity = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 30, 2, (float)(Math.random() * 0.029999999329447746) * 3.0F, ((float)(Math.random() * 0.029999999329447746) + 0.05F) * 3.0F, 0.3F, 2, 168.0F, 50.0F, 214.0F, 0.0F, 0.0F, 0.0F, 175.0F, 55.0F, 228.0F, 3, 0.25F, 0.0F, 0.0F, 0.0F, -0.05F, false, -1, false, npc);
                            entity.worldObj.spawnEntityInWorld(entity);
                            x = Math.random() * 2.5 - 1.25;
                            y = Math.random() * (double)npc.height - 0.20000000298023224;
                            z = Math.random() * 2.5 - 1.25;
                            entity = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 30, 2, (float)(Math.random() * 0.029999999329447746) * 3.0F, ((float)(Math.random() * 0.029999999329447746) + 0.05F) * 3.0F, 0.3F, 2, 168.0F, 50.0F, 214.0F, 0.0F, 0.0F, 0.0F, 175.0F, 55.0F, 228.0F, 3, 0.25F, 0.0F, 0.0F, 0.0F, -0.05F, false, -1, false, npc);
                            entity.worldObj.spawnEntityInWorld(entity);
                        }
                    }
                }
                break;
            case SaiyanRose:
                if (npc.worldObj.isRemote && JGConfigClientSettings.CLIENT_DA8) {
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        float out = 1.6F;
                        float in = 1.0F;
                        float life = 0.8F * npc.height;
                        double x;
                        double y;
                        double z;
                        EntityCusPar entity2;
                        for(int gh = 0; gh < 2; ++gh) {
                            x = Math.random() * (double)out - (double)(out / 2.0F);
                            y = Math.random() * (double)npc.height - 0.5;
                            z = Math.random() * (double)out - (double)(out / 2.0F);
                            entity2 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 186.0F, 37.0F, 197.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) npc);
                            x = Math.random() * (double)out - (double)(out / 2.0F);
                            y = Math.random() * (double)npc.height - 0.5;
                            z = Math.random() * (double)out - (double)(out / 2.0F);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                            entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 186.0F, 37.0F, 197.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) npc);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                        }

                        out *= 1.4F;
                        x = Math.random() * (double)out - (double)(out / 2.0F);
                        y = Math.random() * (double)npc.height - 0.5;
                        z = Math.random() * (double)out - (double)(out / 2.0F);
                        Entity entity = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 140.0F, 8.0F, 62.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) npc);
                        x = Math.random() * (double)out - (double)(out / 2.0F);
                        y = Math.random() * (double)npc.height - 0.5;
                        z = Math.random() * (double)out - (double)(out / 2.0F);
                        entity.worldObj.spawnEntityInWorld(entity);
                        entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 140.0F, 8.0F, 62.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) npc);
                        entity.worldObj.spawnEntityInWorld(entity2);
                        x = Math.random() * (double)in - (double)(in / 2.0F);
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() * (double)in - (double)(in / 2.0F);
                        entity = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 184.0F, 147.0F, 241.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) npc);
                        x = Math.random() * (double)in - (double)(in / 2.0F);
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() * (double)in - (double)(in / 2.0F);
                        entity.worldObj.spawnEntityInWorld(entity);
                        entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 184.0F, 147.0F, 241.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) npc);
                        entity.worldObj.spawnEntityInWorld(entity2);
                        in *= 1.2F;
                        x = Math.random() * (double)in - (double)(in / 2.0F);
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() * (double)in - (double)(in / 2.0F);
                        entity = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 93.0F, 3.0F, 177.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) npc);
                        x = Math.random() * (double)in - (double)(in / 2.0F);
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() * (double)in - (double)(in / 2.0F);
                        entity.worldObj.spawnEntityInWorld(entity);
                        entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 93.0F, 3.0F, 177.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, (Entity) npc);
                        entity.worldObj.spawnEntityInWorld(entity2);
                    }
                }
                break;
            case UI:
                for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                    if (JGConfigClientSettings.CLIENT_DA8) {
                        double posXOth = npc.posX;
                        double posYOth = npc.posY;
                        double posZOth = npc.posZ;
                        float life = 0.8F * npc.height;
                        double x = Math.random() * 1.600000023841858 - 0.800000011920929;
                        double y = Math.random() * (double)npc.height - 0.5;
                        double z = Math.random() * 1.600000023841858 - 0.800000011920929;
                        Entity entity = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 141.0F, 158.0F, 210.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, npc);
                        x = Math.random() * 1.600000023841858 - 0.800000011920929;
                        y = Math.random() * (double)npc.height - 0.5;
                        z = Math.random() * 1.600000023841858 - 0.800000011920929;
                        entity.worldObj.spawnEntityInWorld(entity);

                        entity = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 141.0F, 158.0F, 210.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, npc);
                        entity.worldObj.spawnEntityInWorld(entity);
                        x = Math.random() * 1.5 - 0.75;
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() * 1.5 - 0.75;
                        entity = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 215.0F, 152.0F, 219.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, npc);
                        x = Math.random() * 1.5 - 0.75;
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() * 1.5 - 0.75;
                        entity.worldObj.spawnEntityInWorld(entity);
                        entity = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 215.0F, 152.0F, 219.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, npc);
                        entity.worldObj.spawnEntityInWorld(entity);
                        x = Math.random() * 0.6000000238418579 - 0.30000001192092896;
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929 * 0.6000000238418579;
                        z = Math.random() * 0.6000000238418579 - 0.30000001192092896;
                        entity = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 243.0F, 247.0F, 250.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, npc);
                        x = Math.random() * 0.6000000238418579 - 0.30000001192092896;
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() * 0.6000000238418579 - 0.30000001192092896;
                        entity.worldObj.spawnEntityInWorld(entity);
                        Entity entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 243.0F, 247.0F, 250.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, true, npc);
                        entity.worldObj.spawnEntityInWorld(entity2);
                    }
                }
                break;
            case Golden:
                if (npc.worldObj.isRemote && JGConfigClientSettings.CLIENT_DA8) {
                    float out = 1.6F;
                    float life = 0.8F * npc.height;
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        int i;
                        double x;
                        double y;
                        double z;
                        EntityCusPar entity2;
                        for(i = 0; i < 2; ++i) {
                            x = Math.random() * (double)out - (double)(out / 2.0F);
                            y = Math.random() * (double)npc.height - 0.5;
                            z = Math.random() * (double)out - (double)(out / 2.0F);
                            entity2 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 249.0F, 212.0F, 33.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                            x = Math.random() * (double)out - (double)(out / 2.0F);
                            y = Math.random() * (double)npc.height - 0.5;
                            z = Math.random() * (double)out - (double)(out / 2.0F);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                            entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 249.0F, 212.0F, 33.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                        }

                        for(i = 0; i < 2; ++i) {
                            out *= 1.3F;
                            x = Math.random() * (double)out - (double)(out / 2.0F);
                            y = Math.random() * (double)npc.height - 0.5;
                            z = Math.random() * (double)out - (double)(out / 2.0F);
                            entity2 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 234.0F, 134.0F, 34.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                            x = Math.random() * (double)out - (double)(out / 2.0F);
                            y = Math.random() * (double)npc.height - 0.5;
                            z = Math.random() * (double)out - (double)(out / 2.0F);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                            entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 234.0F, 134.0F, 34.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                        }

                        x = Math.random() - 0.5;
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() - 0.5;
                        Entity entity = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 255.0F, 255.0F, 208.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                        x = Math.random() - 0.5;
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() - 0.5;
                        entity.worldObj.spawnEntityInWorld(entity);
                        entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 255.0F, 255.0F, 208.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                        entity.worldObj.spawnEntityInWorld(entity2);
                    }
                }
                break;
            case Legendary:
                if (npc.worldObj.isRemote && JGConfigClientSettings.CLIENT_DA8) {
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        for(int i = 0; i < 5; ++i) {
                            float red = 183.0F;
                            float green = 205.0F;
                            float blue = 97.0F;
                            float life = 0.8F * npc.height;
                            float extra_scale = 1.0F + (npc.height > 2.1F ? npc.height / 2.0F : 0.0F) / 5.0F;
                            float width = npc.width * 3.0F;
                            double x = (Math.random() - 0.5) * (double)(width * 1.2F);
                            double y = Math.random() * (double)(npc.height * 1.4F) - (double)(npc.height / 2.0F) - 0.30000001192092896;
                            double z = (Math.random() - 0.5) * (double)(width * 1.2F);
                            double motx = Math.random() * 0.019999999552965164 - 0.009999999776482582;
                            double moty = (Math.random() * 0.8999999761581421 + 0.8999999761581421) * (double)(life * extra_scale) * 0.07;
                            double motz = Math.random() * 0.019999999552965164 - 0.009999999776482582;
                            Entity entity2 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY + (double)(0.0F), npc.posZ, x, y, z, motx, moty, motz, 0.0F, (int)(Math.random() * 3.0) + 32, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", (int)(30.0F * life * 0.5F), 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.2F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, npc);
                            npc.worldObj.spawnEntityInWorld(entity2);
                            entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY + (double)(0.0F), npc.posZ, x, y, z, motx, moty, motz, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", (int)(30.0F * life * 0.5F), 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.1F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, npc);
                            npc.worldObj.spawnEntityInWorld(entity2);
                        }
                    }
                }
                break;
            case SaiyanSuper:
                if (npc.worldObj.isRemote && JGConfigClientSettings.CLIENT_DA8) {
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        for(int i = 0; i < 5; ++i) {
                            float red = 255.0F;
                            float green = 217.0F;
                            float blue = 25.0F;
                            float life = 0.8F * npc.height;
                            float extra_scale = 1.0F + (npc.height > 2.1F ? npc.height / 2.0F : 0.0F) / 5.0F;
                            float width = npc.width * 3.0F;
                            double x = (Math.random() - 0.5) * (double)(width * 1.2F);
                            double y = Math.random() * (double)(npc.height * 1.4F) - (double)(npc.height / 2.0F) - 0.30000001192092896;
                            double z = (Math.random() - 0.5) * (double)(width * 1.2F);
                            double motx = Math.random() * 0.019999999552965164 - 0.009999999776482582;
                            double moty = (Math.random() * 0.8999999761581421 + 0.8999999761581421) * (double)(life * extra_scale) * 0.07;
                            double motz = Math.random() * 0.019999999552965164 - 0.009999999776482582;
                            Entity entity2 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY + (double)(0.0F), npc.posZ, x, y, z, motx, moty, motz, 0.0F, (int)(Math.random() * 3.0) + 32, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", (int)(30.0F * life * 0.5F), 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.2F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, npc);
                            npc.worldObj.spawnEntityInWorld(entity2);
                            entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY + (double)(0.0F), npc.posZ, x, y, z, motx, moty, motz, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", (int)(30.0F * life * 0.5F), 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.1F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, npc);
                            npc.worldObj.spawnEntityInWorld(entity2);
                        }
                    }
                }
                break;
            case SaiyanGod:
                if (npc.worldObj.isRemote && JGConfigClientSettings.CLIENT_DA8) {
                    float life = 0.8F * npc.height;
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        int i;
                        double x;
                        double y;
                        double z;
                        EntityCusPar entity2;
                        for(i = 0; i < 2; ++i) {
                            float spe2 = 1.3F;
                            x = Math.random() * (double)spe2 - (double)(spe2 / 2.0F);
                            y = -0.30000001192092896;
                            z = Math.random() * (double)spe2 - (double)(spe2 / 2.0F);
                            entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, 0.05 + Math.random() * 0.10000000149011612, 0.0, 0.0F, (int)(Math.random() * 8.0) + 48, 48, 8, 32, false, 0.0F, false, 0.0F, 1, "", 25, 0, 0.003F + (float)(Math.random() * 0.006000000052154064), 0.0F, 0.0F, 0, 255.0F, 220.0F, 200.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.8F, 0.0F, 0.9F, 0.95F, 0.02F, false, -1, false, npc);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                        }
                        for(i = 0; i < 2; ++i) {
                            x = Math.random() * 1.600000023841858 - 0.800000011920929;
                            y = Math.random() * (double)npc.height - 0.5;
                            z = Math.random() * 1.600000023841858 - 0.800000011920929;
                            entity2 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 215.0F, 107.0F, 61.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                            x = Math.random() * 1.600000023841858 - 0.800000011920929;
                            y = Math.random() * (double)npc.height - 0.5;
                            z = Math.random() * 1.600000023841858 - 0.800000011920929;
                            entity2.worldObj.spawnEntityInWorld(entity2);
                            entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 215.0F, 107.0F, 61.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                        }

                        x = Math.random() * 1.5 - 0.75;
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() * 1.5 - 0.75;
                        Entity entity = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 218.0F, 209.0F, 71.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                        x = Math.random() * 1.5 - 0.75;
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() * 1.5 - 0.75;
                        entity.worldObj.spawnEntityInWorld(entity);
                        entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 218.0F, 209.0F, 71.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                        entity.worldObj.spawnEntityInWorld(entity2);
                    }
                }
                break;
            case SaiyanBlue:
                if (npc.worldObj.isRemote && JGConfigClientSettings.CLIENT_DA8) {
                    float life = 0.8F * npc.height;
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        int i;
                        double x;
                        double y;
                        double z;
                        EntityCusPar entity2;
                        for(i = 0; i < 2; ++i) {
                            float spe2 = 1.3F;
                            x = Math.random() * (double)spe2 - (double)(spe2 / 2.0F);
                            y = -0.30000001192092896;
                            z = Math.random() * (double)spe2 - (double)(spe2 / 2.0F);
                            entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, 0.05 + Math.random() * 0.10000000149011612, 0.0, 0.0F, (int)(Math.random() * 8.0) + 48, 48, 8, 32, false, 0.0F, false, 0.0F, 1, "", 25, 0, 0.003F + (float)(Math.random() * 0.006000000052154064), 0.0F, 0.0F, 0, 160.0F, 220.0F, 255.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.8F, 0.0F, 0.9F, 0.95F, 0.02F, false, -1, false, npc);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                        }
                        for(i = 0; i < 2; ++i) {
                            x = Math.random() * 1.600000023841858 - 0.800000011920929;
                            y = Math.random() * (double)npc.height - 0.5;
                            z = Math.random() * 1.600000023841858 - 0.800000011920929;
                            entity2 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 48.0F, 208.0F, 232.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                            x = Math.random() * 1.600000023841858 - 0.800000011920929;
                            y = Math.random() * (double)npc.height - 0.5;
                            z = Math.random() * 1.600000023841858 - 0.800000011920929;
                            entity2.worldObj.spawnEntityInWorld(entity2);
                            entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 48.0F, 208.0F, 232.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                        }

                        x = Math.random() - 0.5;
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() - 0.5;
                        Entity entity = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 160.0F, 220.0F, 255.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                        x = Math.random() - 0.5;
                        y = (Math.random() * (double)npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() - 0.5;
                        entity.worldObj.spawnEntityInWorld(entity);
                        entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, npc.posX, npc.posY, npc.posZ, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 160.0F, 220.0F, 255.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.015F, false, -1, false, npc);
                        entity.worldObj.spawnEntityInWorld(entity2);
                    }
                }
                break;
            case Jiren:
                if (npc.worldObj.isRemote && JGConfigClientSettings.CLIENT_DA8) {
                    for(int k = 0; k < JGConfigClientSettings.get_da1(); ++k) {
                        boolean bol4a = true;
                        double posXOth = npc.posX;
                        double posYOth = npc.posY + (double)(0.0F);
                        double posZOth = npc.posZ;
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

                        float life = 0.8F * npc.height;
                        float outNew = 1.6F;
                        float target_fullsize_one1 = 0.32F;
                        float targetsizeMin = npc.height * (8.0F / target_fullsize_one1) * 0.01F;
                        float target_fullsize_one2 = 0.32F;
                        float targetsizeMax = npc.height * (26.0F / target_fullsize_one2) * 0.01F;
                        double x;
                        double y;
                        double z;
                        int repeat2;
                        EntityCusPar entity2;
                        for(repeat2 = 0; repeat2 < 4; ++repeat2) {
                            outNew = 1.7600001F;
                            y = (Math.random() * (double) npc.height - 0.5) * 0.800000011920929;
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            entity2 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.18F, 0.21000001F, 0.006F, false, -1, true, npc);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                        }
                        for(repeat2 = 0; repeat2 < 4; ++repeat2) {
                            y = (Math.random() * (double) npc.height - 0.5) * 0.800000011920929;
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            entity2 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.18F, 0.21000001F, 0.006F, false, -1, true, npc);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                        }
                        if (npc.ticksExisted % 4 == 0) {
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            y = Math.random() * (double) npc.height - 0.5;
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.24000001F, 0.27F, 0.009000001F, false, -1, true, npc);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                        }

                        EntityCusPar entity3;
                        outNew = 1.8000001F;
                        y = (Math.random() * (double) npc.height - 0.5) * 0.800000011920929;
                        x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                        z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                        entity2 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red2, green2, blue2, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.24000001F, 0.27F, 0.009000001F, false, -1, true, npc);
                        entity2.worldObj.spawnEntityInWorld(entity2);

                        for(repeat2 = 0; repeat2 < 1 + 1; ++repeat2) {
                            y = Math.random() * (double)(targetsizeMax - targetsizeMin) + (double)targetsizeMin;
                            y -= 0.30000001192092896;
                            outNew = 1.9499999F;
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            entity3 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red2, green2, blue2, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.24000001F, 0.27F, 0.009000001F, false, -1, true, npc);
                            entity3.worldObj.spawnEntityInWorld(entity3);
                        }

                        x = Math.random() * 1.5 - 0.75;
                        y = (Math.random() * (double) npc.height - 0.5) * 0.800000011920929;
                        z = Math.random() * 1.5 - 0.75;
                        entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.05000000074505806, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.019999999552965164) + 0.02F) * life * 0.5F * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red2, green2, blue2, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.4F * 1.2F * 0.6F, 0.45F * 1.2F * 0.6F, 0.015F * 1.2F * 0.6F, false, -1, true, npc);
                        entity2.worldObj.spawnEntityInWorld(entity2);


                        for(repeat2 = 0; repeat2 < 3; ++repeat2) {
                            outNew = 0.6F;
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            y = (Math.random() * (double) npc.height - 0.5) * 0.800000011920929 * 0.6000000238418579 - 0.30000001192092896;
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            entity3 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.03999999910593033 + 0.01, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, 243.0F, 247.0F, 250.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.120000005F, 0.15F, 0.003F, false, -1, true, npc);
                            entity3.worldObj.spawnEntityInWorld(entity3);

                            for(int inner = 0; inner < 2; ++inner) {
                                y = Math.random() * (double)(targetsizeMax - targetsizeMin) + (double)targetsizeMin;
                                y -= 0.30000001192092896;
                                outNew = 1.26F;
                                x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                                z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                                Entity entity1 = new EntityCusPar("jinryuumodscore:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.03999999910593033 + 0.01, 0.0, 0.0F, (int)(Math.random() * 3.0) + 8, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", 32, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F, 0.2F * life * 0.5F, 0, red3, green3, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.18F, 0.21000001F, 0.012F, false, -1, true, npc);
                                entity1.worldObj.spawnEntityInWorld(entity1);
                            }
                        }

                        if (npc.ticksExisted % 4 == 0) {
                            x = Math.random() * 0.6000000238418579 - 0.30000001192092896;
                            y = (Math.random() * (double) npc.height - 0.5) * 0.800000011920929;
                            z = Math.random() * 0.6000000238418579 - 0.30000001192092896;
                            entity2 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.029999999329447746 + 0.01, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.029999999329447746) + 0.03F) * life * 0.5F * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red3, green3, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.24000001F, 0.27F, 0.009000001F, false, -1, true, npc);
                            entity2.worldObj.spawnEntityInWorld(entity2);
                            y = Math.random() * (double)(targetsizeMax - targetsizeMin) + (double)targetsizeMin;
                            y -= 0.30000001192092896;
                            x = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            z = Math.random() * (double)outNew - (double)(outNew / 2.0F);
                            entity3 = new EntityCusPar("jinryuudragonbc:bens_particles.png", npc.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, 0.0, Math.random() * 0.029999999329447746 + 0.01, 0.0, 0.0F, (int)(Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", 50, 2, ((float)(Math.random() * 0.019999999552965164) + 0.02F) * life * 0.5F * 0.5F, ((float)(Math.random() * 0.009999999776482582) + 0.02F) * life * 0.5F * 0.5F, 0.2F * life * 0.5F * 0.5F, 0, red3, green3, blue3, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 2, 0.0F, 0.0F, 0.24000001F, 0.27F, 0.009000001F, false, -1, true, npc);
                            entity3.worldObj.spawnEntityInWorld(entity3);
                        }
                    }
                }
                break;
        }
    }

}
