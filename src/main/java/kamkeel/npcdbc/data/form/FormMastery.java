package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.api.form.IFormMastery;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.util.ValueUtil;
import scala.tools.nsc.doc.model.Val;

public class FormMastery implements IFormMastery {

    private final Form parent;

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

    public int kiDrain = 1;
    public int kiDrainTimer = 20;
    public float kiDrainMultiFlat = 1.0f, kiDrainMultiPerLevel = -0.01f, kiDrainMultiMinOrMax = 0.1f;

    public float strainMultiFlat = 1.0f, strainMultiPerLevel = -0.01f, strainMultiMinOrMax = 0.1f;


    public FormMastery(Form parent) {
        this.parent = parent;
    }

    @Override
    public int getKiDrain() {
        return kiDrain;
    }

    @Override
    public void setKiDrain(int kiDrain) {
        this.kiDrain = kiDrain;
    }

    @Override
    public boolean hasKiDrain() {
        return kiDrain > 0;
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
            case "strain":
                switch (type1.toLowerCase()) {
                    case "flat":
                        return strainMultiFlat;
                    case "perlevel":
                        return strainMultiPerLevel;
                    case "minormax":
                        return strainMultiMinOrMax;
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

        }
        return 1.0f;
    }

    public void setMulti(String type, String type1, float value) {
        switch (type.toLowerCase()) {
            case "attribute":
                switch (type1.toLowerCase()) {
                    case "flat":
                        attributeMultiFlat = value;
                    case "perlevel":
                        attributeMultiPerLevel = value;
                    case "minormax":
                        attributeMultiMinOrMax = value;
                }
            case "kidrain":
                switch (type1.toLowerCase()) {
                    case "flat":
                        kiDrainMultiFlat = value;
                    case "perlevel":
                        kiDrainMultiPerLevel = value;
                    case "minormax":
                        kiDrainMultiMinOrMax = value;
                }
            case "strain":
                switch (type1.toLowerCase()) {
                    case "flat":
                        strainMultiFlat = value;
                    case "perlevel":
                        strainMultiPerLevel = value;
                    case "minormax":
                        strainMultiMinOrMax = value;
                }
            case "healthrequirement":
                switch (type1.toLowerCase()) {
                    case "flat":
                        healthRequirementMultiFlat = value;
                    case "perlevel":
                        healthRequirementMultiPerLevel = value;
                    case "minormax":
                        healthRequirementMultiMinOrMax = value;
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
        kiDrain = formMastery.getInteger("kiDrain");
        kiDrainTimer = formMastery.getInteger("kiDrainTimer");
        healthRequirement = formMastery.getFloat("healthRequirement");

        NBTTagCompound attributeMulti = formMastery.getCompoundTag("attributeMulti");
        attributeMultiFlat = attributeMulti.getFloat("flat");
        attributeMultiPerLevel = attributeMulti.getFloat("perLevel");
        attributeMultiMinOrMax = attributeMulti.getFloat("max");

        NBTTagCompound kiDrainMulti = formMastery.getCompoundTag("kiDrainMulti");
        kiDrainMultiFlat = kiDrainMulti.getFloat("flat");
        kiDrainMultiPerLevel = kiDrainMulti.getFloat("perLevel");
        kiDrainMultiMinOrMax = kiDrainMulti.getFloat("minOrMax");

        NBTTagCompound strainMulti = formMastery.getCompoundTag("strainMulti");
        strainMultiFlat = strainMulti.getFloat("flat");
        strainMultiPerLevel = strainMulti.getFloat("perLevel");
        strainMultiMinOrMax = strainMulti.getFloat("minOrMax");

        NBTTagCompound healthRequirementMulti = formMastery.getCompoundTag("healthRequirementMulti");
        healthRequirementMultiFlat = healthRequirementMulti.getFloat("flat");
        healthRequirementMultiPerLevel = healthRequirementMulti.getFloat("perLevel");
        healthRequirementMultiMinOrMax = healthRequirementMulti.getFloat("minOrMax");

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
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound formMastery = new NBTTagCompound();
        formMastery.setFloat("maxLevel", maxLevel);
        formMastery.setFloat("instantTransformationUnlockLevel", instantTransformationUnlockLevel);
        formMastery.setInteger("kiDrain", kiDrain);
        formMastery.setInteger("kiDrainTimer", kiDrainTimer);
        formMastery.setFloat("healthRequirement", healthRequirement);

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

        NBTTagCompound strainMulti = new NBTTagCompound();
        strainMulti.setFloat("flat", strainMultiFlat);
        strainMulti.setFloat("perLevel", strainMultiPerLevel);
        strainMulti.setFloat("minOrMax", strainMultiMinOrMax);
        formMastery.setTag("strainMulti", strainMulti);

        NBTTagCompound healthRequirementMulti = new NBTTagCompound();
        healthRequirementMulti.setFloat("flat", healthRequirementMultiFlat);
        healthRequirementMulti.setFloat("perLevel", healthRequirementMultiPerLevel);
        healthRequirementMulti.setFloat("minOrMax", healthRequirementMultiMinOrMax);
        formMastery.setTag("healthRequirementMulti", healthRequirementMulti);

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
        return compound;
    }

    public IFormMastery save() {
        if (parent != null)
            parent.save();
        return this;
    }
}
