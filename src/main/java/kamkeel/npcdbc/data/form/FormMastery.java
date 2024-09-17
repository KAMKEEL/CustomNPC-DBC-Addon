package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.api.form.IFormMastery;
import kamkeel.npcdbc.api.form.IFormMasteryLinkData;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.util.ValueUtil;

public class FormMastery implements IFormMastery {

    private final Form parent;

    public FormMasteryLinkData masteryLink = new FormMasteryLinkData(this);

    public float maxLevel = 100;
    public float instantTransformationUnlockLevel = -1;

    // Attribute
    public float attributeMultiFlat = 1.0f, attributeMultiPerLevel = 0.01f, attributeMultiMinOrMax = 1.5f;

    // Gain
    public float updateGain = 0.01f, updateMultiDivPlus = 100, updateMindBonusFlat = 1.0f, updateMindBonusPerMind = 0.001f, updateMindBonusMax = 5f;
    public float attackGain = 0.03f, attackMultiDivPlus = 100, attackMindBonusFlat = 1.0f, attackMindBonusPerMind = 0.001f, attackMindBonusMax = 5f;
    public float damagedGain = 0.03f, damagedMultiDivPlus = 100, damagedMindBonusFlat = 1.0f, damagedMindBonusPerMind = 0.001f, damagedMindBonusMax = 5f;
    public float fireKiGain = 0.03f, fireKiMultiDivPlus = 100, fireKiMindBonusFlat = 1.0f, fireKiMindBonusPerMind = 0.001f, fireKiMindBonusMax = 5;

    public float healthRequirement = 100f;
    public float healthRequirementMultiFlat = 1.0f, healthRequirementMultiPerLevel = 0.01f, healthRequirementMultiMinOrMax = 5f;

    public float kiDrain = 1;
    public int kiDrainTimer = 100;
    public float kiDrainMultiFlat = 1.0f, kiDrainMultiPerLevel = -0.01f, kiDrainMultiMinOrMax = 0.1f;

    public int maxHeat = 0; //seconds it takes to reach max heat
    public float heatMultiFlat = 1.0f, heatMultiPerLevel = -0.01f, heatMultiMinOrMax = 0f; //0 min so form doesn't generate heat at max level

    public int painTime = 0; //in minutes
    public float painMultiFlat = 1.0f, painMultiPerLevel = -0.01f, painMultiMinOrMax = 0f; //0 min so form doesn't generate pain at max level

    public float dodgeChance = 0f;
    public float dodgeMultiFlat = 1.0f, dodgeMultiPerLevel = 0.01f, dodgeMultiMinOrMax = 2f;

    public float damageNegation = 0f;
    public float damageNegationMultiFlat = 1.0f, damageNegationMultiPerLevel = 0.01f, damageNegationMultiMinOrMax = 2f;

    public float movementSpeed = 1f;
    public float movementSpeedMultiFlat = 1.0f, movementSpeedMultiPerLevel = 0.01f, movementSpeedMultiMinOrMax = 1.5f;

    public float tailCutChance = 100;
    public float tailCutChanceMultiFlat = 1.0f, tailCutChanceMultiPerLevel = -0.01f, tailCutChanceMultiMinOrMax = 0f;

    public boolean powerPointEnabled;
    public int powerPointCost = 100, powerPointGrowth = 0;
    public float powerPointMultiNormal = 1, powerPointMultiBasedOnPoints = -1;
    public float powerPointCostMultiFlat = 1.0f, powerPointCostPerLevel = -0.01f, powerPointCostMinOrMax = 0f;

    public boolean absorptionEnabled;
    public float absorptionMulti = 1;


    public boolean destroyerEnabled = false;
    public float destroyerKiDamage = 0.8f;
    public float destroyerKiDamageMultiFlat = 1.0f, destroyerKiDamageMultiPerLevel = 0.01f, destroyerKiDamageMultiMinOrMax = 1.5f;


    public FormMastery(Form parent) {
        this.parent = parent;
    }

    @Override
    public int getPainTime() {
        return painTime;
    }

    @Override
    public void setPainTime(int painTime) {
        this.painTime = painTime;
    }

    @Override
    public boolean hasPainTime() {
        return painTime > 0;
    }

    @Override
    public int getMaxHeat() {
        return maxHeat;
    }

    @Override
    public void setMaxHeat(int maxHeat) {
        this.maxHeat = maxHeat;
    }

    @Override
    public boolean hasHeat() {
        return maxHeat > 1;
    }

    @Override
    public float getDamageNegation() {
        return damageNegation;
    }

    @Override
    public void setDamageNegation(float damageNegation) {
        this.damageNegation = damageNegation;
    }

    @Override
    public boolean hasDamageNegation() {
        return damageNegation > 0;
    }

    @Override
    public float getDodgeChance() {
        return dodgeChance;
    }

    @Override
    public void setDodgeChance(float dodgeChance) {
        this.dodgeChance = dodgeChance;
    }

    @Override
    public boolean hasDodge() {
        return dodgeChance > 0;
    }

    @Override
    public float getKiDrain() {
        return kiDrain;
    }

    @Override
    public void setKiDrain(float kiDrain) {
        this.kiDrain = kiDrain;
    }

    @Override
    public boolean hasKiDrain() {
        return kiDrain != 0;
    }

    @Override
    public int getKiDrainTimer() {
        return kiDrainTimer;
    }

    @Override
    public void setKiDrainTimer(int timeInTicks) {
        this.kiDrainTimer = timeInTicks;
    }

    @Override
    public float getHealthRequirement() {
        return healthRequirement;
    }

    @Override
    public void setHealthRequirement(float healthRequirementInPercent) {
        this.healthRequirement = ValueUtil.clamp(healthRequirementInPercent, 1, 100);
    }

    @Override
    public float getMulti(String type, String type1) {
        switch (type.toLowerCase()) {
            case "attribute":
                switch (type1.toLowerCase()) {
                    case "flat":
                        return attributeMultiFlat;
                    case "perlevel":
                        return attributeMultiPerLevel;
                    case "minormax":
                        return attributeMultiMinOrMax;
                }
            case "kidrain":
                switch (type1.toLowerCase()) {
                    case "flat":
                        return kiDrainMultiFlat;
                    case "perlevel":
                        return kiDrainMultiPerLevel;
                    case "minormax":
                        return kiDrainMultiMinOrMax;
                }
            case "heat":
                switch (type1.toLowerCase()) {
                    case "flat":
                        return heatMultiFlat;
                    case "perlevel":
                        return heatMultiPerLevel;
                    case "minormax":
                        return heatMultiMinOrMax;
                }
            case "pain":
                switch (type1.toLowerCase()) {
                    case "flat":
                        return painMultiFlat;
                    case "perlevel":
                        return painMultiPerLevel;
                    case "minormax":
                        return painMultiMinOrMax;
                }
            case "healthrequirement":
                switch (type1.toLowerCase()) {
                    case "flat":
                        return healthRequirementMultiFlat;
                    case "perlevel":
                        return healthRequirementMultiPerLevel;
                    case "minormax":
                        return healthRequirementMultiMinOrMax;
                }
            case "dodge":
                switch (type1.toLowerCase()) {
                    case "flat":
                        return dodgeMultiFlat;
                    case "perlevel":
                        return dodgeMultiPerLevel;
                    case "minormax":
                        return dodgeMultiMinOrMax;
                }
            case "damagenegation":
                switch (type1.toLowerCase()) {
                    case "flat":
                        return damageNegationMultiFlat;
                    case "perlevel":
                        return damageNegationMultiPerLevel;
                    case "minormax":
                        return damageNegationMultiMinOrMax;
                }
            case "movementspeed":
                switch (type1.toLowerCase()) {
                    case "flat":
                        return movementSpeedMultiFlat;
                    case "perlevel":
                        return movementSpeedMultiPerLevel;
                    case "minormax":
                        return movementSpeedMultiMinOrMax;
                }
            case "tailcutchance":
                switch (type1.toLowerCase()) {
                    case "flat":
                        return tailCutChanceMultiFlat;
                    case "perlevel":
                        return tailCutChanceMultiPerLevel;
                    case "minormax":
                        return tailCutChanceMultiMinOrMax;
                }
            case "ppcost":
                switch (type1.toLowerCase()){
                    case "flat":
                        return powerPointCostMultiFlat;
                    case "perlevel":
                        return powerPointCostPerLevel;
                    case "minormax":
                        return powerPointCostMinOrMax;
                }
            case "destroyerkidamage":
                switch(type1.toLowerCase()){
                    case "flat":
                        return destroyerKiDamageMultiFlat;
                    case "perlevel":
                        return destroyerKiDamageMultiPerLevel;
                    case "minormax":
                        return destroyerKiDamageMultiMinOrMax;
                }
        }
        return 1.0f;
    }

    public void setMulti(String type, String type1, float value) {
        switch (type.toLowerCase()) {
            case "attribute":
                switch (type1.toLowerCase()) {
                    case "flat":
                        attributeMultiFlat = value;
                        break;
                    case "perlevel":
                        attributeMultiPerLevel = value;
                        break;
                    case "minormax":
                        attributeMultiMinOrMax = value;
                        break;
                }
                break;
            case "kidrain":
                switch (type1.toLowerCase()) {
                    case "flat":
                        kiDrainMultiFlat = value;
                        break;
                    case "perlevel":
                        kiDrainMultiPerLevel = value;
                        break;
                    case "minormax":
                        kiDrainMultiMinOrMax = value;
                        break;
                }
                break;
            case "heat":
                switch (type1.toLowerCase()) {
                    case "flat":
                        heatMultiFlat = value;
                        break;
                    case "perlevel":
                        heatMultiPerLevel = value;
                        break;
                    case "minormax":
                        heatMultiMinOrMax = value;
                        break;
                }
                break;
            case "pain":
                switch (type1.toLowerCase()) {
                    case "flat":
                        painMultiFlat = value;
                        break;
                    case "perlevel":
                        painMultiPerLevel = value;
                        break;
                    case "minormax":
                        painMultiMinOrMax = value;
                        break;
                }
                break;
            case "healthrequirement":
                switch (type1.toLowerCase()) {
                    case "flat":
                        healthRequirementMultiFlat = value;
                        break;
                    case "perlevel":
                        healthRequirementMultiPerLevel = value;
                        break;
                    case "minormax":
                        healthRequirementMultiMinOrMax = value;
                        break;
                }
                break;
            case "dodge":
                switch (type1.toLowerCase()) {
                    case "flat":
                        dodgeMultiFlat = value;
                        break;
                    case "perlevel":
                        dodgeMultiPerLevel = value;
                        break;
                    case "minormax":
                        dodgeMultiMinOrMax = value;
                        break;
                }
                break;
            case "damagenegation":
                switch (type1.toLowerCase()) {
                    case "flat":
                        damageNegationMultiFlat = value;
                        break;
                    case "perlevel":
                        damageNegationMultiPerLevel = value;
                        break;
                    case "minormax":
                        damageNegationMultiMinOrMax = value;
                        break;
                }
            case "movementspeed":
                switch (type1.toLowerCase()) {
                    case "flat":
                        movementSpeedMultiFlat = value;
                        break;
                    case "perlevel":
                        movementSpeedMultiPerLevel = value;
                        break;
                    case "minormax":
                        movementSpeedMultiMinOrMax = value;
                        break;
                }
            case "tailcutchance":
                switch (type1.toLowerCase()) {
                    case "flat":
                        tailCutChanceMultiFlat = value;
                        break;
                    case "perlevel":
                        tailCutChanceMultiPerLevel = value;
                        break;
                    case "minormax":
                        tailCutChanceMultiMinOrMax = value;
                        break;
                }
            case "ppcost":
                switch (type1.toLowerCase()){
                    case "flat":
                        powerPointCostMultiFlat = value;
                    case "perlevel":
                        powerPointCostPerLevel = value;
                    case "minormax":
                        powerPointCostMinOrMax = value;
                }
            case "destroyerkidamage":
                switch(type1.toLowerCase()){
                    case "flat":
                        destroyerKiDamageMultiFlat = value;
                    case "perlevel":
                        destroyerKiDamageMultiPerLevel = value;
                    case "minormax":
                        destroyerKiDamageMultiMinOrMax = value;
                }

        }
    }

    public float getGain(String eventType, String type) {
        switch (eventType.toLowerCase()) {
            case "update":
                switch (type.toLowerCase()) {
                    case "gain":
                        return updateGain;
                    case "multidivplus":
                        return updateMultiDivPlus;
                    case "flat":
                        return updateMindBonusFlat;
                    case "permind":
                        return updateMindBonusPerMind;
                    case "max":
                        return updateMindBonusMax;
                }
            case "attack":
                switch (type.toLowerCase()) {
                    case "gain":
                        return attackGain;
                    case "multidivplus":
                        return attackMultiDivPlus;
                    case "flat":
                        return attackMindBonusFlat;
                    case "permind":
                        return attackMindBonusPerMind;
                    case "max":
                        return attackMindBonusMax;
                }
            case "damaged":
                switch (type.toLowerCase()) {
                    case "gain":
                        return damagedGain;
                    case "multidivplus":
                        return damagedMultiDivPlus;
                    case "flat":
                        return damagedMindBonusFlat;
                    case "permind":
                        return damagedMindBonusPerMind;
                    case "max":
                        return damagedMindBonusMax;
                }
            case "fireki":
                switch (type.toLowerCase()) {
                    case "gain":
                        return fireKiGain;
                    case "multidivplus":
                        return fireKiMultiDivPlus;
                    case "flat":
                        return fireKiMindBonusFlat;
                    case "permind":
                        return fireKiMindBonusPerMind;
                    case "max":
                        return fireKiMindBonusMax;
                }

        }
        return 1.0f;
    }

    public void setGain(String eventType, String type, float value) {
        switch (eventType.toLowerCase()) {
            case "update":
                switch (type.toLowerCase()) {
                    case "gain":
                        updateGain = value;
                        break;
                    case "multidivplus":
                        updateMultiDivPlus = value;
                        break;
                    case "flat":
                        updateMindBonusFlat = value;
                        break;
                    case "permind":
                        updateMindBonusPerMind = value;
                        break;
                    case "max":
                        updateMindBonusMax = value;
                        break;
                }
                break;
            case "attack":
                switch (type.toLowerCase()) {
                    case "gain":
                        attackGain = value;
                        break;
                    case "multidivplus":
                        attackMultiDivPlus = value;
                        break;
                    case "flat":
                        attackMindBonusFlat = value;
                        break;
                    case "permind":
                        attackMindBonusPerMind = value;
                        break;
                    case "max":
                        attackMindBonusMax = value;
                        break;
                }
                break;
            case "damaged":
                switch (type.toLowerCase()) {
                    case "gain":
                        damagedGain = value;
                        break;
                    case "multidivplus":
                        damagedMultiDivPlus = value;
                        break;
                    case "flat":
                        damagedMindBonusFlat = value;
                        break;
                    case "permind":
                        damagedMindBonusPerMind = value;
                        break;
                    case "max":
                        damagedMindBonusMax = value;
                        break;
                }
                break;
            case "fireki":
                switch (type.toLowerCase()) {
                    case "gain":
                        fireKiGain = value;
                        break;
                    case "multidivplus":
                        fireKiMultiDivPlus = value;
                        break;
                    case "flat":
                        fireKiMindBonusFlat = value;
                        break;
                    case "permind":
                        fireKiMindBonusPerMind = value;
                        break;
                    case "max":
                        fireKiMindBonusMax = value;
                        break;
                }
                break;

        }

    }

    public float getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(float value) {
        maxLevel = value;
    }

    public float getInstantTransformationUnlockLevel() {
        return instantTransformationUnlockLevel;
    }

    public void setInstantTransformationUnlockLevel(float value) {
        this.instantTransformationUnlockLevel = value;
    }

    @Override
    public boolean hasInstantTransformationUnlockLevel() {
        return instantTransformationUnlockLevel > -1;
    }

    public boolean canInstantTransform(float curLevel) {
        return curLevel >= instantTransformationUnlockLevel;
    }

    public float calculateMulti(String type, float playerLevel) {
        float flat = getMulti(type, "flat");
        float perLevel = getMulti(type, "perlevel");
        float minOrMax = getMulti(type, "minormax");
        float value = perLevel * playerLevel + flat;

        boolean downwards = perLevel < 0.0;
        if (downwards ? value < minOrMax : value > minOrMax)
            value = minOrMax;

        return value;
    }

    public float calculateGainMindMulti(String type, int playerMind) {
        float max = getGain(type, "max");
        if (max <= 0.0)
            return max;

        float flat = getGain(type, "flat");
        float perMind = getGain(type, "permind");
        float multi = playerMind * perMind + flat;

        return multi > max ? max : multi;
    }

    public float calculateMultipliedGain(String type, float playerLevel) {
        float gain = getGain(type, "gain");
        float multiDivPlus = getGain(type, "multidivplus");
        if (multiDivPlus != 0.0 && gain != 0.0) {
            float lossFromLevel = playerLevel / (playerLevel + multiDivPlus);
            return gain - gain * lossFromLevel;
        }
        return gain;

    }

    public float calculateFullGain(String type, float playerLevel, int playerMind) {
        return calculateMultipliedGain(type, playerLevel) * calculateGainMindMulti(type, playerMind);
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound formMastery = compound.getCompoundTag("formMastery");

        maxLevel = formMastery.getInteger("maxLevel");
        instantTransformationUnlockLevel = formMastery.getFloat("instantTransformationUnlockLevel");
        kiDrain = formMastery.getFloat("kiDrain");
        kiDrainTimer = formMastery.getInteger("kiDrainTimer");
        healthRequirement = formMastery.getFloat("healthRequirement");
        dodgeChance = formMastery.getFloat("dodgeChance");
        damageNegation = formMastery.getFloat("damageNegation");
        painTime = formMastery.getInteger("painTime");
        maxHeat = formMastery.getInteger("maxHeat");
        movementSpeed = formMastery.getFloat("movementSpeed");
        tailCutChance = formMastery.getFloat("tailCutChance");

        NBTTagCompound attributeMulti = formMastery.getCompoundTag("attributeMulti");
        attributeMultiFlat = attributeMulti.getFloat("flat");
        attributeMultiPerLevel = attributeMulti.getFloat("perLevel");
        attributeMultiMinOrMax = attributeMulti.getFloat("max");

        NBTTagCompound kiDrainMulti = formMastery.getCompoundTag("kiDrainMulti");
        kiDrainMultiFlat = kiDrainMulti.getFloat("flat");
        kiDrainMultiPerLevel = kiDrainMulti.getFloat("perLevel");
        kiDrainMultiMinOrMax = kiDrainMulti.getFloat("minOrMax");

        NBTTagCompound heatMulti = formMastery.getCompoundTag("heatMulti");
        heatMultiFlat = heatMulti.getFloat("flat");
        heatMultiPerLevel = heatMulti.getFloat("perLevel");
        heatMultiMinOrMax = heatMulti.getFloat("minOrMax");

        NBTTagCompound painMulti = formMastery.getCompoundTag("painMulti");
        painMultiFlat = painMulti.getFloat("flat");
        painMultiPerLevel = painMulti.getFloat("perLevel");
        painMultiMinOrMax = painMulti.getFloat("minOrMax");

        NBTTagCompound healthRequirementMulti = formMastery.getCompoundTag("healthRequirementMulti");
        healthRequirementMultiFlat = healthRequirementMulti.getFloat("flat");
        healthRequirementMultiPerLevel = healthRequirementMulti.getFloat("perLevel");
        healthRequirementMultiMinOrMax = healthRequirementMulti.getFloat("minOrMax");

        NBTTagCompound dodgeMulti = formMastery.getCompoundTag("dodgeMulti");
        dodgeMultiFlat = dodgeMulti.getFloat("flat");
        dodgeMultiPerLevel = dodgeMulti.getFloat("perLevel");
        dodgeMultiMinOrMax = dodgeMulti.getFloat("minOrMax");

        NBTTagCompound damageNegationMulti = formMastery.getCompoundTag("damageNegationMulti");
        damageNegationMultiFlat = damageNegationMulti.getFloat("flat");
        damageNegationMultiPerLevel = damageNegationMulti.getFloat("perLevel");
        damageNegationMultiMinOrMax = damageNegationMulti.getFloat("minOrMax");

        NBTTagCompound movementSpeedMulti = formMastery.getCompoundTag("movementSpeedMulti");
        movementSpeedMultiFlat = movementSpeedMulti.getFloat("flat");
        movementSpeedMultiPerLevel = movementSpeedMulti.getFloat("perLevel");
        movementSpeedMultiMinOrMax = movementSpeedMulti.getFloat("minOrMax");

        NBTTagCompound tailCutChanceMulti = formMastery.getCompoundTag("tailCutMulti");
        tailCutChanceMultiFlat = tailCutChanceMulti.getFloat("flat");
        tailCutChanceMultiPerLevel = tailCutChanceMulti.getFloat("perLevel");
        tailCutChanceMultiMinOrMax = tailCutChanceMulti.getFloat("minOrMax");

        if(formMastery.hasKey("racialBonuses")){
            NBTTagCompound racialBonuses = formMastery.getCompoundTag("racialBonuses");

            absorptionEnabled = racialBonuses.getBoolean("absorptionEnabled");
            absorptionMulti = racialBonuses.getFloat("absorptionMulti");


            powerPointEnabled = racialBonuses.getBoolean("powerPointEnabled");
            powerPointCost = racialBonuses.getInteger("powerPointCost");
            powerPointGrowth = racialBonuses.getInteger("powerPointGrowth");

            powerPointMultiNormal = racialBonuses.getFloat("powerPointMultiNormal");
            powerPointMultiBasedOnPoints = racialBonuses.getFloat("powerPointMultiBasedOnPoints");

            NBTTagCompound powerPointMastery = racialBonuses.getCompoundTag("powerPointMastery");
            powerPointCostMultiFlat = powerPointMastery.getFloat("flat");
            powerPointCostPerLevel = powerPointMastery.getFloat("perLevel");
            powerPointCostMinOrMax = powerPointMastery.getFloat("minOrMax");
        }

        if(formMastery.hasKey("destroyerConfigs")){
            NBTTagCompound destroyerConfigs = formMastery.getCompoundTag("destroyerConfigs");
            destroyerEnabled = destroyerConfigs.getBoolean("enabled");
            destroyerKiDamageMultiFlat = destroyerConfigs.getFloat("flat");
            destroyerKiDamageMultiPerLevel = destroyerConfigs.getFloat("perLevel");
            destroyerKiDamageMultiMinOrMax = destroyerConfigs.getFloat("minOrMax");
            destroyerKiDamage = destroyerConfigs.getFloat("damage");
        }


        NBTTagCompound update = formMastery.getCompoundTag("update");
        updateGain = update.getFloat("gain");
        updateMultiDivPlus = update.getFloat("multiDivPlus");
        updateMindBonusFlat = update.getFloat("flat");
        updateMindBonusPerMind = update.getFloat("perMind");
        updateMindBonusMax = update.getFloat("max");

        NBTTagCompound attack = formMastery.getCompoundTag("attack");
        attackGain = attack.getFloat("gain");
        attackMultiDivPlus = attack.getFloat("multiDivPlus");
        attackMindBonusFlat = attack.getFloat("flat");
        attackMindBonusPerMind = attack.getFloat("perMind");
        attackMindBonusMax = attack.getFloat("max");

        NBTTagCompound damaged = formMastery.getCompoundTag("damaged");
        damagedGain = damaged.getFloat("gain");
        damagedMultiDivPlus = damaged.getFloat("multiDivPlus");
        damagedMindBonusFlat = damaged.getFloat("flat");
        damagedMindBonusPerMind = damaged.getFloat("perMind");
        damagedMindBonusMax = damaged.getFloat("max");

        NBTTagCompound fireKi = formMastery.getCompoundTag("fireKi");
        fireKiGain = fireKi.getFloat("gain");
        fireKiMultiDivPlus = fireKi.getFloat("multiDivPlus");
        fireKiMindBonusFlat = fireKi.getFloat("flat");
        fireKiMindBonusPerMind = fireKi.getFloat("perMind");
        fireKiMindBonusMax = fireKi.getFloat("max");

        masteryLink.loadFromNBT(formMastery);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound formMastery = new NBTTagCompound();

        formMastery.setFloat("maxLevel", maxLevel);
        formMastery.setFloat("instantTransformationUnlockLevel", instantTransformationUnlockLevel);
        formMastery.setFloat("kiDrain", kiDrain);
        formMastery.setInteger("kiDrainTimer", kiDrainTimer);
        formMastery.setFloat("healthRequirement", healthRequirement);
        formMastery.setFloat("dodgeChance", dodgeChance);
        formMastery.setFloat("damageNegation", damageNegation);
        formMastery.setInteger("maxHeat", maxHeat);
        formMastery.setInteger("painTime", painTime);
        formMastery.setFloat("movementSpeed", movementSpeed);
        formMastery.setFloat("tailCutChance", tailCutChance);

        NBTTagCompound attributeMulti = new NBTTagCompound();
        attributeMulti.setFloat("flat", attributeMultiFlat);
        attributeMulti.setFloat("perLevel", attributeMultiPerLevel);
        attributeMulti.setFloat("max", attributeMultiMinOrMax);
        formMastery.setTag("attributeMulti", attributeMulti);

        NBTTagCompound kiDrainMulti = new NBTTagCompound();
        kiDrainMulti.setFloat("flat", kiDrainMultiFlat);
        kiDrainMulti.setFloat("perLevel", kiDrainMultiPerLevel);
        kiDrainMulti.setFloat("minOrMax", kiDrainMultiMinOrMax);
        formMastery.setTag("kiDrainMulti", kiDrainMulti);

        NBTTagCompound heatMulti = new NBTTagCompound();
        heatMulti.setFloat("flat", heatMultiFlat);
        heatMulti.setFloat("perLevel", heatMultiPerLevel);
        heatMulti.setFloat("minOrMax", heatMultiMinOrMax);
        formMastery.setTag("heatMulti", heatMulti);

        NBTTagCompound painMulti = new NBTTagCompound();
        painMulti.setFloat("flat", painMultiFlat);
        painMulti.setFloat("perLevel", painMultiPerLevel);
        painMulti.setFloat("minOrMax", painMultiMinOrMax);
        formMastery.setTag("painMulti", painMulti);

        NBTTagCompound healthRequirementMulti = new NBTTagCompound();
        healthRequirementMulti.setFloat("flat", healthRequirementMultiFlat);
        healthRequirementMulti.setFloat("perLevel", healthRequirementMultiPerLevel);
        healthRequirementMulti.setFloat("minOrMax", healthRequirementMultiMinOrMax);
        formMastery.setTag("healthRequirementMulti", healthRequirementMulti);

        NBTTagCompound dodgeMulti = new NBTTagCompound();
        dodgeMulti.setFloat("flat", dodgeMultiFlat);
        dodgeMulti.setFloat("perLevel", dodgeMultiPerLevel);
        dodgeMulti.setFloat("minOrMax", dodgeMultiMinOrMax);
        formMastery.setTag("dodgeMulti", dodgeMulti);

        NBTTagCompound damageNegationMulti = new NBTTagCompound();
        damageNegationMulti.setFloat("flat", damageNegationMultiFlat);
        damageNegationMulti.setFloat("perLevel", damageNegationMultiPerLevel);
        damageNegationMulti.setFloat("minOrMax", damageNegationMultiMinOrMax);
        formMastery.setTag("damageNegationMulti", damageNegationMulti);

        NBTTagCompound movementSpeedMulti = new NBTTagCompound();
        movementSpeedMulti.setFloat("flat", movementSpeedMultiFlat);
        movementSpeedMulti.setFloat("perLevel", movementSpeedMultiPerLevel);
        movementSpeedMulti.setFloat("minOrMax", movementSpeedMultiMinOrMax);
        formMastery.setTag("movementSpeedMulti", movementSpeedMulti);

        NBTTagCompound tailCutChanceMulti = new NBTTagCompound();
        tailCutChanceMulti.setFloat("flat", tailCutChanceMultiFlat);
        tailCutChanceMulti.setFloat("perLevel", tailCutChanceMultiPerLevel);
        tailCutChanceMulti.setFloat("minOrMax", tailCutChanceMultiMinOrMax);
        formMastery.setTag("tailCutMulti", tailCutChanceMulti);

        NBTTagCompound racialBonuses = new NBTTagCompound();
            racialBonuses.setBoolean("powerPointEnabled", powerPointEnabled);
            racialBonuses.setInteger("powerPointCost", powerPointCost);
            racialBonuses.setInteger("powerPointGrowth", powerPointGrowth);

            racialBonuses.setFloat("powerPointMultiNormal", powerPointMultiNormal);
            racialBonuses.setFloat("powerPointMultiBasedOnPoints", powerPointMultiBasedOnPoints);

            racialBonuses.setBoolean("absorptionEnabled", absorptionEnabled);
            racialBonuses.setFloat("absorptionMulti", absorptionMulti);

            NBTTagCompound powerPointMastery = new NBTTagCompound();
                powerPointMastery.setFloat("flat", powerPointCostMultiFlat);
                powerPointMastery.setFloat("perLevel", powerPointCostPerLevel);
                powerPointMastery.setFloat("minOrMax", powerPointCostMinOrMax);
            racialBonuses.setTag("powerPointMastery", powerPointMastery);

        formMastery.setTag("racialBonuses", racialBonuses);

        NBTTagCompound destroyerConfigs = new NBTTagCompound();
            destroyerConfigs.setBoolean("enabled", destroyerEnabled);
            destroyerConfigs.setFloat("flat", destroyerKiDamageMultiFlat);
            destroyerConfigs.setFloat("perLevel", destroyerKiDamageMultiPerLevel);
            destroyerConfigs.setFloat("minOrMax", destroyerKiDamageMultiMinOrMax);
            destroyerConfigs.setFloat("damage", destroyerKiDamage);
        formMastery.setTag("destroyerConfigs", destroyerConfigs);

        NBTTagCompound update = new NBTTagCompound();
        update.setFloat("gain", updateGain);
        update.setFloat("multiDivPlus", updateMultiDivPlus);
        update.setFloat("flat", updateMindBonusFlat);
        update.setFloat("perMind", updateMindBonusPerMind);
        update.setFloat("max", updateMindBonusMax);
        formMastery.setTag("update", update);

        NBTTagCompound attack = new NBTTagCompound();
        attack.setFloat("gain", attackGain);
        attack.setFloat("multiDivPlus", attackMultiDivPlus);
        attack.setFloat("flat", attackMindBonusFlat);
        attack.setFloat("perMind", attackMindBonusPerMind);
        attack.setFloat("max", attackMindBonusMax);
        formMastery.setTag("attack", attack);

        NBTTagCompound damaged = new NBTTagCompound();
        damaged.setFloat("gain", damagedGain);
        damaged.setFloat("multiDivPlus", damagedMultiDivPlus);
        damaged.setFloat("flat", damagedMindBonusFlat);
        damaged.setFloat("perMind", damagedMindBonusPerMind);
        damaged.setFloat("max", damagedMindBonusMax);
        formMastery.setTag("damaged", damaged);

        NBTTagCompound fireKi = new NBTTagCompound();
        fireKi.setFloat("gain", fireKiGain);
        fireKi.setFloat("multiDivPlus", fireKiMultiDivPlus);
        fireKi.setFloat("flat", fireKiMindBonusFlat);
        fireKi.setFloat("perMind", fireKiMindBonusPerMind);
        fireKi.setFloat("max", fireKiMindBonusMax);
        formMastery.setTag("fireKi", fireKi);

        compound.setTag("formMastery", formMastery);

        masteryLink.saveToNBT(formMastery);
        return compound;
    }

    public IFormMastery save() {
        if (parent != null)
            parent.save();
        return this;
    }

    @Override
    public void setPowerPointCost(int cost) {
        if(cost < 0)
            cost = 0;
        this.powerPointCost = cost;
    }

    @Override
    public void setPowerPointGrowth(int growth) {
        if(growth < 0)
            growth = 0;
        this.powerPointGrowth = growth;
    }

    @Override
    public void setPowerPointMultiNormal(float multi) {
        if(multi < 0)
            multi = 0;
        this.powerPointMultiNormal = multi;
    }

    @Override
    public void setPowerPointMultiBasedOnPoints(float multi) {
        if(multi < 0)
            multi = 0;
        this.powerPointMultiBasedOnPoints = multi;
    }

    @Override
    public void setAbsorptionMulti(float multi) {
        if(multi < 0)
            multi = 0;
        this.absorptionMulti = multi;
    }

    @Override
    public int getPowerPointCost() {
        return this.powerPointCost;
    }

    @Override
    public int getPowerPointGrowth() {
        return this.powerPointGrowth;
    }

    @Override
    public float getPowerPointMultiNormal() {
        return this.powerPointMultiNormal;
    }

    @Override
    public float getPowerPointMultiBasedOnPoints() {
        return this.powerPointMultiBasedOnPoints;
    }

    public float getAbsorptionMulti() {
        return this.absorptionMulti;
    }

    @Override
    public void setDestroyerOn(boolean isOn) {
        this.destroyerEnabled = isOn;
    }

    @Override
    public boolean isDestroyerOn() {
        return this.destroyerEnabled;
    }

    @Override
    public void setDestroyerEnergyDamage(float energyDamage) {
        this.destroyerKiDamage = Math.max(energyDamage, 0);
    }

    @Override
    public float getDestroyerEnergyDamage() {
        return this.destroyerKiDamage;
    }

    @Override
    public boolean isAbsorptionBoostEnabled() {
        return this.absorptionEnabled;
    }

    @Override
    public boolean isPowerPointBoostEnabled() {
        return this.powerPointEnabled;
    }


    @Override
    public IFormMasteryLinkData getMasteryLinks() {
        return masteryLink;
    }
}
