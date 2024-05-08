package kamkeel.npcdbc.data.npc;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.npc.IDBCDisplay;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormMastery;
import kamkeel.npcdbc.network.packets.PlaySound;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

import java.util.HashMap;
import java.util.HashSet;

public class DBCDisplay implements IDBCDisplay {

    public boolean enabled = false;
    public String hairCode = "", hairType = "";
    public int hairColor, eyeColor, bodyCM = -1, bodyC1 = -1, bodyC2 = -1, bodyC3 = -1, furColor = -1;
    public boolean hasArcoMask = false;
    public int race = 4, bodyType;
    public int noseType = 1, mouthType = 1, eyeType = 0, arcoState;
    public int auraID = -1;
    public boolean auraOn = false;
    public HashSet<Integer> unlockedAuras = new HashSet<Integer>();
    public int formID = -1, selectedForm = -1, rage;
    public float rageValue;
    public boolean isTransforming, transformed;
    public HashSet<Integer> unlockedForms = new HashSet<Integer>();
    public HashMap<Integer, Float> formLevels = new HashMap<Integer, Float>();
    EntityNPCInterface npc;
    private EnumAuraTypes enumAuraTypes = EnumAuraTypes.None;

    public DBCDisplay(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("DBCDisplayEnabled", enabled);
        if (enabled) {
            compound.setInteger("race", race);
            compound.setBoolean("auraOn", auraOn);

            compound.setInteger("bodyType", bodyType);
            compound.setString("DBCHairType", hairType);
            compound.setString("DBCHair", hairCode);

            compound.setInteger("DBCHairColor", hairColor);
            compound.setInteger("DBCEyeColor", eyeColor);
            compound.setInteger("furColor", furColor);
            compound.setInteger("bodyCM", bodyCM);
            compound.setInteger("bodyC1", bodyC1);
            compound.setInteger("bodyC2", bodyC2);
            compound.setInteger("bodyC3", bodyC3);

            compound.setInteger("arcoState", arcoState);
            compound.setBoolean("hasArcoMask", hasArcoMask);

            compound.setInteger("auraID", auraID);
            compound.setTag("unlockedAuras", NBTTags.nbtIntegerSet(unlockedAuras));
            compound.setInteger("DBCDisplayAura", enumAuraTypes.ordinal());

            compound.setInteger("rage", rage);
            compound.setBoolean("isTransforming", isTransforming);
            compound.setBoolean("transformed", transformed);
            compound.setInteger("formID", formID);
            compound.setInteger("selectedForm", selectedForm);
            compound.setTag("unlockedForms", NBTTags.nbtIntegerSet(unlockedForms));
            compound.setTag("formLevels", NBTTags.nbtIntegerFloatMap(formLevels));
        }
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        enabled = compound.getBoolean("DBCDisplayEnabled");
        if (enabled) {
            race = compound.getInteger("race");
            auraOn = compound.getBoolean("auraOn");

            bodyType = compound.getInteger("bodyType");
            hairType = compound.getString("DBCHairType");
            hairCode = compound.getString("DBCHair");

            hairColor = compound.getInteger("DBCHairColor");
            eyeColor = compound.getInteger("DBCEyeColor");
            furColor = compound.getInteger("furColor");
            bodyCM = compound.getInteger("bodyCM");
            bodyC1 = compound.getInteger("bodyC1");
            bodyC2 = compound.getInteger("bodyC2");
            bodyC3 = compound.getInteger("bodyC3");

            arcoState = compound.getInteger("arcoState");
            hasArcoMask = compound.getBoolean("hasArcoMask");

            auraID = compound.getInteger("auraID");
            unlockedAuras = NBTTags.getIntegerSet(compound.getTagList("unlockedAuras", 10));
            enumAuraTypes = EnumAuraTypes.values()[compound.getInteger("DBCDisplayAura") % EnumAuraTypes.values().length];

            rage = compound.getInteger("rage");
            isTransforming = compound.getBoolean("isTransforming");
            transformed = compound.getBoolean("transformed");
            formID = compound.getInteger("formID");
            selectedForm = compound.getInteger("selectedForm");
            unlockedForms = NBTTags.getIntegerSet(compound.getTagList("unlockedForms", 10));
            formLevels = NBTTags.getIntegerFloatMap(compound.getTagList("formLevels", 10));
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
            case "fur":
                furColor = color;
                break;
            default:
                throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur");
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
            case "fur":
                return furColor;
        }
        throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur");
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

    public void resetAllAuras() {
        auraID = -1;
        unlockedAuras.clear();
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

    public void descend(int id) {
        if (isInForm()) {
            PlaySound.play(npc, getCurrentForm().descendSound, 50);
        }
        if (FormController.Instance.has(id)) {
            formID = id;
        } else
            formID = -1;
        selectedForm = -1;
        isTransforming = false;
        transformed = false;
    }

    public void interruptTransform() {
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

    public void addForm(Form form) {
        addForm(form.id);
    }

    public void addForm(int id) {
        if (!unlockedForms.contains(id)) {
            unlockedForms.add(id);
            formLevels.put(id, 0f);
        }
    }

    public boolean removeForm(Form form) {
        return removeForm(form.id);
    }

    public boolean removeForm(int id) {
        formLevels.remove(id);
        return unlockedForms.remove(id);
    }

    public Form getForm(int id) {
        if (unlockedForms.contains(id))
            return (Form) FormController.getInstance().get(id);

        return null;
    }

    public Form getForm(String name) {
        Form f = (Form) FormController.Instance.get(name);
        if (f != null)
            return getForm(f.id);
        return null;

    }


    public Form getCurrentForm() {
        if (formID > 0)
            return (Form) FormController.Instance.get(formID);
        return null;
    }


    public boolean hasForm(int id) {
        return unlockedForms.contains(id);
    }

    public boolean hasForm(Form form) {
        return unlockedForms.contains(form.id);
    }

    public boolean isInForm() {
        return formID > -1 && getCurrentForm() != null;
    }

    public boolean isInForm(String formName) {
        return getCurrentForm().getName().equals(formName);
    }


    public void resetAllForm() {
        formID = -1;
        unlockedForms.clear();
        formLevels.clear();
    }

    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    // Form mastery stuff
    public void updateCurrentFormMastery(String gainType) {
        updateFormMastery(formID, gainType);
    }

    public void updateFormMastery(int formID, String gainType) {
        Form f = FormController.getInstance().customForms.get(formID);
        if (f == null || !isInForm())
            return;

        FormMastery fm = (FormMastery) f.getMastery();
        if (!formLevels.containsKey(f.id))
            return;
        int mind = 0;
        float playerLevel = formLevels.get(f.id);
        float fullGain = fm.calculateFullGain(gainType, playerLevel, mind);

        playerLevel = ValueUtil.clamp(playerLevel + fullGain, 0, fm.maxLevel); //updated level
        formLevels.replace(f.id, playerLevel);
    }

    public void addFormLevel(int formID, float amount) {
        Form form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            float current = formLevels.get(formID);
            float updated = ValueUtil.clamp(current + amount, 0, ((FormMastery) form.getMastery()).maxLevel);
            formLevels.put(formID, updated);
        }
    }

    public void setFormLevel(int formID, float amount) {
        Form form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            float updated = ValueUtil.clamp(amount, 0, ((FormMastery) form.getMastery()).maxLevel);
            formLevels.put(formID, updated);
        }
    }

    public void removeFormMastery(int formID) {
        Form form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            formLevels.remove(formID);
        }
    }

    public float getFormLevel(int formID) {
        if (formID != -1 && formLevels.containsKey(formID))
            return formLevels.get(formID);
        return 0f;
    }

    public float getCurrentLevel() {
        return getFormLevel(formID);
    }

    ////////////////////////////////////////////////
    ////////////////////////////////////////////////

}
