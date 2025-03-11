package kamkeel.npcdbc.mixins.early.impl.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mixin(value = RenderGlobal.class)
public class MixinRenderGlobal {

    @Shadow
    public Minecraft mc;

    /**
     * Sorts from furthest to nearest of the frustum's near plane
     */
    @Unique
    private List sortedEntityList;
    @Unique
    private boolean sorted;

    @Redirect(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getLoadedEntityList()Ljava/util/List;"))
    private List secondRendPass(WorldClient instance, @Local(ordinal = 0) LocalRef<ICamera> camera) {
        sorted = MinecraftForgeClient.getRenderPass() != 0; //sorts only once per tick, at the start of rend pass 0
        if (!sorted) {
            try {
                sortedEntityList = new ArrayList(instance.getLoadedEntityList());
                ClippingHelperImpl frustum = ClippingHelperImpl.instance;
                float[] nearPlane = frustum.frustum[5];

                float planeX = (float) (RenderManager.renderPosX - nearPlane[3] * nearPlane[0]); //posX of the near plane center
                float planeY = (float) (RenderManager.renderPosY - nearPlane[3] * nearPlane[1]); //posY
                float planeZ = (float) (RenderManager.renderPosZ - nearPlane[3] * nearPlane[2]); //posZ


                Collections.sort(sortedEntityList, (Comparator<Entity>) (entity1, entity2) -> {
                    double distanceToEntity1 = entity1.getDistanceSq(planeX, planeY, planeZ);
                    double distanceToEntity2 = entity2.getDistanceSq(planeX, planeY, planeZ);
                    return Double.compare(distanceToEntity1, distanceToEntity2); // Sorting from nearest to furthest
                });
                sorted = true;
            } catch (Exception e) {
                FMLLog.log(Level.ERROR, "Failed to sort entities: " + e.getMessage());
            }
        }

        if (sorted && sortedEntityList != null)
            return sortedEntityList;
        else
            return instance.getLoadedEntityList();
    }
}
