package kamkeel.npcdbc.data;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Data class for storing ability wheel slot configuration.
 * Uses string keys for abilities (built-in registry keys or custom ability UUIDs).
 */
public class AbilityWheelData {
    public int slot = -1;
    public String abilityKey = "";

    public AbilityWheelData() {
    }

    public AbilityWheelData(int slot) {
        this.slot = slot;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound wheel = new NBTTagCompound();
        wheel.setInteger("slot", slot);
        wheel.setString("abilityKey", abilityKey != null ? abilityKey : "");

        compound.setTag("AbilityWheel" + slot, wheel);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        abilityKey = compound.getString("abilityKey");
    }

    public void reset() {
        abilityKey = "";
    }

    public boolean isEmpty() {
        return abilityKey == null || abilityKey.isEmpty();
    }
}
