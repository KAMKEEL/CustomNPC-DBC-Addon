package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.entity.EntityCusPar;
import kamkeel.npcdbc.config.ConfigDBCClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EntityCusPar.class, remap = false)
public class MixinEntityCusPar {

    @Shadow
    private Entity ent;
    @Unique
    public boolean shouldRenderInPass(int pass) {
        if (ConfigDBCClient.RevampAura && !ent.isInWater())
            return pass == 0;
        return pass == 0;
    }
}
