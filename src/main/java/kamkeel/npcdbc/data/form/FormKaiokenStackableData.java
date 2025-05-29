package kamkeel.npcdbc.data.form;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.api.form.IFormKaiokenStackables;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;

public class FormKaiokenStackableData implements IFormKaiokenStackables {

    public float kaiokenDrainMulti = 1;
    public float[] kaiokenBalanceValue = new float[6];
    public float[] kaiokenStrainedBalanceValue = new float[6];

    public boolean kaiokenMultipliesCurrentFormDrain;

    // Multipliers
    public boolean isUsingGlobalAttributeMultis;
    public float attributeMultiScalar = 1.0f;
    public float[] kaiokenMultis;

    private final Form parent;
    private final FormStackable stackableParent;

    public FormKaiokenStackableData(Form parent, FormStackable formStackable) {
        this.parent = parent;
        this.stackableParent = formStackable;

        Arrays.fill(kaiokenBalanceValue, 1);
        Arrays.fill(kaiokenStrainedBalanceValue, 1);
        this.kaiokenMultis = Arrays.copyOfRange(JRMCoreH.TransKaiDmg, 1, JRMCoreH.TransKaiDmg.length);
    }

    public void readFromNBT(NBTTagCompound stack) {
        if (stack.hasKey("kaiokenDrainData")) {
            NBTTagCompound kaioDrain = stack.getCompoundTag("kaiokenDrainData");
            kaiokenDrainMulti = kaioDrain.getFloat("multi");

            NBTTagCompound balanceNormal = kaioDrain.getCompoundTag("balanceNormal");
            for (int i = 0; i < 6; i++) {
                kaiokenBalanceValue[i] = balanceNormal.getFloat(i + "");
            }
            NBTTagCompound balanceStrained = kaioDrain.getCompoundTag("balanceStrained");
            for (int i = 0; i < 6; i++) {
                kaiokenStrainedBalanceValue[i] = balanceStrained.getFloat(i + "");
            }
            kaiokenMultipliesCurrentFormDrain = kaioDrain.getBoolean("multipliesCurrentFormDrain");

        } else {
            kaiokenMultipliesCurrentFormDrain = true;
        }
        if (stack.hasKey("kaiokenMultiData")) {
            NBTTagCompound kaioMulti = stack.getCompoundTag("kaiokenMultiData");

            isUsingGlobalAttributeMultis = kaioMulti.getBoolean("isUsingGlobal");
            attributeMultiScalar = kaioMulti.getFloat("attributeScalar");
            NBTTagCompound multis = kaioMulti.getCompoundTag("multis");
            for (int i = 0; i < 6; i++) {
                kaiokenMultis[i] = multis.getFloat(i + "");
            }
        } else {
            isUsingGlobalAttributeMultis = true;
            attributeMultiScalar = 1.0f;
        }
    }

    public void saveToNBT(NBTTagCompound stack) {
        NBTTagCompound kaioDrainData = new NBTTagCompound();
        kaioDrainData.setFloat("multi", kaiokenDrainMulti);
        NBTTagCompound kaioBalanceNormal = new NBTTagCompound();
        for (int i = 0; i < 6; i++) {
            kaioBalanceNormal.setFloat(i + "", kaiokenBalanceValue[i]);
        }
        NBTTagCompound kaioBalanceStrained = new NBTTagCompound();
        for (int i = 0; i < 6; i++) {
            kaioBalanceStrained.setFloat(i + "", kaiokenStrainedBalanceValue[i]);
        }

        kaioDrainData.setTag("balanceNormal", kaioBalanceNormal);
        kaioDrainData.setTag("balanceStrained", kaioBalanceStrained);
        kaioDrainData.setBoolean("multipliesCurrentFormDrain", kaiokenMultipliesCurrentFormDrain);
        stack.setTag("kaiokenDrainData", kaioDrainData);

        NBTTagCompound kaioMulti = new NBTTagCompound();
        kaioMulti.setBoolean("isUsingGlobal", isUsingGlobalAttributeMultis);
        kaioMulti.setFloat("attributeScalar", attributeMultiScalar);

        NBTTagCompound multiData = new NBTTagCompound();
        for (int i = 0; i < 6; i++) {
            multiData.setFloat(i + "", kaiokenMultis[i]);
        }
        kaioMulti.setTag("multis", multiData);
        stack.setTag("kaiokenMultiData", kaioMulti);


    }

    @Override
    public void setKaioDrain(float drain) {
        this.kaiokenDrainMulti = drain;
    }

    @Override
    public float getKaioDrain() {
        return this.kaiokenDrainMulti;
    }

    @Override
    public void setMultiplyingCurrentFormDrain(boolean isOn) {
        this.kaiokenMultipliesCurrentFormDrain = isOn;
    }

    @Override
    public boolean isMultiplyingCurrentFormDrain() {
        return this.kaiokenMultipliesCurrentFormDrain;
    }

    @Override
    public void setKaioState2Balance(int state2, boolean strained, float value) {
        if (state2 < 0)
            state2 = 0;
        if (state2 > 5)
            state2 = 5;

        if (strained)
            this.kaiokenStrainedBalanceValue[state2] = value;
        else
            this.kaiokenBalanceValue[state2] = value;
    }

    @Override
    public float getKaioState2Balance(int state2, boolean strained) {
        if (state2 < 0)
            state2 = 0;
        if (state2 > 5)
            state2 = 5;

        return (strained ? kaiokenStrainedBalanceValue[state2] : kaiokenBalanceValue[state2]);
    }

    @Override
    public float getKaiokenAttributeMulti(int state2) {
        if (state2 < 0)
            return 1;
        if (state2 >= kaiokenMultis.length)
            return 1;

        return kaiokenMultis[state2];
    }

    @Override
    public float getKaiokenMultiScalar() {
        return attributeMultiScalar;
    }

    @Override
    public void setKaiokenAttributeMulti(int state2, float multi) {
        if (state2 < 0)
            return;
        if (state2 >= kaiokenMultis.length)
            return;
        kaiokenMultis[state2] = multi;
    }

    @Override
    public void setKaiokenMultiScalar(float scalar) {
        if (scalar < 0)
            scalar = 0;
        this.attributeMultiScalar = scalar;
    }

    @Override
    public boolean isUsingGlobalAttributeMultis() {
        return isUsingGlobalAttributeMultis;
    }

    @Override
    public void setUsingGlobalAttributeMultis(boolean isUsing) {
        this.isUsingGlobalAttributeMultis = isUsing;
    }

    /**
     * @param state2 0 - Lowest kaioken state (Ex. Kaioken x2), 5 - highest kaioken state
     */
    public float getCurrentFormMulti(int state2) {
        if (state2 < 0)
            return 1;
        if (state2 >= kaiokenMultis.length)
            return 1;

        if (isUsingGlobalAttributeMultis) {
            return JRMCoreH.TransKaiDmg[state2 + 1] * attributeMultiScalar;
        } else {
            return kaiokenMultis[state2] * attributeMultiScalar;
        }
    }
}
