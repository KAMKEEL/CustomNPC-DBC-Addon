package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.api.ability.IDBCAbility;
import kamkeel.npcs.controllers.data.ability.Ability;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
    // Player settings - resource costs
    public int kiCost = 0;
    public boolean kiCostPercent = false;
    public int kiDrain = 0;
    public boolean kiDrainPercent = false;
    public int staminaCost = 0;
    public boolean staminaCostPercent = false;
    public int staminaDrain = 0;
    public boolean staminaDrainPercent = false;

    // Player settings - damage configuration
    public int playerDamageType = 0;       // AbilityDamageType ordinal: 0=DEFAULT, 1=FLAT, 2=MELEE, 3=KI, 4=CNPC
    public int scalingAttribute = 0;       // DBCAttribute index: 0=STR, 1=DEX, 2=CON, 3=WIL, 4=MND, 5=SPI
    public float scalingMultiplier = 1.0f; // 0.0-10.0
    public int flatDamage = 100;
    public boolean usePlayerSettings = true; // When true, respects player DBC toggles (Ki Fist, Ki Weapon, Ki Infuse)
    // CNPC multi-set scaling
    public int scalingSetCount = 1;
    private static final int MAX_SETS = 3;
    private CNPCScalingSet[] cnpcSets = newSetsArray();

    // Not exposed in GUI - kept at defaults for IDBCStats compatibility
    private byte release = 100;
    private float dodgeChance = 0;
    private boolean canBeLockedOn = true;

    private static CNPCScalingSet[] newSetsArray() {
        CNPCScalingSet[] sets = new CNPCScalingSet[MAX_SETS];
        for (int i = 0; i < MAX_SETS; i++) sets[i] = new CNPCScalingSet();
        return sets;
    }

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
        nbt.setBoolean("KiCostPercent", kiCostPercent);
        nbt.setInteger("KiDrain", kiDrain);
        nbt.setBoolean("KiDrainPercent", kiDrainPercent);
        nbt.setInteger("StaminaCost", staminaCost);
        nbt.setBoolean("StaminaCostPercent", staminaCostPercent);
        nbt.setInteger("StaminaDrain", staminaDrain);
        nbt.setBoolean("StaminaDrainPercent", staminaDrainPercent);
        nbt.setInteger("PlayerDamageType", playerDamageType);
        nbt.setInteger("ScalingAttribute", scalingAttribute);
        nbt.setFloat("ScalingMultiplier", scalingMultiplier);
        nbt.setInteger("FlatDamage", flatDamage);
        nbt.setBoolean("UsePlayerSettings", usePlayerSettings);
        nbt.setInteger("ScalingSetCount", scalingSetCount);
        NBTTagList setList = new NBTTagList();
        for (int i = 0; i < scalingSetCount; i++) {
            NBTTagCompound setTag = new NBTTagCompound();
            cnpcSets[i].writeToNBT(setTag);
            setList.appendTag(setTag);
        }
        nbt.setTag("ScalingSets", setList);
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
        kiCostPercent = nbt.getBoolean("KiCostPercent");
        kiDrain = nbt.getInteger("KiDrain");
        kiDrainPercent = nbt.getBoolean("KiDrainPercent");
        staminaCost = nbt.getInteger("StaminaCost");
        staminaCostPercent = nbt.getBoolean("StaminaCostPercent");
        staminaDrain = nbt.getInteger("StaminaDrain");
        staminaDrainPercent = nbt.getBoolean("StaminaDrainPercent");
        playerDamageType = nbt.getInteger("PlayerDamageType");
        scalingAttribute = nbt.getInteger("ScalingAttribute");
        scalingMultiplier = nbt.getFloat("ScalingMultiplier");
        if (scalingMultiplier <= 0) scalingMultiplier = 1.0f;
        flatDamage = nbt.getInteger("FlatDamage");
        usePlayerSettings = nbt.getBoolean("UsePlayerSettings");
        scalingSetCount = nbt.getInteger("ScalingSetCount");
        if (scalingSetCount < 1) scalingSetCount = 1;
        if (scalingSetCount > MAX_SETS) scalingSetCount = MAX_SETS;
        if (nbt.hasKey("ScalingSets")) {
            NBTTagList setList = nbt.getTagList("ScalingSets", 10);
            for (int i = 0; i < Math.min(setList.tagCount(), MAX_SETS); i++) {
                cnpcSets[i].readFromNBT(setList.getCompoundTagAt(i));
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // IDBCStats IMPLEMENTATION
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    @Override
    public boolean isFriendlyFist() {
        return friendlyFist;
    }

    @Override
    public void setFriendlyFist(boolean friendlyFist) {
        this.friendlyFist = friendlyFist;
        save();
    }

    @Override
    public int getFriendlyFistAmount() {
        return friendlyFistTime;
    }

    @Override
    public void setFriendlyFistAmount(int seconds) {
        this.friendlyFistTime = ValueUtil.clamp(seconds, 1, 60);
        save();
    }

    @Override
    public boolean isIgnoreDex() {
        return ignoreDex;
    }

    @Override
    public void setIgnoreDex(boolean ignoreDex) {
        this.ignoreDex = ignoreDex;
        save();
    }

    @Override
    public boolean isIgnoreBlock() {
        return ignoreBlock;
    }

    @Override
    public void setIgnoreBlock(boolean ignoreBlock) {
        this.ignoreBlock = ignoreBlock;
        save();
    }

    @Override
    public boolean isIgnoreEndurance() {
        return ignoreEndurance;
    }

    @Override
    public void setIgnoreEndurance(boolean ignoreEndurance) {
        this.ignoreEndurance = ignoreEndurance;
        save();
    }

    @Override
    public boolean isIgnoreKiProtection() {
        return ignoreKiProtection;
    }

    @Override
    public void setIgnoreKiProtection(boolean ignoreKiProtection) {
        this.ignoreKiProtection = ignoreKiProtection;
        save();
    }

    @Override
    public boolean isIgnoreFormReduction() {
        return ignoreFormReduction;
    }

    @Override
    public void setIgnoreFormReduction(boolean ignoreFormReduction) {
        this.ignoreFormReduction = ignoreFormReduction;
        save();
    }

    @Override
    public boolean hasDefensePenetration() {
        return hasDefensePenetration;
    }

    @Override
    public void setHasDefensePenetration(boolean has) {
        this.hasDefensePenetration = has;
        save();
    }

    @Override
    public int getDefensePenetration() {
        return defensePenetration;
    }

    @Override
    public void setDefensePenetration(int pen) {
        this.defensePenetration = ValueUtil.clamp(pen, 0, 100);
        save();
    }

    @Override
    public int getKiCost() {
        return kiCost;
    }

    @Override
    public void setKiCost(int kiCost) {
        this.kiCost = Math.max(0, kiCost);
        save();
    }

    @Override
    public boolean isKiCostPercent() {
        return kiCostPercent;
    }

    @Override
    public void setKiCostPercent(boolean percent) {
        this.kiCostPercent = percent;
        save();
    }

    @Override
    public int getKiDrain() {
        return kiDrain;
    }

    @Override
    public void setKiDrain(int kiDrain) {
        this.kiDrain = Math.max(0, kiDrain);
        save();
    }

    @Override
    public boolean isKiDrainPercent() {
        return kiDrainPercent;
    }

    @Override
    public void setKiDrainPercent(boolean percent) {
        this.kiDrainPercent = percent;
        save();
    }

    @Override
    public int getStaminaCost() {
        return staminaCost;
    }

    @Override
    public void setStaminaCost(int staminaCost) {
        this.staminaCost = Math.max(0, staminaCost);
        save();
    }

    @Override
    public boolean isStaminaCostPercent() {
        return staminaCostPercent;
    }

    @Override
    public void setStaminaCostPercent(boolean percent) {
        this.staminaCostPercent = percent;
        save();
    }

    @Override
    public int getStaminaDrain() {
        return staminaDrain;
    }

    @Override
    public void setStaminaDrain(int staminaDrain) {
        this.staminaDrain = Math.max(0, staminaDrain);
        save();
    }

    @Override
    public boolean isStaminaDrainPercent() {
        return staminaDrainPercent;
    }

    @Override
    public void setStaminaDrainPercent(boolean percent) {
        this.staminaDrainPercent = percent;
        save();
    }

    // Player damage configuration
    @Override
    public int getPlayerDamageType() {
        return playerDamageType;
    }

    @Override
    public void setPlayerDamageType(int type) {
        this.playerDamageType = ValueUtil.clamp(type, 0, 4);
        save();
    }

    @Override
    public int getScalingAttribute() {
        return scalingAttribute;
    }

    @Override
    public void setScalingAttribute(int attr) {
        this.scalingAttribute = ValueUtil.clamp(attr, 0, 5);
        save();
    }

    @Override
    public float getScalingMultiplier() {
        return scalingMultiplier;
    }

    @Override
    public void setScalingMultiplier(float mult) {
        this.scalingMultiplier = ValueUtil.clamp(mult, 0.0f, 10.0f);
        save();
    }

    @Override
    public int getFlatDamage() {
        return flatDamage;
    }

    @Override
    public void setFlatDamage(int dmg) {
        this.flatDamage = Math.max(0, dmg);
        save();
    }

    @Override
    public boolean getUsePlayerSettings() {
        return usePlayerSettings;
    }

    @Override
    public void setUsePlayerSettings(boolean use) {
        this.usePlayerSettings = use;
        save();
    }

    @Override
    public int getScalingSetCount() {
        return scalingSetCount;
    }

    @Override
    public void setScalingSetCount(int count) {
        this.scalingSetCount = ValueUtil.clamp(count, 1, 3);
        save();
    }

    // ═══════════════════════════════════════════════════════════════════
    // CNPC SET ACCESSORS (used by formula and GUI)
    // ═══════════════════════════════════════════════════════════════════

    private int clampSet(int set) {
        return ValueUtil.clamp(set, 0, MAX_SETS - 1);
    }

    public CNPCScalingSet getSet(int set) {
        return cnpcSets[clampSet(set)];
    }

    public int getAttributeForSet(int set) {
        return cnpcSets[clampSet(set)].attribute;
    }

    public void setAttributeForSet(int set, int attr) {
        cnpcSets[clampSet(set)].attribute = ValueUtil.clamp(attr, 0, 5);
        save();
    }

    public int getStatTypeForSet(int set) {
        return cnpcSets[clampSet(set)].statType;
    }

    public void setStatTypeForSet(int set, int type) {
        cnpcSets[clampSet(set)].statType = ValueUtil.clamp(type, 0, 5);
        save();
    }

    public boolean isStatEnabledForSet(int set) {
        return cnpcSets[clampSet(set)].statEnabled;
    }

    public void setStatEnabledForSet(int set, boolean enabled) {
        cnpcSets[clampSet(set)].statEnabled = enabled;
        save();
    }

    public float getMultiplierForSet(int set) {
        return cnpcSets[clampSet(set)].multiplier;
    }

    public void setMultiplierForSet(int set, float mult) {
        cnpcSets[clampSet(set)].multiplier = ValueUtil.clamp(mult, 0.0f, 10.0f);
        save();
    }

    public boolean isKiFistForSet(int set) {
        return cnpcSets[clampSet(set)].kiFist;
    }

    public void setKiFistForSet(int set, boolean v) {
        cnpcSets[clampSet(set)].kiFist = v;
        save();
    }

    public boolean isKiWeaponForSet(int set) {
        return cnpcSets[clampSet(set)].kiWeapon;
    }

    public void setKiWeaponForSet(int set, boolean v) {
        cnpcSets[clampSet(set)].kiWeapon = v;
        save();
    }

    public boolean isKiInfuseForSet(int set) {
        return cnpcSets[clampSet(set)].kiInfuse;
    }

    public void setKiInfuseForSet(int set, boolean v) {
        cnpcSets[clampSet(set)].kiInfuse = v;
        save();
    }

    // Not exposed in GUI - defaults for IDBCStats compatibility
    @Override
    public byte getRelease() {
        return release;
    }

    @Override
    public void setRelease(byte release) {
        this.release = release;
    }

    @Override
    public float getDodgeChance() {
        return dodgeChance;
    }

    @Override
    public void setDodgeChance(float dodge) {
        this.dodgeChance = dodge;
    }

    @Override
    public boolean canBeLockedOn() {
        return canBeLockedOn;
    }

    @Override
    public void setLockOnState(boolean canBeLockedOn) {
        this.canBeLockedOn = canBeLockedOn;
    }
}
