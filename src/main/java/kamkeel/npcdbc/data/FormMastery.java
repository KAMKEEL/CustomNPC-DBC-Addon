package kamkeel.npcdbc.data;

import kamkeel.npcdbc.api.IFormMastery;
import kamkeel.npcdbc.controllers.FormController;
import net.minecraft.nbt.NBTTagCompound;

public class FormMastery implements IFormMastery {
    public int maxLevel = 100, instantTransformationUnlockLevel = 50;
    CustomForm form;

    public FormMastery(CustomForm f) {
        form = f;
    }

    public void readFromNBT(NBTTagCompound compound) {
        maxLevel = compound.getInteger("maxLevel");
        instantTransformationUnlockLevel = compound.getInteger("instantTransformationUnlockLevel");
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound c = new NBTTagCompound();
        c.setInteger("maxLevel", maxLevel);
        c.setInteger("instantTransformationUnlockLevel", instantTransformationUnlockLevel);

        return c;
    }

    @Override
    public IFormMastery save() {
        return FormController.Instance.saveFormMastery(this);
    }
}
