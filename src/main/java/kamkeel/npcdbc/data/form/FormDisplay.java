package kamkeel.npcdbc.data.form;

import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IFormDisplay;
import kamkeel.npcdbc.api.outline.IOutline;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

import java.util.EnumSet;
import java.util.Set;

public class FormDisplay implements IFormDisplay {

    private final Form parent;

    public float formSize = 1.0f;
    public float formWidth = 1.0f;
    public boolean keepOriginalSize = true;

    public String hairCode = "";
    public String hairType = "";

    public String bodyType = "";

    public BodyColor bodyColors = new BodyColor();
    public int auraColor = -1;
    public int kiBarColor = -1;

    public boolean hasBodyFur = false;
    public int furType = 0;

    public boolean hasArcoMask = false;
    public boolean effectMajinHair = false;
    public boolean hasPupils = false;
    public boolean isBerserk, hasEyebrows = true;

    public boolean isCustomizable = false;

    public OverlayChain overlays = new OverlayChain().enable(false);
    public Set<Overlay.Type> disabledOverlayTypes = EnumSet.noneOf(Overlay.Type.class);

    public FacePartData faceData = new FacePartData();

    public int auraID = -1, outlineID = -1;

    public FormDisplay(Form parent) {
        this.parent = parent;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound rendering = compound.getCompoundTag("rendering");
        auraColor = rendering.getInteger("auraColor");

        kiBarColor = rendering.getInteger("kiBarColor");

        hairCode = rendering.getString("hairCode");
        hairType = rendering.getString("hairType");

        bodyType = rendering.getString("bodyType");

        bodyColors.readFromNBT(rendering);
        overlays.readFromNBT(rendering);
        faceData.readFromNBT(rendering, false);


        hasArcoMask = rendering.getBoolean("hasArcoMask");
        effectMajinHair = rendering.getBoolean("effectMajinHair");
        hasBodyFur = rendering.getBoolean("hasBodyFur");
        furType = rendering.getInteger("furType");
        isBerserk = rendering.getBoolean("isBerserk");
        hasPupils = rendering.getBoolean("hasPupils");
        hasEyebrows = !rendering.hasKey("hasEyebrows") ? true : rendering.getBoolean("hasEyebrows");

        formSize = rendering.getFloat("formSize");
        if (rendering.hasKey("formWidth", Constants.NBT.TAG_FLOAT))
            formWidth = rendering.getFloat("formWidth");
        keepOriginalSize = rendering.getBoolean("keepOriginalSize");

        auraID = rendering.hasKey("auraID") ? rendering.getInteger("auraID") : -1;
        outlineID = rendering.hasKey("outlineID") ? rendering.getInteger("outlineID") : -1;

        isCustomizable = rendering.getBoolean("isCustomizable");


        if (compound.hasKey("overlayTypes")) {
            disabledOverlayTypes.clear();
            byte[] arr = compound.getByteArray("overlayTypes");
            Overlay.Type[] values = Overlay.Type.values();
            for (byte ordinal : arr) {
                if (ordinal >= 0 && ordinal < values.length)
                    disabledOverlayTypes.add(values[ordinal]);
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound rendering = new NBTTagCompound();
        rendering.setInteger("auraColor", auraColor);
        rendering.setInteger("kiBarColor", kiBarColor);
        rendering.setInteger("furType", furType);

        rendering.setString("hairCode", hairCode);
        rendering.setString("hairType", hairType);
        rendering.setString("bodyType", bodyType);

        bodyColors.writeToNBT(rendering);
        overlays.writeToNBT(rendering);
        faceData.writeToNBT(rendering, false);

        rendering.setBoolean("hasArcoMask", hasArcoMask);
        rendering.setBoolean("effectMajinHair", effectMajinHair);
        rendering.setBoolean("hasBodyFur", hasBodyFur);
        rendering.setBoolean("isBerserk", isBerserk);
        rendering.setBoolean("hasPupils", hasPupils);
        rendering.setBoolean("hasEyebrows", hasEyebrows);

        rendering.setFloat("formSize", formSize);
        rendering.setFloat("formWidth", formWidth);
        rendering.setBoolean("keepOriginalSize", keepOriginalSize);

        rendering.setInteger("auraID", auraID);
        rendering.setInteger("outlineID", outlineID);

        rendering.setBoolean("isCustomizable", isCustomizable);


        if (!disabledOverlayTypes.isEmpty()) {
            byte[] arr = new byte[disabledOverlayTypes.size()];
            int i = 0;
            for (Overlay.Type t : disabledOverlayTypes)
                arr[i++] = (byte) t.ordinal();
            compound.setByteArray("overlayTypes", arr);
        }

        compound.setTag("rendering", rendering);
        return compound;
    }

    //internal usage
    public int getFurColor(DBCData data) {
        int c1 = data.skinType == 1 ? JRMCoreH.dnsBodyC1(data.DNS) : JRMCoreH.dnsBodyC1_0(data.DNS);

        if (c1 != 6498048 && bodyColors.furColor == -1)  //default
            return c1;

        if (bodyColors.furColor == -1)
            return 0xDA152C;

        return bodyColors.furColor;
    }
    //internal usage

    public boolean hasHairCol(DBCData data) {
        return bodyColors.hairColor != -1 && data.Race != DBCRace.NAMEKIAN;
    }

    //internal usage
    public int getHairColor(DBCData data) {
        if (data.Race == DBCRace.NAMEKIAN)
            return bodyColors.bodyCM;
        return bodyColors.hairColor;
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
        if (s.equals("firstform") || s.equals("secondform") || s.equals("thirdform") || s.equals("finalform") || s.equals("ultimatecooler") || s.equals("golden") || s.equals(""))
            bodyType = s;
        else
            throw new CustomNPCsException("Invalid type! Legal: firstform, secondform, thirdform, finalform, ultimatecooler, golden");

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
    public float getWidth() {
        return formWidth;
    }

    @Override
    public void setWidth(float width) {
        formSize = ValueUtil.clamp(width, 0.2f, 3);
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
                return bodyColors.hairColor != -1;
            case "eye":
                return bodyColors.eyeColor != -1;
            case "bodycm":
                return bodyColors.bodyCM != -1;
            case "bodyc1":
                return bodyColors.bodyC1 != -1;
            case "bodyc2":
                return bodyColors.bodyC2 != -1;
            case "bodyc3":
                return bodyColors.bodyC3 != -1;
            case "fur":
                return bodyColors.furColor != -1;

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
                bodyColors.hairColor = color;
                break;
            case "eye":
                bodyColors.eyeColor = color;
                break;
            case "bodycm":
                bodyColors.bodyCM = color;
                break;
            case "bodyc1":
                bodyColors.bodyC1 = color;
                break;
            case "bodyc2":
                bodyColors.bodyC2 = color;
                break;
            case "bodyc3":
                bodyColors.bodyC3 = color;
                break;
            case "fur":
                bodyColors.furColor = color;
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

        } else
            throw new CustomNPCsException("Invalid type! Legal types: base, raditz, ssj, ssj2, ssj3, ssj4, oozaru");

    }


    @Override
    public String getHairType() {
        return hairType;
    }

    @Override
    public int getColor(String type) {
        switch (type.toLowerCase()) {
            case "kibar":
                return kiBarColor;
            case "aura":
                return auraColor;
            case "hair":
                return bodyColors.hairColor;
            case "eye":
                return bodyColors.eyeColor;
            case "bodycm":
                return bodyColors.bodyCM;
            case "bodyc1":
                return bodyColors.bodyC1;
            case "bodyc2":
                return bodyColors.bodyC2;
            case "bodyc3":
                return bodyColors.bodyC3;
            case "fur":
                return bodyColors.furColor;
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

    public void setFurType(int type) {
        this.furType = Math.max(0, Math.min(2, type));
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

    public FacePartData getFaceData() {
        return faceData;
    }

    public IFormDisplay save() {
        if (parent != null)
            parent.save();
        return this;
    }

    @Override
    public void setCustomizable(boolean customizable) {
        this.isCustomizable = customizable;
    }

    @Override
    public boolean isCustomizable() {
        return isCustomizable;
    }

    /**
     * Class made for the purpose of letting players customize their forms from the default colors.
     */
    public static class BodyColor {
        public int bodyCM = -1, bodyC1 = -1, bodyC2 = -1, bodyC3 = -1, furColor = -1;
        public int hairColor = -1;
        public int eyeColor = -1;

        public static boolean canBeCustomized(String type, int race, Form form) {
            String hairType = form.display.hairType;
            boolean isHumanoid = (DBCRace.isSaiyan(race) || race == DBCRace.HUMAN);
            switch (type.toLowerCase()) {
                case "bodycm":
                    return !isHumanoid;
                case "eye":
                    return true;
                case "bodyc1":
                case "bodyc2":
                case "bodyc3":
                    return race == DBCRace.NAMEKIAN || race == DBCRace.ARCOSIAN;
                case "hair":
                    return (isHumanoid || (DBCRace.MAJIN == race && form.display.effectMajinHair)) && (!hairType.equals("oozaru"));
                case "fur":
                    return DBCRace.isSaiyan(race) && (hairType.equals("ssj4") || form.display.hasBodyFur);
            }
            return true;
        }

        @SideOnly(Side.CLIENT)
        public int getProperColor(FormDisplay formDisplay, String type) {
            return getProperColor(formDisplay.getColor(type), type);
        }

        @SideOnly(Side.CLIENT)
        public int getProperColor(int formColor, String type) {
            int customColor = getColor(type);
            if (customColor != -1)
                return customColor;
            return formColor;
        }

        @SideOnly(Side.CLIENT)
        public boolean hasAnyColor(FormDisplay formDisplay, String type) {
            if (hasColor(type))
                return true;
            return formDisplay.hasColor(type);
        }

        @SideOnly(Side.CLIENT)
        public int getFurColor(FormDisplay display, DBCData data) {
            if (this.furColor != -1)
                return this.furColor;
            return display.getFurColor(data);
        }

        @SideOnly(Side.CLIENT)
        public boolean hasHairColor(DBCData data, FormDisplay display) {
            if (data.Race == DBCRace.NAMEKIAN)
                return false;
            return hairColor != -1 || display.bodyColors.hairColor != -1;
        }

        public void readFromNBT(NBTTagCompound compound) {
            eyeColor = compound.getInteger("eyeColor");
            hairColor = compound.getInteger("hairColor");
            bodyCM = compound.getInteger("bodyCM");
            bodyC1 = compound.getInteger("bodyC1");
            bodyC2 = compound.getInteger("bodyC2");
            bodyC3 = compound.getInteger("bodyC3");
            furColor = compound.getInteger("furColor");
        }

        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            compound.setInteger("eyeColor", eyeColor);
            compound.setInteger("hairColor", hairColor);
            compound.setInteger("furColor", furColor);
            compound.setInteger("bodyCM", bodyCM);
            compound.setInteger("bodyC1", bodyC1);
            compound.setInteger("bodyC2", bodyC2);
            compound.setInteger("bodyC3", bodyC3);
            return compound;
        }

        public boolean hasColor(String type) {
            switch (type.toLowerCase()) {
                case "hair":
                    return this.hairColor != -1;
                case "eye":
                    return this.eyeColor != -1;
                case "bodycm":
                    return this.bodyCM != -1;
                case "bodyc1":
                    return this.bodyC1 != -1;
                case "bodyc2":
                    return this.bodyC2 != -1;
                case "bodyc3":
                    return this.bodyC3 != -1;
                case "fur":
                    return this.furColor != -1;
                default:
                    return false;
            }
        }

        public int getColor(String type) {
            switch (type.toLowerCase()) {
                case "hair":
                    return this.hairColor;
                case "eye":
                    return this.eyeColor;
                case "bodycm":
                    return this.bodyCM;
                case "bodyc1":
                    return this.bodyC1;
                case "bodyc2":
                    return this.bodyC2;
                case "bodyc3":
                    return this.bodyC3;
                case "fur":
                    return this.furColor;
                default:
                    return -1;
            }
        }

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
            }
        }

        public boolean isEmpty() {
            return bodyCM == -1 && bodyC1 == -1 && bodyC2 == -1 && bodyC3 == -1 &&
                furColor == -1 && hairColor == -1 && eyeColor == -1;
        }
    }
}
