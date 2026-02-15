package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.api.ability.IDBCAbility;
import kamkeel.npcs.controllers.data.ability.Ability;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.util.ValueUtil;

/**
 * DBC combat stats for abilities, implementing {@link IDBCAbility} so the
 * existing {@code calculateDBCStatDamage} and {@code doDBCDamage} methods
 * can be used directly.
 * <p>
 * Stored in the ability's customData NBT under the "DBCAbilityStats" tag.
 * Fields like release, dodgeChance, and canBeLockedOn are kept at defaults
 * (not exposed in the GUI) since they are NPC-level concerns, not ability-level.
 */
public class DBCAbilityStats implements IDBCAbility {
    private static final String NBT_KEY = "DBCAbilityStats";

    private final NBTTagCompound customData;

    public boolean enabled = false;
    public boolean friendlyFist = false;
    public int friendlyFistTime = 6;
    public boolean ignoreDex = false;
    public boolean ignoreBlock = false;
    public boolean ignoreEndurance = false;
    public boolean ignoreKiProtection = false;
    public boolean ignoreFormReduction = false;
    public boolean hasDefensePenetration = false;
    public int defensePenetration = 10;
    // Player settings
    public int kiCost = 0;
    public int kiDrain = 0;
    public int staminaCost = 0;
    public int staminaDrain = 0;

    // Not exposed in GUI - kept at defaults for IDBCStats compatibility
    private byte release = 100;
    private float dodgeChance = 0;
    private boolean canBeLockedOn = true;

    private DBCAbilityStats(NBTTagCompound customData) {
        this.customData = customData;
    }

    /**
     * Create a DBCAbilityStats instance from an ability's customData.
     * Reads existing values if present, otherwise uses defaults.
     */
    public static DBCAbilityStats fromAbility(Ability ability) {
        NBTTagCompound customData = ability.getCustomData();
        DBCAbilityStats stats = new DBCAbilityStats(customData);
        if (customData.hasKey(NBT_KEY)) {
            stats.readFromNBT(customData.getCompoundTag(NBT_KEY));
        }
        return stats;
    }

    /**
     * Write current state back to the customData NBT.
     */
    public void save() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        customData.setTag(NBT_KEY, tag);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setBoolean("Enabled", enabled);
        nbt.setBoolean("FriendlyFist", friendlyFist);
        nbt.setInteger("FriendlyFistTime", friendlyFistTime);
        nbt.setBoolean("IgnoreDex", ignoreDex);
        nbt.setBoolean("IgnoreBlock", ignoreBlock);
        nbt.setBoolean("IgnoreEndurance", ignoreEndurance);
        nbt.setBoolean("IgnoreKiProtection", ignoreKiProtection);
        nbt.setBoolean("IgnoreFormReduction", ignoreFormReduction);
        nbt.setBoolean("HasDefensePen", hasDefensePenetration);
        nbt.setInteger("DefensePen", defensePenetration);
        nbt.setInteger("KiCost", kiCost);
        nbt.setInteger("KiDrain", kiDrain);
        nbt.setInteger("StaminaCost", staminaCost);
        nbt.setInteger("StaminaDrain", staminaDrain);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        enabled = nbt.getBoolean("Enabled");
        friendlyFist = nbt.getBoolean("FriendlyFist");
        friendlyFistTime = nbt.getInteger("FriendlyFistTime");
        if (friendlyFistTime <= 0) friendlyFistTime = 6;
        ignoreDex = nbt.getBoolean("IgnoreDex");
        ignoreBlock = nbt.getBoolean("IgnoreBlock");
        ignoreEndurance = nbt.getBoolean("IgnoreEndurance");
        ignoreKiProtection = nbt.getBoolean("IgnoreKiProtection");
        ignoreFormReduction = nbt.getBoolean("IgnoreFormReduction");
        hasDefensePenetration = nbt.getBoolean("HasDefensePen");
        defensePenetration = nbt.getInteger("DefensePen");
        kiCost = nbt.getInteger("KiCost");
        kiDrain = nbt.getInteger("KiDrain");
        staminaCost = nbt.getInteger("StaminaCost");
        staminaDrain = nbt.getInteger("StaminaDrain");
    }

    // ═══════════════════════════════════════════════════════════════════
    // IDBCStats IMPLEMENTATION
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public boolean isEnabled() { return enabled; }

    @Override
    public void setEnabled(boolean enabled) { this.enabled = enabled; save(); }

    @Override
    public boolean isFriendlyFist() { return friendlyFist; }

    @Override
    public void setFriendlyFist(boolean friendlyFist) { this.friendlyFist = friendlyFist; save(); }

    @Override
    public int getFriendlyFistAmount() { return friendlyFistTime; }

    @Override
    public void setFriendlyFistAmount(int seconds) { this.friendlyFistTime = ValueUtil.clamp(seconds, 1, 60); save(); }

    @Override
    public boolean isIgnoreDex() { return ignoreDex; }

    @Override
    public void setIgnoreDex(boolean ignoreDex) { this.ignoreDex = ignoreDex; save(); }

    @Override
    public boolean isIgnoreBlock() { return ignoreBlock; }

    @Override
    public void setIgnoreBlock(boolean ignoreBlock) { this.ignoreBlock = ignoreBlock; save(); }

    @Override
    public boolean isIgnoreEndurance() { return ignoreEndurance; }

    @Override
    public void setIgnoreEndurance(boolean ignoreEndurance) { this.ignoreEndurance = ignoreEndurance; save(); }

    @Override
    public boolean isIgnoreKiProtection() { return ignoreKiProtection; }

    @Override
    public void setIgnoreKiProtection(boolean ignoreKiProtection) { this.ignoreKiProtection = ignoreKiProtection; save(); }

    @Override
    public boolean isIgnoreFormReduction() { return ignoreFormReduction; }

    @Override
    public void setIgnoreFormReduction(boolean ignoreFormReduction) { this.ignoreFormReduction = ignoreFormReduction; save(); }

    @Override
    public boolean hasDefensePenetration() { return hasDefensePenetration; }

    @Override
    public void setHasDefensePenetration(boolean has) { this.hasDefensePenetration = has; save(); }

    @Override
    public int getDefensePenetration() { return defensePenetration; }

    @Override
    public void setDefensePenetration(int pen) { this.defensePenetration = ValueUtil.clamp(pen, 0, 100); save(); }

    @Override
    public int getKiCost() { return kiCost; }

    @Override
    public void setKiCost(int kiCost) { this.kiCost = Math.max(0, kiCost); save(); }

    @Override
    public int getKiDrain() { return kiDrain; }

    @Override
    public void setKiDrain(int kiDrain) { this.kiDrain = Math.max(0, kiDrain); save(); }

    @Override
    public int getStaminaCost() { return staminaCost; }

    @Override
    public void setStaminaCost(int staminaCost) { this.staminaCost = Math.max(0, staminaCost); save(); }

    @Override
    public int getStaminaDrain() { return staminaDrain; }

    @Override
    public void setStaminaDrain(int staminaDrain) { this.staminaDrain = Math.max(0, staminaDrain); save(); }

    // Not exposed in GUI - defaults for IDBCStats compatibility
    @Override
    public byte getRelease() { return release; }

    @Override
    public void setRelease(byte release) { this.release = release; }

    @Override
    public float getDodgeChance() { return dodgeChance; }

    @Override
    public void setDodgeChance(float dodge) { this.dodgeChance = dodge; }

    @Override
    public boolean canBeLockedOn() { return canBeLockedOn; }

    @Override
    public void setLockOnState(boolean canBeLockedOn) { this.canBeLockedOn = canBeLockedOn; }
}
