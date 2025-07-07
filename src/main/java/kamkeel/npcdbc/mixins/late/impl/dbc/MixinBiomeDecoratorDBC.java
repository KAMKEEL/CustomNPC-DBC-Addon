package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.DragonBC.common.Worlds.BiomeDecoratorDBC;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = BiomeDecoratorDBC.class, remap = false)
public class MixinBiomeDecoratorDBC {

    /**
     * Prevents BiomeDecoratorDBC from throwing a RuntimeException when it is
     * called recursively by simply canceling the additional call.
     */
    @Inject(method = "decorateChunk", at = @At("HEAD"), cancellable = true)
    private void cancelIfAlreadyDecorating(World world, Random random, BiomeGenBase biome, int chunkX, int chunkZ, CallbackInfo ci) {
        BiomeDecoratorDBC self = (BiomeDecoratorDBC) (Object) this;
        if (self.currentWorld != null) {
            // skip decoration if another call is already running
            ci.cancel();
        }
    }
}
