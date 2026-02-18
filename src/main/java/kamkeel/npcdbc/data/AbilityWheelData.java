package kamkeel.npcdbc.data;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Data class for storing ability wheel slot configuration.
 * Uses string keys for abilities (built-in registry keys or custom ability UUIDs)
 * or chained abilities (prefixed with {@link #CHAIN_PREFIX}).
 */
public class AbilityWheelData {
    /** Prefix for chained ability keys, matching PlayerAbilityData convention. */
    public static final String CHAIN_PREFIX = "chain:";

    public int slot = -1;
    public String abilityKey = "";

    public boolean isChainKey() {
        return abilityKey != null && abilityKey.startsWith(CHAIN_PREFIX);
    }

    public String getResolveKey() {
        if (isChainKey()) return abilityKey.substring(CHAIN_PREFIX.length());
        return abilityKey;
    }

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
