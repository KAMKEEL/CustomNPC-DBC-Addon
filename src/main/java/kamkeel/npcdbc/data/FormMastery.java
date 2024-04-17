package kamkeel.npcdbc.data;

import kamkeel.npcdbc.api.IFormMastery;
import net.minecraft.nbt.NBTTagCompound;

public class FormMastery implements IFormMastery {

    public int maxLevel = 100, instantTransformationUnlockLevel = 50;
    CustomForm form;

    public FormMastery(CustomForm f) {
        form = f;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound formMastery = compound.getCompoundTag("formMastery");
        maxLevel = formMastery.getInteger("maxLevel");
        instantTransformationUnlockLevel = formMastery.getInteger("instantTransformationUnlockLevel");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound formMastery = new NBTTagCompound();
        compound.setTag("formMastery", formMastery);
        formMastery.setInteger("maxLevel", maxLevel);
        formMastery.setInteger("instantTransformationUnlockLevel", instantTransformationUnlockLevel);

        return formMastery;
    }

    @Override
    public IFormMastery save() {
        form.save();
        return this;
    }
}
