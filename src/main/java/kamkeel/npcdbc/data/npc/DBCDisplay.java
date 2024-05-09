package kamkeel.npcdbc.data.npc;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.npc.IDBCDisplay;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

public class DBCDisplay implements IDBCDisplay {

    public boolean enabled = false;

    // Hair Display //
    public String hairCode = "", hairType = "";
    public int hairColor = -1;



    // Race Display //
    public int race = -1;
    public boolean useSkin = false;
    public int bodyType = 0;
    public int bodyCM = 0xffffff, bodyC1 = 0xffffff, bodyC2 = 0xffffff, bodyC3 = 0xffffff;
    public boolean hasArcoMask = false;

    // Face Display //
    public int eyeColor = -1;
    public int noseType = 1, mouthType = 1, eyeType = 0, arcoState;

    // Aura Display //
    public boolean auraOn = false;
    public int auraID = -1;

    // Form Display //
    public int formID = -1, selectedForm = -1, rage;
    public float formLevel = 0;
    public boolean isTransforming;
    private EnumAuraTypes enumAuraTypes = EnumAuraTypes.None;

    // Server Side Usage
    public float rageValue;

    public NBTTagCompound writeToNBT(NBTTagCompound comp) {
        comp.setBoolean("DBCDisplayEnabled", enabled);
        if (enabled) {
            comp.setInteger("DBCBodyType", bodyType);
            comp.setString("DBCHairType", hairType);
            comp.setString("DBCHair", hairCode);

            comp.setInteger("DBCHairColor", hairColor);
            comp.setInteger("DBCEyeColor", eyeColor);

            comp.setInteger("DBCRace", race);
            comp.setBoolean("DBCUseSkin", useSkin);
            comp.setInteger("DBCBodyCM", bodyCM);
            comp.setInteger("DBCBodyC1", bodyC1);
            comp.setInteger("DBCBodyC2", bodyC2);
            comp.setInteger("DBCBodyC3", bodyC3);
            comp.setInteger("DBCArcoState", arcoState);

            comp.setBoolean("DBCArcoMask", hasArcoMask);

            comp.setInteger("DBCRage", rage);
            comp.setBoolean("DBCIsTransforming", isTransforming);
            comp.setInteger("DBCFormID", formID);
            comp.setFloat("DBCFormLevel", formLevel);
            comp.setInteger("DBCSelectedForm", selectedForm);

            comp.setInteger("DBCAuraID", auraID);
            comp.setBoolean("DBCAuraOn", auraOn);
            comp.setInteger("DBCDisplayAura", enumAuraTypes.ordinal());
        }
        return comp;
    }

    public void readFromNBT(NBTTagCompound compound) {
        enabled = compound.getBoolean("DBCDisplayEnabled");
        if (enabled) {
            race = compound.getInteger("DBCRace");
            auraID = compound.getInteger("DBCAuraID");
            auraOn = compound.getBoolean("DBCAuraOn");

            useSkin = compound.getBoolean("DBCUseSkin");
            bodyType = compound.getInteger("DBCBodyType");
            hairType = compound.getString("DBCHairType");
            hairCode = compound.getString("DBCHair");

            hairColor = compound.getInteger("DBCHairColor");
            eyeColor = compound.getInteger("DBCEyeColor");
            bodyCM = compound.getInteger("DBCBodyCM");
            bodyC1 = compound.getInteger("DBCBodyC1");
            bodyC2 = compound.getInteger("DBCBodyC2");
            bodyC3 = compound.getInteger("DBCBodyC3");

            arcoState = compound.getInteger("DBCArcoState");
            hasArcoMask = compound.getBoolean("DBCArcoMask");

            auraID = compound.getInteger("auraID");
            enumAuraTypes = EnumAuraTypes.values()[compound.getInteger("DBCDisplayAura") % EnumAuraTypes.values().length];

            rage = compound.getInteger("rage");
            isTransforming = compound.getBoolean("isTransforming");
            formID = compound.getInteger("formID");
            selectedForm = compound.getInteger("selectedForm");
        }
    }

    @Override
    public void setColor(String type, int color) {
        switch (type.toLowerCase()) {
            case "hair":
                hairColor = color;
                break;
            case "eye":
                eyeColor = color;
                break;
            case "bodycm":
                bodyCM = color;
                break;
            case "bodyc1":
                bodyC1 = color;
                break;
            case "bodyc2":
                bodyC2 = color;
                break;
            case "bodyc3":
                bodyC3 = color;
                break;
            default:
                throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3");
        }
    }

    @Override
    public int getColor(String type) {
        switch (type.toLowerCase()) {
            case "hair":
                return hairColor;
            case "eye":
                return eyeColor;
            case "bodycm":
                return bodyCM;
            case "bodyc1":
                return bodyC1;
            case "bodyc2":
                return bodyC2;
            case "bodyc3":
                return bodyC3;
        }
        throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3");
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public EnumAuraTypes getFormAuraTypes() {
        return enumAuraTypes;
    }

    public void setFormAuraTypes(EnumAuraTypes enumAuraTypes) {
        this.enumAuraTypes = enumAuraTypes;
    }

    @Override
    public void setFormAuraTypes(String type) {
        this.enumAuraTypes = EnumAuraTypes.valueOf(type);
    }

    @Override
    public String getHairCode() {
        return hairCode;
    }

    @Override
    public void setHairCode(String hairCode) {
        this.hairCode = hairCode;
    }


    @Override
    public int getRace() {
        return race;
    }

    @Override
    public void setRace(int race) {
        this.race = ValueUtil.clamp(race, 0, 5);
    }

    @Override
    public int setBodyType() {
        return bodyType;
    }

    @Override
    public void getBodyType(int bodyType) {
        this.bodyType = ValueUtil.clamp(bodyType, 0, 2);
    }

    @Override
    public void setHairType(String type) {
        String s = type.toLowerCase();
        if (s.equals("base") || s.equals("ssj") || s.equals("ssj2") || s.equals("ssj3") || s.equals("ssj4") || s.equals("oozaru") || s.equals("")) {
            hairType = s;

        } else {
            hairType = "";
            throw new CustomNPCsException("Invalid type!");
        }
    }

    @Override
    public boolean hasCoolerMask() {
        return hasArcoMask;
    }

    @Override
    public void hasCoolerMask(boolean has) {
        hasArcoMask = has;
    }

    @Override
    public String getHairType(String type) {
        String s = type.toLowerCase();
        if (s.equals("base") || s.equals("ssj") || s.equals("ssj2") || s.equals("ssj3") || s.equals("ssj4") || s.equals("oozaru") || s.equals(""))
            return hairType;
        else
            throw new CustomNPCsException("Invalid type!");
    }

    /////////////////////////////////////////////
    /////////////////////////////////////////////
    // Auras
    @Override
    public boolean hasAura() {
        boolean has = AuraController.getInstance().has(auraID);
        if (!has && auraID > -1)
            auraID = -1;
        return has;
    }

    @Override
    public IAura getAura() {
        return AuraController.getInstance().get(auraID);
    }

    @Override
    public void setAura(IAura aura) {
        this.auraID = aura.getID();
    }

    @Override
    public void setAura(int auraID) {
        this.auraID = auraID;
    }

    @Override
    public boolean isAuraToggled() {
        return auraOn;
    }


    @Override
    public void toggleAura(boolean toggle) {
        if (AuraController.getInstance().has(auraID))
            this.auraOn = toggle;
        else
            this.auraOn = false;
    }

    //internal usage
    public Aura getAur() {
        if (isInForm()) {
            Form form = getCurrentForm();
            if (form.display.hasAura())
                return form.display.getAur();
        }

        return (Aura) AuraController.getInstance().get(auraID);
    }

    /////////////////////////////////////////////
    /////////////////////////////////////////////
    // Forms

    public void transform(int id) {
        if (FormController.Instance.has(id)) {
            isTransforming = true;
            selectedForm = id;
        } else
            throw new CustomNPCsException("Form " + id + " does not exist!");
    }

    public void transform(Form form) {
        transform(form.id);
    }

    public void cancelTransform() {
        selectedForm = -1;
        isTransforming = false;
    }

    public void setForm(Form form) {
        if (form != null)
            formID = form.id;
    }

    public void setForm(int id) {
        Form f = (Form) FormController.Instance.get(id);
        if (f != null)
            formID = f.id;
    }

    public void setForm(String formName) {
        Form f = (Form) FormController.Instance.get(formName);
        if (f != null)
            formID = f.id;
    }

    public Form getCurrentForm() {
        if (formID > 0)
            return (Form) FormController.Instance.get(formID);
        return null;
    }

    public boolean isInForm() {
        return formID > -1 && getCurrentForm() != null;
    }

    public boolean isInForm(String formName) {
        return getCurrentForm().getName().equals(formName);
    }

    public void setFormLevel(float amount) {
        formLevel = amount;
    }

    public float getFormLevel(int formID) {
        if (formID != -1)
            return formLevel;
        return 0f;
    }

    public int getCurrentArcoState() {
        int state = this.arcoState;
        Form form = this.getCurrentForm();
        if (form != null) {
            switch (form.display.bodyType) {
                case "firstform":
                    state = 0;
                    break;
                case "secondform":
                    state = 2;
                    break;
                case "thirdform":
                    state = 3;
                    break;
                case "finalform":
                    state = 4;
                    break;
                case "ultimatecooler":
                    state = 5;
                    break;
            }
        }
        return state;
    }

    public int getCurrentBodyColor(String part) {
        int body = 0;
        switch (part.toLowerCase()) {
            case "cm":
                body = this.bodyCM;
                break;
            case "c1":
                body = this.bodyC1;
                break;
            case "c2":
                body = this.bodyC2;
                break;
            case "c3":
                body = this.bodyC3;
                break;
        }

        Form form = this.getCurrentForm();
        if (form != null) {
            switch (part) {
                case "cm":
                    if (form.display.bodyCM != -1) {
                        return form.display.bodyCM;
                    }
                    break;
                case "c1":
                    if (form.display.bodyC1 != -1) {
                        return form.display.bodyC1;
                    }
                    break;
                case "c2":
                    if (form.display.bodyC2 != -1) {
                        return form.display.bodyC2;
                    }
                    break;
                case "c3":
                    if (form.display.bodyC3 != -1) {
                        return form.display.bodyC3;
                    }
                    break;
            }
        }

        return body;
    }
}
