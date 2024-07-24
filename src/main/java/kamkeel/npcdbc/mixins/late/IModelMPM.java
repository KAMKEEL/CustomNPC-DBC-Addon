package kamkeel.npcdbc.mixins.late;

import kamkeel.npcdbc.client.model.ModelDBC;
import net.minecraft.client.model.ModelBase;
import org.spongepowered.asm.mixin.Unique;

public interface IModelMPM {
    @Unique
    ModelDBC getDBCModel();

    @Unique
    ModelBase getMainModel();
}
