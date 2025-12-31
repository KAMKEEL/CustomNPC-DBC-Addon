package kamkeel.npcdbc.data;

import net.minecraft.nbt.NBTTagCompound;

public class AbilityWheelData {
    public int slot = -1, abilityID = -1;
    public boolean isDBC;

    public AbilityWheelData() {
    }

    public AbilityWheelData(int slot) {
        this.slot = slot;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound wheel = new NBTTagCompound();
        wheel.setInteger("slot", slot);
        wheel.setInteger("abilityID", abilityID);
        wheel.setBoolean("isDBC", isDBC);

        compound.setTag("AbilityWheel" + slot, wheel);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        abilityID = compound.getInteger("abilityID");
        isDBC = compound.getBoolean("isDBC");
    }

    public void reset() {
        abilityID = -1;
        isDBC = false;
    }
}
