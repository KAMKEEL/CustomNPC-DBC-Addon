package kamkeel.npcdbc.data.ability;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.util.ValueUtil;

/**
 * One set in the CNPC+ multi-set damage formula.
 * Each set defines an attribute/stat combination with a multiplier,
 * plus gates that allow Ki Fist, Ki Weapon, and Ki Infuse bonuses
 * to contribute (still respects the player's own DBC toggle settings).
 */
public class CNPCScalingSet {
    public int attribute = 0;        // 0-5 (STR/DEX/CON/WIL/MND/SPI)
    public int statType = 0;         // 0-5 (Melee/Defense/Body/Stamina/EnergyPower/EnergyPool)
    public boolean statEnabled = true;
    public float multiplier = 1.0f;
    public boolean kiFist = false;
    public boolean kiWeapon = false;
    public boolean kiInfuse = false;

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("Attribute", attribute);
        nbt.setInteger("StatType", statType);
        nbt.setBoolean("StatEnabled", statEnabled);
        nbt.setFloat("Multiplier", multiplier);
        nbt.setBoolean("KiFist", kiFist);
        nbt.setBoolean("KiWeapon", kiWeapon);
        nbt.setBoolean("KiInfuse", kiInfuse);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        attribute = ValueUtil.clamp(nbt.getInteger("Attribute"), 0, 5);
        statType = ValueUtil.clamp(nbt.getInteger("StatType"), 0, 5);
        statEnabled = nbt.hasKey("StatEnabled") ? nbt.getBoolean("StatEnabled") : true;
        multiplier = nbt.getFloat("Multiplier");
        if (multiplier <= 0) multiplier = 1.0f;
        kiFist = nbt.getBoolean("KiFist");
        kiWeapon = nbt.getBoolean("KiWeapon");
        kiInfuse = nbt.getBoolean("KiInfuse");
    }
}
