package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.form.IFormKaiokenStackables;
import kamkeel.npcdbc.api.form.IFormStackable;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.controllers.FormController;
import net.minecraft.nbt.NBTTagCompound;

public class FormStackable implements IFormStackable {

    private final Form parent;

    public boolean vanillaStackable = false;
    public boolean kaiokenStackable = true, uiStackable = true, godStackable = true, mysticStackable = true;
    public float uiStrength = 1.0f, godStrength = 1.0f, mysticStrength = 1.0f, legendaryStrength = 1.0f, divineStrength = 1.0f, majinStrength = 1.0f;
    public float uiState2Factor = 1.0f;

    public boolean useLegendaryConfig, useDivineConfig, useMajinConfig;
    public int legendaryID = -1, divineID = -1, majinID = -1, fusionID = -1;

    public FormKaiokenStackableData kaiokenData;

    public FormStackable(Form parent) {
        this.parent = parent;
        this.kaiokenData = new FormKaiokenStackableData(parent, this);
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound stack = compound.getCompoundTag("stackableForms");
        vanillaStackable = stack.getBoolean("vanillaStackable");
        kaiokenStackable = stack.getBoolean("kaiokenStackable");
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

        legendaryID = !stack.hasKey("legendaryID") ? -1 : stack.getInteger("legendaryID");
        divineID = !stack.hasKey("divineID") ? -1 : stack.getInteger("divineID");
        majinID = !stack.hasKey("majinID") ? -1 : stack.getInteger("majinID");
        fusionID = !stack.hasKey("fusionID") ? -1 : stack.getInteger("fusionID");

        kaiokenData.readFromNBT(stack);

    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound stack = new NBTTagCompound();
        stack.setBoolean("vanillaStackable", vanillaStackable);
        stack.setBoolean("kaiokenStackable", kaiokenStackable);
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
        stack.setInteger("fusionID", fusionID);

        kaiokenData.saveToNBT(stack);

        compound.setTag("stackableForms", stack);
        return compound;
    }

    @Override
    public void setFusionForm(IForm form) {
        if (form == null) {
            fusionID = -1;
            return;
        }

        int id = form.getID();
        if (form.getID() == this.fusionID)
            return;

        if (id > -1) {
            fusionID = id;
        }
    }

    @Override
    public int getFusionFormID() {
        return fusionID;
    }

    @Override
    public IForm getFusionForm() {
        return FormController.Instance.get(fusionID);
    }

    @Override
    public void setLegendaryForm(IForm form) {
        if (form == null) {
            legendaryID = -1;
            return;
        }

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
        if (form == null) {
            divineID = -1;
            return;
        }

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
        if (form == null) {
            majinID = -1;
            return;
        }

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
    public IFormKaiokenStackables getKaiokenConfigs() {
        return this.kaiokenData;
    }

    @Override
    public void setFormMulti(int dbcForm, float multi) {
        switch (dbcForm) {
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
            case DBCForm.UltraInstinct:
                uiState2Factor = factor;
                break;
        }
    }

    @Override
    public float getState2Factor(int dbcForm) {
        switch (dbcForm) {
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
}
