package kamkeel.npcdbc.mixin;

import kamkeel.npcdbc.data.PlayerCustomFormData;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Unique;

public interface IPlayerFormData {
    PlayerCustomFormData getCustomFormData();

    boolean getFormUpdate();

    void updateFormInfo();

    void finishFormInfo();
}
