package kamkeel.npcdbc.data;

import net.minecraft.nbt.NBTTagCompound;

public class FormWheelData {
    public int slot = -1, formID = -1;
    public boolean isDBC;

    public FormWheelData() {
    }

    public FormWheelData(int slot) {
        this.slot = slot;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound wheel = new NBTTagCompound();
        wheel.setInteger("slot", slot);
        wheel.setInteger("formID", formID);
        wheel.setBoolean("isDBC", isDBC);

        compound.setTag("FormWheel" + slot, wheel);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        formID = compound.getInteger("formID");
        isDBC = compound.getBoolean("isDBC");
    }

    public void reset() {
        formID = -1;
        isDBC = false;
    }
}
