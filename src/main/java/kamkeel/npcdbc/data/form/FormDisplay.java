package kamkeel.npcdbc.data.form;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IFormDisplay;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.api.outline.IOutline;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

public class FormDisplay implements IFormDisplay {

    private final Form parent;

    public float formSize = 1.0f;
    public boolean keepOriginalSize = true;

    public String hairCode = "";
    public String hairType = "";
    public int hairColor = -1;
    public int eyeColor = -1;
    public int auraColor = -1;
    public int kiBarColor = -1;
    public String bodyType = "";
    public int bodyCM = -1, bodyC1 = -1, bodyC2 = -1, bodyC3 = -1, furColor = -1;
    public boolean hasBodyFur = false;
    public boolean hasArcoMask = false;
    public boolean effectMajinHair = false;
    public boolean isBerserk, hasEyebrows = true;

    public int auraID = -1, outlineID = -1;

    public FormDisplay(Form parent) {
        this.parent = parent;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound rendering = compound.getCompoundTag("rendering");
        auraColor = rendering.getInteger("auraColor");
        eyeColor = rendering.getInteger("eyeColor");
        hairColor = rendering.getInteger("hairColor");
        kiBarColor = rendering.getInteger("kiBarColor");

        furColor = rendering.getInteger("furColor");
        hairCode = rendering.getString("hairCode");
        hairType = rendering.getString("hairType");

        bodyType = rendering.getString("bodyType");
        bodyCM = rendering.getInteger("bodyCM");
        bodyC1 = rendering.getInteger("bodyC1");
        bodyC2 = rendering.getInteger("bodyC2");
        bodyC3 = rendering.getInteger("bodyC3");

        hasArcoMask = rendering.getBoolean("hasArcoMask");
        effectMajinHair = rendering.getBoolean("effectMajinHair");
        hasBodyFur = rendering.getBoolean("hasBodyFur");
        isBerserk = rendering.getBoolean("isBerserk");
        hasEyebrows = !rendering.hasKey("hasEyebrows") ? true : rendering.getBoolean("hasEyebrows");

        formSize = rendering.getFloat("formSize");
        keepOriginalSize = rendering.getBoolean("keepOriginalSize");

        auraID = rendering.hasKey("auraID") ? rendering.getInteger("auraID") : -1;
        outlineID = rendering.hasKey("outlineID") ? rendering.getInteger("outlineID") : -1;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound rendering = new NBTTagCompound();
        rendering.setInteger("auraColor", auraColor);
        rendering.setInteger("eyeColor", eyeColor);
        rendering.setInteger("hairColor", hairColor);
        rendering.setInteger("kiBarColor", kiBarColor);

        rendering.setString("hairCode", hairCode);
        rendering.setString("hairType", hairType);
        rendering.setString("bodyType", bodyType);

        rendering.setInteger("furColor", furColor);
        rendering.setInteger("bodyCM", bodyCM);
        rendering.setInteger("bodyC1", bodyC1);
        rendering.setInteger("bodyC2", bodyC2);
        rendering.setInteger("bodyC3", bodyC3);

        rendering.setBoolean("hasArcoMask", hasArcoMask);
        rendering.setBoolean("effectMajinHair", effectMajinHair);
        rendering.setBoolean("hasBodyFur", hasBodyFur);
        rendering.setBoolean("isBerserk", isBerserk);
        rendering.setBoolean("hasEyebrows", hasEyebrows);

        rendering.setFloat("formSize", formSize);
        rendering.setBoolean("keepOriginalSize", keepOriginalSize);


        rendering.setInteger("auraID", auraID);
        rendering.setInteger("outlineID", outlineID);

        compound.setTag("rendering", rendering);
        return compound;
    }

    //internal usage
    public int getFurColor(DBCData data) {
        if (data.skinType == 0)
            return 0xDA152C;

        int c1 = JRMCoreH.dnsBodyC1(data.DNS);
        if (c1 != 6498048 && furColor == -1)  //default
            return c1;

        if (furColor == -1)
            return 0xDA152C;

        return furColor;
    }
    //internal usage

    public boolean hasHairCol(DBCData data) {
        return hairColor != -1 && data.Race != DBCRace.NAMEKIAN;
    }

    //internal usage
    public int getHairColor(DBCData data) {
        if(data.Race == DBCRace.NAMEKIAN)
            return bodyCM;
        return hairColor;
    }

    @Override
    public boolean getKeepOriginalSize() {
        return this.keepOriginalSize;
    }

    @Override
    public void setKeepOriginalSize(boolean keepOriginalSize) {
        this.keepOriginalSize = keepOriginalSize;
    }

    @Override
    public String getHairCode() {
        return hairCode;
    }

    @Override
    public void setHairCode(String hairCode) {
        if (hairCode.length() != 786 && hairCode.length() != 784 && hairCode.length() != 392)
            hairCode = "";
        this.hairCode = hairCode;
    }

    @Override
    public String getBodyType() {
        return bodyType;
    }

    @Override
    public void setBodyType(String type) {
        String s = type.toLowerCase();
        if (s.equals("firstform") || s.equals("secondform") || s.equals("thirdform") || s.equals("finalform") || s.equals("ultimatecooler") || s.equals(""))
            bodyType = s;
        else
            throw new CustomNPCsException("Invalid type! Legal: firstform, secondform, thirdform, finalform, ultimatecooler");

    }

    @Override
    public boolean isBerserk() {
        return isBerserk;
    }

    @Override
    public void setBerserk(boolean isBerserk) {
        this.isBerserk = isBerserk;
    }

    @Override
    public boolean hasEyebrows() {
        return hasEyebrows;
    }

    @Override
    public void hasEyebrows(boolean has) {
        hasEyebrows = has;
    }


    @Override
    public float getSize() {
        return formSize;
    }

    @Override
    public void setSize(float size) {
        formSize = ValueUtil.clamp(size, 0.2f, 3);
    }

    @Override
    public boolean hasSize() {
        return formSize != 1f;
    }

    @Override
    public boolean hasColor(String type) {
        switch (type.toLowerCase()) {
            case "kibar":
                return kiBarColor != -1;
            case "aura":
                return auraColor != -1;
            case "hair":
                return hairColor != -1;
            case "eye":
                return eyeColor != -1;
            case "bodycm":
                return bodyCM != -1;
            case "bodyc1":
                return bodyC1 != -1;
            case "bodyc2":
                return bodyC2 != -1;
            case "bodyc3":
                return bodyC3 != -1;
            case "fur":
                return furColor != -1;

        }
        throw new CustomNPCsException("Invalid type! Legal types: kiBar, aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur");
    }

    @Override
    public void setColor(String type, int color) {
        switch (type.toLowerCase()) {
            case "kibar":
                kiBarColor = color;
                break;
            case "aura":
                auraColor = color;
                break;
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
                throw new CustomNPCsException("Invalid type! Legal types: kiBar, aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur");
        }
    }

    @Override
    public void setHairType(String type) {
        String s = type.toLowerCase();
        if (s.equals("base") || s.equals("ssj") || s.equals("ssj2") || s.equals("ssj3") || s.equals("ssj4") || s.equals("oozaru") || s.equals("raditz") || s.equals("")) {
            hairType = s;

        } else {
            hairType = "";
            throw new CustomNPCsException("Invalid type!");
        }
    }


    @Override
    public String getHairType(String type) {
        String s = type.toLowerCase();
        if (s.equals("base") || s.equals("ssj") || s.equals("ssj2") || s.equals("ssj3") || s.equals("ssj4") || s.equals("oozaru") || s.equals("raditz") || s.equals(""))
            return hairType;
        else
            throw new CustomNPCsException("Invalid type!");
    }

    @Override
    public int getColor(String type) {
        switch (type.toLowerCase()) {
            case "kibar":
                return kiBarColor;
            case "aura":
                return auraColor;
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
        throw new CustomNPCsException("Invalid type! Legal types: kiBar, aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur");
    }


    @Override
    public boolean hasArcoMask() {
        return hasArcoMask;
    }

    @Override
    public void hasArcoMask(boolean hasMask) {
        this.hasArcoMask = hasMask;
    }

    @Override
    public boolean hasBodyFur() {
        return hasBodyFur;
    }

    @Override
    public void hasBodyFur(boolean hasFur) {
        this.hasBodyFur = hasFur;
    }

    @Override
    public boolean effectMajinHair() {
        return effectMajinHair;
    }

    @Override
    public void setEffectMajinHair(boolean effect) {
        this.effectMajinHair = effect;
    }

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

    //internal usage
    public Aura getAur() {
        return (Aura) AuraController.getInstance().get(auraID);
    }

    @Override
    public void setOutline(int id) {
        if (OutlineController.Instance.has(id))
            this.outlineID = id;
        else
            outlineID = -1;
    }

    @Override
    public void setOutline(IOutline outline) {
        int id = outline != null ? outline.getID() : -1;
        setOutline(id);
    }

    public IFormDisplay save() {
        if (parent != null)
            parent.save();
        return this;
    }
}
