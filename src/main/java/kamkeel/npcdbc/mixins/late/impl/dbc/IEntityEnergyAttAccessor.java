package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityEnergyAtt.class, remap = false)
public interface IEntityEnergyAttAccessor {
    @Accessor("color")
    void npcdbc$setColor(int color);

    @Accessor("color2")
    void npcdbc$setColor2(int color2);
}
