package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.ChainedAbility;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.util.ValueUtil;

public class AbilityFormData {
    public static final String NBT_KEY = "AbilityForm";

    private final NBTTagCompound customData;

    // Form properties
    public int formID = -1;
    public int transformTick = 0;
    public int detransformTick = 0;
    public boolean keepTransformed = true;
    public boolean needsFormUnlocked = true;
    public boolean activateTurbo = true;

    public boolean kaioken = false;
    public boolean keepKaioken = false;
    public int kaiokenStage = 1;
    public int activateKaiokenTick = 0;
    public int deactivateKaiokenTick = 0;

    private AbilityFormData(NBTTagCompound customData) {
        this.customData = customData;
    }

    /**
     * Create an AbilityIconData instance from a raw customData compound.
     * Reads existing values if present, otherwise uses defaults.
     */
    public static AbilityFormData fromCustomData(NBTTagCompound customData) {
        AbilityFormData formData = new AbilityFormData(customData);
        if (customData.hasKey(NBT_KEY)) {
            formData.readFromNBT(customData.getCompoundTag(NBT_KEY));
        }
        return formData;
    }

    /**
     * Create an AbilityIconData instance from an ability's customData.
     */
    public static AbilityFormData fromAbility(Ability ability) {
        return fromCustomData(ability.getCustomData());
    }

    /**
     * Create an AbilityIconData instance from a chained ability's customData.
     */
    public static AbilityFormData fromChainedAbility(ChainedAbility chain) {
        return fromCustomData(chain.getCustomData());
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
        nbt.setInteger("formID", formID);
        nbt.setInteger("transformTick", transformTick);
        nbt.setInteger("detransformTick", detransformTick);
        nbt.setBoolean("keepTransformed", keepTransformed);
        nbt.setBoolean("needsFormUnlocked", needsFormUnlocked);
        nbt.setBoolean("activateTurbo", activateTurbo);

        nbt.setBoolean("kaioken", kaioken);
        nbt.setBoolean("keepKaioken", keepKaioken);
        nbt.setInteger("kaiokenStage", kaiokenStage);
        nbt.setInteger("activateKaiokenTick", activateKaiokenTick);
        nbt.setInteger("deactivateKaiokenTick", deactivateKaiokenTick);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.formID = nbt.getInteger("formID");
        this.transformTick = nbt.getInteger("transformTick");
        this.detransformTick = nbt.getInteger("detransformTick");
        this.keepTransformed = nbt.getBoolean("keepTransformed");
        this.needsFormUnlocked = nbt.getBoolean("needsFormUnlocked");
        this.activateTurbo = nbt.getBoolean("activateTurbo");

        this.kaioken = nbt.getBoolean("kaioken");
        this.keepKaioken = nbt.getBoolean("keepKaioken");
        this.kaiokenStage = nbt.getInteger("kaiokenStage");
        this.activateKaiokenTick = nbt.getInteger("activateKaiokenTick");
        this.deactivateKaiokenTick = nbt.getInteger("deactivateKaiokenTick");
    }

    public int getFormID() {
        return formID;
    }

    public void setFormID(int formID) {
        if (!FormController.Instance.has(formID))
            return;

        this.formID = formID;
        save();
    }

    public int getTransformTick() {
        return transformTick;
    }

    public void setTransformTick(int transformTick) {
        this.transformTick = Math.max(0, transformTick);
        save();
    }

    public int getDetransformTick() {
        return detransformTick;
    }

    public void setDetransformTick(int detransformTick) {
        this.detransformTick = ValueUtil.clamp(detransformTick, 0, getTransformTick());
        save();
    }

    public boolean isKeepTransformed() {
        return keepTransformed;
    }

    public void setKeepTransformed(boolean keepTransformed) {
        this.keepTransformed = keepTransformed;
        save();
    }

    public boolean isNeedsFormUnlocked() {
        return needsFormUnlocked;
    }

    public void setNeedsFormUnlocked(boolean needsFormUnlocked) {
        this.needsFormUnlocked = needsFormUnlocked;
        save();
    }

    public boolean isActivateTurbo() {
        return activateTurbo;
    }

    public void setActivateTurbo(boolean activateTurbo) {
        this.activateTurbo = activateTurbo;
        save();
    }

    public boolean isKaioken() {
        return kaioken;
    }

    public void setKaioken(boolean kaioken) {
        this.kaioken = kaioken;
        save();
    }

    public boolean isKeepKaioken() {
        return keepKaioken;
    }

    public void setKeepKaioken(boolean keepKaioken) {
        this.keepKaioken = keepKaioken;
        save();
    }

    public int getKaiokenStage() {
        return kaiokenStage;
    }

    public void setKaiokenStage(int kaiokenStage) {
        this.kaiokenStage = ValueUtil.clamp(kaiokenStage, 1, 6);
        save();
    }

    public int getActivateKaiokenTick() {
        return activateKaiokenTick;
    }

    public void setActivateKaiokenTick(int activateKaiokenTick) {
        this.activateKaiokenTick = activateKaiokenTick;
        save();
    }

    public int getDeactivateKaiokenTick() {
        return deactivateKaiokenTick;
    }

    public void setDeactivateKaiokenTick(int deactivateKaiokenTick) {
        this.deactivateKaiokenTick = ValueUtil.clamp(deactivateKaiokenTick, 0, getActivateKaiokenTick());
        save();
    }
}
