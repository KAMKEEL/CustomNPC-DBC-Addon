package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.entity.EntityCusPar;
import kamkeel.npcdbc.config.ConfigDBCClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EntityCusPar.class, remap = false)
public class MixinEntityCusPar {


    @Unique
    public boolean shouldRenderInPass(int pass) {
        if (ConfigDBCClient.RevampAura)
            return pass == 1;
        return pass == 0;
    }

}
