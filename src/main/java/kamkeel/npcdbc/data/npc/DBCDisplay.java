package kamkeel.npcdbc.data.npc;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.npc.IDBCDisplay;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.IAuraData;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixin.INPCStats;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

public class DBCDisplay implements IDBCDisplay, IAuraData {

    public final EntityNPCInterface npc;
    public boolean enabled = false;

    // Hair Display //
    public String hairCode = "", hairType = "";
    public int hairColor = -1;

    // Race Display //
    public byte race = -1;
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

    // Server Side Usage
    public float rageValue;
    public int tempState, stateChange, state2Change, auraTime, auraType, bendTime;
    private EnumAuraTypes2D enumAuraTypes = EnumAuraTypes2D.None;

    public EntityAura auraEntity;

    public DBCDisplay(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound comp) {
        comp.setBoolean("DBCDisplayEnabled", enabled);
        if (enabled) {
            NBTTagCompound dbcDisplay = new NBTTagCompound();

            dbcDisplay.setInteger("DBCBodyType", bodyType);
            dbcDisplay.setString("DBCHairType", hairType);
            dbcDisplay.setString("DBCHair", hairCode);

            dbcDisplay.setInteger("DBCHairColor", hairColor);
            dbcDisplay.setInteger("DBCEyeColor", eyeColor);

            dbcDisplay.setInteger("DBCRace", race);
            dbcDisplay.setBoolean("DBCUseSkin", useSkin);
            dbcDisplay.setInteger("DBCBodyCM", bodyCM);
            dbcDisplay.setInteger("DBCBodyC1", bodyC1);
            dbcDisplay.setInteger("DBCBodyC2", bodyC2);
            dbcDisplay.setInteger("DBCBodyC3", bodyC3);
            dbcDisplay.setInteger("DBCArcoState", arcoState);

            dbcDisplay.setBoolean("DBCArcoMask", hasArcoMask);

            dbcDisplay.setInteger("DBCRage", rage);
            dbcDisplay.setBoolean("DBCIsTransforming", isTransforming);
            dbcDisplay.setInteger("DBCFormID", formID);
            dbcDisplay.setFloat("DBCFormLevel", formLevel);
            dbcDisplay.setInteger("DBCSelectedForm", selectedForm);

            dbcDisplay.setInteger("DBCAuraID", auraID);
            dbcDisplay.setBoolean("DBCAuraOn", auraOn);
            dbcDisplay.setInteger("DBCDisplayAura", enumAuraTypes.ordinal());

            comp.setTag("DBCDisplay", dbcDisplay);
        } else {
            comp.removeTag("DBCDisplay");
        }
        return comp;
    }

    public void readFromNBT(NBTTagCompound comp) {
        enabled = comp.getBoolean("DBCDisplayEnabled");
        if (enabled) {
            NBTTagCompound dbcDisplay = comp.getCompoundTag("DBCDisplay");

            race = dbcDisplay.getByte("DBCRace");
            auraID = dbcDisplay.getInteger("DBCAuraID");
            auraOn = dbcDisplay.getBoolean("DBCAuraOn");

            useSkin = dbcDisplay.getBoolean("DBCUseSkin");
            bodyType = dbcDisplay.getInteger("DBCBodyType");
            hairType = dbcDisplay.getString("DBCHairType");
            hairCode = dbcDisplay.getString("DBCHair");

            hairColor = dbcDisplay.getInteger("DBCHairColor");
            eyeColor = dbcDisplay.getInteger("DBCEyeColor");
            bodyCM = dbcDisplay.getInteger("DBCBodyCM");
            bodyC1 = dbcDisplay.getInteger("DBCBodyC1");
            bodyC2 = dbcDisplay.getInteger("DBCBodyC2");
            bodyC3 = dbcDisplay.getInteger("DBCBodyC3");

            arcoState = dbcDisplay.getInteger("DBCArcoState");
            hasArcoMask = dbcDisplay.getBoolean("DBCArcoMask");

            auraID = dbcDisplay.getInteger("DBCAuraID");
            enumAuraTypes = EnumAuraTypes2D.values()[dbcDisplay.getInteger("DBCDisplayAura") % EnumAuraTypes2D.values().length];

            rage = dbcDisplay.getInteger("DBCRage");
            isTransforming = dbcDisplay.getBoolean("DBCIsTransforming");
            formID = dbcDisplay.getInteger("DBCFormID");
            selectedForm = dbcDisplay.getInteger("DBCSelectedForm");
        } else {
            comp.removeTag("DBCDisplay");
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

    public boolean hasColor(String type) {
        Form form = this.getForm();
        boolean inF = form != null;
        switch (type.toLowerCase()) {
            case "hair":
                return (inF ? form.display.hairColor : hairColor) != -1;
            case "eye":
                return (inF ? form.display.eyeColor : eyeColor) != -1;
            case "bodycm":
                return (inF ? form.display.bodyCM : bodyCM) != -1;
            case "bodyc1":
                return (inF ? form.display.bodyC1 : bodyC1) != -1;
            case "bodyc2":
                return (inF ? form.display.bodyC2 : bodyC2) != -1;
            case "bodyc3":
                return (inF ? form.display.bodyC3 : bodyC3) != -1;

        }
        throw new CustomNPCsException("Invalid type! Legal types: hair, eye, bodycm, bodyc1, bodyc2, bodyc3");
    }

    @Override
    public int getColor(String type) {
        Form form = this.getForm();
        boolean inF = form != null;
        switch (type.toLowerCase()) {
            case "hair":
                return inF ? form.display.hairColor : hairColor;
            case "eye":
                return inF ? form.display.eyeColor : eyeColor;
            case "bodycm":
                return inF ? form.display.bodyCM : bodyCM;
            case "bodyc1":
                return inF ? form.display.bodyC1 : bodyC1;
            case "bodyc2":
                return inF ? form.display.bodyC2 : bodyC2;
            case "bodyc3":
                return inF ? form.display.bodyC3 : bodyC3;
        }
        throw new CustomNPCsException("Invalid type! Legal types: hair, eye, bodycm, bodyc1, bodyc2, bodyc3");
    }


    public int getCurrentArcoState() {
        int state = this.arcoState;
        Form form = this.getForm();
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


    @Override
    public boolean isEnabled() {
        return enabled;
    }


    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public EnumAuraTypes2D getFormAuraTypes() {
        return enumAuraTypes;
    }

    public void setFormAuraTypes(EnumAuraTypes2D enumAuraTypes) {
        this.enumAuraTypes = enumAuraTypes;
    }

    @Override
    public void setFormAuraTypes(String type) {
        this.enumAuraTypes = EnumAuraTypes2D.valueOf(type);
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
    public byte getRace() {
        return race;
    }

    @Override
    public void setRace(byte race) {
        this.race = ValueUtil.clamp(race, (byte) 0,  (byte) 5);
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
        setAura(aura.getID());
    }

    @Override
    public void setAura(int auraID) {
        if (AuraController.Instance.has(auraID))
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

    @Override
    public boolean isInAura(IAura aura) {
        return aura.getID() == auraID;
    }

    public boolean isAuraOn() {
        return auraOn || isTransforming;
    }

    public Aura getToggledAura() {
        if (!auraOn && !isTransforming)
            return null;

        return getAur();
    }


    //internal usage
    public Aura getAur() {
        if (isInForm()) {
            Form form = (Form) getForm();
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

    public void transform(IForm form) {
        transform(form.getID());
    }

    public void cancelTransform() {
        selectedForm = -1;
        isTransforming = false;
    }

    public void descend(int id) {
        TransformController.npcDescend(npc, id);

    }

    public IForm getCurrentForm() {
        return getForm();
    }

    public Form getForm() {
        if (formID > 0)
            return (Form) FormController.Instance.get(formID);
        return null;
    }

    public void setForm(IForm form) {
        if (form != null)
            formID = form.getID();
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

 
    public boolean isInForm() {
        return formID > -1 && getForm() != null;
    }

    public boolean isInForm(IForm form) {
        return formID == form.getID();
    }

    public void setFormLevel(float amount) {
        formLevel = amount;
    }

    public float getFormLevel(int formID) {
        if (formID != -1)
            return formLevel;
        return 0f;
    }

    public void setDefaultColors() {
        if (race < 3) {
            bodyCM = 16297621;
        } else if (race == DBCRace.NAMEKIAN) {
            hairColor = 5095183;
            bodyCM = 5095183;
            bodyC1 = 13796998;
            bodyC2 = 12854822;
        } else if (race == DBCRace.ARCOSIAN) {
            bodyCM = 15460342;
            bodyC1 = 16111595;
            bodyC2 = 8533141;
            bodyC3 = 16550015;
        } else if (race == DBCRace.MAJIN)
            bodyCM = 16757199;

        eyeColor = 0x000000;
    }

    @Override
    public EntityAura getAuraEntity() {
        return this.auraEntity;
    }

    @Override
    public void setAuraEntity(EntityAura aura) {
        this.auraEntity = aura;
    }

    @Override
    public int getAuraColor() {
        return 11075583;
    }

    @Override
    public boolean isTransforming() {
        return isTransforming;
    }

    @Override
    public boolean isChargingKi() {
        return auraOn;
    }
    

    @Override
    public int getFormID() {
        return formID;
    }

    @Override
    public byte getRelease() {
        return ((INPCStats) npc.stats).getDBCStats().release;
    }

    @Override
    public byte getState() {
        return 0;
    }

    @Override
    public boolean isForm(int form) {
        return false;
    }

    @Override
    public int getDBCColor() {
        return getAuraColor();
    }
}
