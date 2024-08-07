package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.form.IFormStackable;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.controllers.FormController;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;

public class FormStackable implements IFormStackable {

    private final Form parent;

    public boolean vanillaStackable = false;
    public boolean kaiokenStackable = true, uiStackable = true, godStackable = true, mysticStackable = true;
    public float kaiokenStrength = 1.0f, uiStrength = 1.0f, godStrength = 1.0f, mysticStrength = 1.0f, legendaryStrength = 1.0f, divineStrength = 1.0f, majinStrength = 1.0f;
    public float kaiokenState2Factor = 1.0f, uiState2Factor = 1.0f;

    public boolean useLegendaryConfig, useDivineConfig, useMajinConfig;
    public int legendaryID = -1, divineID = -1, majinID = -1;

    public float kaiokenDrainMulti = 1;
    public float[] kaiokenBalanceValue = new float[6];
    public float[] kaiokenStrainedBalanceValue = new float[6];
    public boolean kaiokenMultipliesCurrentFormDrain;

    public float powerPointMulti = 1.0f;
    public int powerPointCost;
    public float absorptionMulti = 1.0f;
    public boolean racialBonusesOn;

    public FormStackable(Form parent) {
        this.parent = parent;
        Arrays.fill(kaiokenBalanceValue, 1);
        Arrays.fill(kaiokenStrainedBalanceValue, 1);
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound stack = compound.getCompoundTag("stackableForms");
        vanillaStackable = stack.getBoolean("vanillaStackable");
        kaiokenStrength = stack.getFloat("kaiokenStrength");
        kaiokenStackable = stack.getBoolean("kaiokenStackable");
        kaiokenState2Factor = stack.getFloat("kaiokenState2Factor");
        uiStrength = stack.getFloat("uiStrength");
        uiStackable = stack.getBoolean("uiStackable");
        uiState2Factor = stack.getFloat("uiState2Factor");
        godStrength = stack.getFloat("godStrength");
        godStackable = stack.getBoolean("godStackable");
        mysticStrength = stack.getFloat("mysticStrength");
        mysticStackable = stack.getBoolean("mysticStackable");

        legendaryStrength = !stack.hasKey("legendaryStrength") ? 1 : stack.getFloat("legendaryStrength");
        divineStrength = !stack.hasKey("divineStrength") ? 1 : stack.getFloat("divineStrength");
        majinStrength = !stack.hasKey("majinStrength") ? 1 : stack.getFloat("majinStrength");

        useLegendaryConfig = !stack.hasKey("useLegendaryConfig") ? true : stack.getBoolean("useLegendaryConfig");
        useDivineConfig = !stack.hasKey("useDivineConfig") ? true : stack.getBoolean("useDivineConfig");
        useMajinConfig = !stack.hasKey("useMajinConfig") ? true : stack.getBoolean("useMajinConfig");

        racialBonusesOn = stack.getBoolean("racialBonusesOn");
        powerPointCost = !stack.hasKey("powerPointCost") ? 100 : stack.getInteger("powerPointCost");
        powerPointMulti = !stack.hasKey("powerPointMulti") ? 1f : stack.getInteger("powerPointMulti");
        absorptionMulti = !stack.hasKey("absorptionMulti") ? 1f : stack.getInteger("absorptionMulti");

        legendaryID = !stack.hasKey("legendaryID") ? -1 : stack.getInteger("legendaryID");
        divineID = !stack.hasKey("divineID") ? -1 : stack.getInteger("divineID");
        majinID = !stack.hasKey("majinID") ? -1 : stack.getInteger("majinID");

        if(stack.hasKey("kaiokenDrainData")){
            NBTTagCompound kaioDrain = stack.getCompoundTag("kaiokenDrainData");
            kaiokenDrainMulti = kaioDrain.getFloat("multi");

            NBTTagCompound balanceNormal = kaioDrain.getCompoundTag("balanceNormal");
            for(int i = 0; i < 6; i++){
                kaiokenBalanceValue[i] = balanceNormal.getFloat(i+"");
            }
            NBTTagCompound balanceStrained = kaioDrain.getCompoundTag("balanceStrained");
            for(int i = 0; i < 6; i++){
                kaiokenStrainedBalanceValue[i] = balanceStrained.getFloat(i+"");
            }
            kaiokenMultipliesCurrentFormDrain = kaioDrain.getBoolean("multipliesCurrentFormDrain");
        }else{
            kaiokenMultipliesCurrentFormDrain = true;
        }

    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound stack = new NBTTagCompound();
        stack.setBoolean("vanillaStackable", vanillaStackable);
        stack.setFloat("kaiokenStrength", kaiokenStrength);
        stack.setBoolean("kaiokenStackable", kaiokenStackable);
        stack.setFloat("kaiokenState2Factor", kaiokenState2Factor);
        stack.setFloat("uiStrength", uiStrength);
        stack.setBoolean("uiStackable", uiStackable);
        stack.setFloat("uiState2Factor", uiState2Factor);
        stack.setFloat("godStrength", godStrength);
        stack.setBoolean("godStackable", godStackable);
        stack.setFloat("mysticStrength", mysticStrength);
        stack.setBoolean("mysticStackable", mysticStackable);

        stack.setFloat("legendaryStrength", legendaryStrength);
        stack.setFloat("divineStrength", divineStrength);
        stack.setFloat("majinStrength", majinStrength);

        stack.setBoolean("useLegendaryConfig", useLegendaryConfig);
        stack.setBoolean("useDivineConfig", useDivineConfig);
        stack.setBoolean("useMajinConfig", useMajinConfig);

        stack.setInteger("legendaryID", legendaryID);
        stack.setInteger("divineID", divineID);
        stack.setInteger("majinID", majinID);

        NBTTagCompound kaioDrainData = new NBTTagCompound();
        kaioDrainData.setFloat("multi", kaiokenDrainMulti);
        NBTTagCompound kaioBalanceNormal = new NBTTagCompound();
        for(int i = 0; i < 6; i++){
            kaioBalanceNormal.setFloat(i+"", kaiokenBalanceValue[i]);
        }
        NBTTagCompound kaioBalanceStrained = new NBTTagCompound();
        for(int i = 0; i < 6; i++){
            kaioBalanceStrained.setFloat(i+"", kaiokenStrainedBalanceValue[i]);
        }

        kaioDrainData.setTag("balanceNormal", kaioBalanceNormal);
        kaioDrainData.setTag("balanceStrained", kaioBalanceStrained);
        kaioDrainData.setBoolean("multipliesCurrentFormDrain", kaiokenMultipliesCurrentFormDrain);
        stack.setTag("kaiokenDrainData", kaioDrainData);

        stack.setFloat("powerPointMulti", powerPointMulti);
        stack.setInteger("powerPointCost", powerPointCost);
        stack.setFloat("absorptionMulti", absorptionMulti);
        stack.setBoolean("racialBonusesOn", racialBonusesOn);

        compound.setTag("stackableForms", stack);
        return compound;
    }

    @Override
    public void setLegendaryForm(IForm form) {
        if (form == null)
            return;

        int id = form.getID();
        if (form.getID() == this.legendaryID)
            return;

        if (id > -1) {
            legendaryID = id;
        }
    }

    @Override
    public int getLegendaryFormID() {
        return legendaryID;
    }

    @Override
    public IForm getLegendaryForm() {
        return FormController.Instance.get(legendaryID);
    }


    @Override
    public void setDivineForm(IForm form) {
        if (form == null)
            return;

        int id = form.getID();
        if (form.getID() == this.divineID)
            return;

        if (id > -1) {
            divineID = id;
        }
    }

    @Override
    public int getDivineFormID() {
        return divineID;
    }

    @Override
    public IForm getDivineForm() {
        return FormController.Instance.get(divineID);
    }

    @Override
    public void setMajinForm(IForm form) {
        if (form == null)
            return;

        int id = form.getID();
        if (form.getID() == this.majinID)
            return;

        if (id > -1) {
            majinID = id;
        }
    }

    @Override
    public int getMajinFormID() {
        return majinID;
    }

    @Override
    public IForm getMajinForm() {
        return FormController.Instance.get(majinID);
    }

    @Override
    public void useConfigMulti(int DBCNonRacialFormID, boolean useConfig) {
        switch (DBCNonRacialFormID) {
            case DBCForm.Legendary:
                useLegendaryConfig = useConfig;
                break;
            case DBCForm.Divine:
                useDivineConfig = useConfig;
                break;
            case DBCForm.Majin:
                useMajinConfig = useConfig;
                break;
        }
    }

    @Override
    public boolean useConfigMulti(int DBCNonRacialFormID) {
        switch (DBCNonRacialFormID) {
            case DBCForm.Legendary:
                return useLegendaryConfig;
            case DBCForm.Divine:
                return useDivineConfig;
            case DBCForm.Majin:
                return useMajinConfig;
            default:
                return false;
        }
    }

    @Override
    public boolean isFormStackable(int DBCNonRacialFormID) {
        switch (DBCNonRacialFormID) {
            case DBCForm.Kaioken:
                return kaiokenStackable;
            case DBCForm.UltraInstinct:
                return uiStackable;
            case DBCForm.GodOfDestruction:
                return godStackable;
            case DBCForm.Mystic:
                return mysticStackable;
            default:
                return false;
        }
    }

    @Override
    public boolean isVanillaStackable() {
        return vanillaStackable;
    }

    @Override
    public void setVanillaStackable(boolean vanillaStackable) {
        this.vanillaStackable = vanillaStackable;
    }

    @Override
    public void allowStackForm(int DBCNonRacialFormID, boolean stackForm) {
        switch (DBCNonRacialFormID) {
            case DBCForm.Kaioken:
                kaiokenStackable = stackForm;
                break;
            case DBCForm.UltraInstinct:
                uiStackable = stackForm;
                break;
            case DBCForm.GodOfDestruction:
                godStackable = stackForm;
                break;
            case DBCForm.Mystic:
                mysticStackable = stackForm;
                break;
        }
    }

    @Override
    public void setFormMulti(int dbcForm, float multi) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                kaiokenStrength = multi;
                break;
            case DBCForm.UltraInstinct:
                uiStrength = multi;
                break;
            case DBCForm.GodOfDestruction:
                godStrength = multi;
                break;
            case DBCForm.Mystic:
                mysticStrength = multi;
                break;
            case DBCForm.Legendary:
                legendaryStrength = multi;
                break;
            case DBCForm.Divine:
                divineStrength = multi;
                break;
            case DBCForm.Majin:
                majinStrength = multi;
                break;
        }
    }

    @Override
    public float getFormMulti(int dbcForm) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                return kaiokenStrength;
            case DBCForm.UltraInstinct:
                return uiStrength;
            case DBCForm.GodOfDestruction:
                return godStrength;
            case DBCForm.Mystic:
                return mysticStrength;
            case DBCForm.Legendary:
                return legendaryStrength;
            case DBCForm.Divine:
                return divineStrength;
            case DBCForm.Majin:
                return majinStrength;
            default:
                return 1.0f;
        }
    }

    @Override
    public void setState2Factor(int dbcForm, float factor) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                kaiokenState2Factor = factor;
                break;
            case DBCForm.UltraInstinct:
                uiState2Factor = factor;
                break;
        }
    }

    @Override
    public float getState2Factor(int dbcForm) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                return kaiokenState2Factor;
            case DBCForm.UltraInstinct:
                return uiState2Factor;
            default:
                return 1.0f;
        }
    }

    public FormStackable save() {
        if (parent != null)
            parent.save();
        return this;
    }

    @Override
    public float getKaioDrain() {
        return kaiokenDrainMulti;
    }

    @Override
    public void setKaioDrain(float drain){
        this.kaiokenDrainMulti = drain;
    }

    @Override
    public float getKaioState2Balance(int state2, boolean strained) {
        if(state2 < 0)
            state2 = 0;
        if(state2 > 5)
            state2 = 5;

        return (strained ? kaiokenStrainedBalanceValue[state2] : kaiokenBalanceValue[state2]);
    }

    @Override
    public void setKaioState2Balance(int state2, boolean strained, float value){
        if(state2 < 0)
            state2 = 0;
        if(state2 > 5)
            state2 = 5;

        float[] array = strained ? kaiokenStrainedBalanceValue : kaiokenBalanceValue;
        array[state2] = value;
    }

    @Override
    public void setRacialBonusesOn(boolean racialBonusesOn) {
        this.racialBonusesOn = racialBonusesOn;
    }

    @Override
    public boolean getRacialBonusesOn() {
        return this.racialBonusesOn;
    }

    @Override
    public void setPowerPointCost(int cost) {
        this.powerPointCost = cost;
    }

    @Override
    public void setPowerPointMulti(float multi) {
        this.powerPointMulti = multi;
    }

    @Override
    public void setAbsorptionMulti(float multi) {
        this.absorptionMulti = multi;
    }

    @Override
    public int getPowerPointCost() {
        return this.powerPointCost;
    }

    @Override
    public float getPowerPointMulti() {
        return this.powerPointMulti;
    }

    @Override
    public float getAbsorptionMulti() {
        return this.absorptionMulti;
    }

    @Override
    public boolean isAbsorptionBoostEnabled() {
        return this.absorptionMulti != 1;
    }

    @Override
    public boolean isPowerPointBoostEnabled() {
        return this.powerPointMulti != 1;
    }
}
