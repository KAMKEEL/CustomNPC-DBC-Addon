package kamkeel.npcdbc.mixins.early.impl.client;

import JinRyuu.JRMCore.entity.EntityCusPar;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = RenderGlobal.class)
public class MixinRenderGlobal {

    @Shadow
    public Minecraft mc;

    @Redirect(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getLoadedEntityList()Ljava/util/List;"))
    private List secondRendPass(WorldClient instance) {

        List list = new ArrayList(instance.getLoadedEntityList());

        List<Entity> auras = new ArrayList<>();
        List<Entity> players = new ArrayList<>();
        List<Entity> particles = new ArrayList<>();
        List<Entity> otherEntities = new ArrayList<>();

        for (Object entity : list) {
            if (entity instanceof EntityAura)
                auras.add((Entity) entity);
            else if (entity instanceof EntityCusPar)
                particles.add((Entity) entity);
            else if (entity instanceof EntityPlayer)
                players.add((Entity) entity);
            else
                otherEntities.add((Entity) entity);


        }
        List<Entity> sortedEntities = new ArrayList<>();
        sortedEntities.addAll(auras);
        sortedEntities.addAll(particles);
        sortedEntities.addAll(players);
        sortedEntities.addAll(otherEntities);


        return sortedEntities;
    }


}
