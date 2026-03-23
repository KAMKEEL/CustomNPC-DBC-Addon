package kamkeel.npcdbc.data.form;

import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.client.overlay.IOverlay;
import kamkeel.npcdbc.api.client.overlay.IOverlayChain;
import kamkeel.npcdbc.api.form.IFormDisplay;
import kamkeel.npcdbc.api.outline.IOutline;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.race.display.ColorSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FormDisplay implements IFormDisplay {

    private final Form parent;

    public float formSize = 1.0f;
    public float formWidth = 1.0f;
    public boolean keepOriginalSize = true;

    public String hairCode = "";
    public String hairType = "base";

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
        if (hairType.isEmpty())
            hairType = "base";

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
            Overlay.Type[] values = IOverlay.Type.values();
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

        if (c1 != 6498048 && bodyColors.getColor(BodyColor.FUR) == -1)  //default
            return c1;

        if (bodyColors.getColor(BodyColor.FUR) == -1)
            return 0xDA152C;

        return bodyColors.getColor(BodyColor.FUR);
    }
    //internal usage

    public boolean hasHairCol(DBCData data) {
        return bodyColors.getColor(BodyColor.HAIR) != -1 && data.Race != DBCRace.NAMEKIAN;
    }

    //internal usage
    public int getHairColor(DBCData data) {
        if (data.Race == DBCRace.NAMEKIAN)
            return bodyColors.getColor(BodyColor.BODY_CM);
        return bodyColors.getColor(BodyColor.HAIR);
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
    public boolean hasColor(String slotId) {
        switch (slotId.toLowerCase()) {
            case "kibar": return kiBarColor != -1;
            case "aura": return auraColor != -1;
            default: return bodyColors.hasColor(slotId);
        }
    }

    @Override
    public void setColor(String slotId, int color) {
        switch (slotId.toLowerCase()) {
            case "kibar": kiBarColor = color; break;
            case "aura": auraColor = color; break;
            default: bodyColors.setColor(slotId, color);
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
    public int getColor(String slotId) {
        switch (slotId.toLowerCase()) {
            case "kibar": return kiBarColor;
            case "aura": return auraColor;
            default: return bodyColors.getColor(slotId);
        }
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

    @Override
    public IOverlayChain getOverlayChain() {
        return overlays;
    }

    @Override
    public IOverlay.Type[] getDisabledOverlayTypes() {
        return disabledOverlayTypes.toArray(new IOverlay.Type[0]);
    }

    @Override
    public void setDisabledOverlayTypes(IOverlay.Type[] types) {
        disabledOverlayTypes.clear();
        if (types != null) {
            for (IOverlay.Type t : types) {
                if (t != null)
                    disabledOverlayTypes.add(t);
            }
        }
    }
    /**
     * Class made for the purpose of letting players customize their forms from the default colors.
     */
    public static class BodyColor {
        private final Map<String, Integer> colors = new HashMap<>();

        public static final String EYES = ColorSlot.EYES;
        public static final String HAIR = ColorSlot.HAIR;
        public static final String FUR = ColorSlot.FUR;
        public static final String BODY_CM = ColorSlot.BODY_CM;
        public static final String BODY_C1 = ColorSlot.BODY_C1;
        public static final String BODY_C2 = ColorSlot.BODY_C2;
        public static final String BODY_C3 = ColorSlot.BODY_C3;

        public boolean hasColor(String slotId) {
            Integer val = colors.get(slotId.toLowerCase());
            return val != null && val != -1;
        }

        public int getColor(String slotId) {
            return colors.getOrDefault(slotId.toLowerCase(), -1);
        }

        public void setColor(String slotId, int color) {
            colors.put(slotId.toLowerCase(), color);
        }

        public void clearColor(String slotId) {
            colors.put(slotId.toLowerCase(), -1);
        }

        public boolean isEmpty() {
            return colors.values().stream().allMatch(v -> v == -1);
        }

        public void readFromNBT(NBTTagCompound compound) {
            colors.clear();
            if (compound.hasKey("bodyColors")) {
                NBTTagCompound tag = compound.getCompoundTag("bodyColors");
                for (Object key : tag.func_150296_c()) {
                    String id = (String) key;
                    colors.put(id.toLowerCase(), tag.getInteger(id));
                }
            } else {
                // compat with old forms
                readLegacyNBT(compound);
            }
        }

        private void readLegacyNBT(NBTTagCompound compound) {
            colors.put(EYES, compound.getInteger("eyeColor"));
            colors.put(HAIR, compound.getInteger("hairColor"));
            colors.put(FUR, compound.getInteger("furColor"));
            colors.put(BODY_CM, compound.getInteger("bodyCM"));
            colors.put(BODY_C1, compound.getInteger("bodyC1"));
            colors.put(BODY_C2, compound.getInteger("bodyC2"));
            colors.put(BODY_C3, compound.getInteger("bodyC3"));
        }

        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            NBTTagCompound tag = new NBTTagCompound();
            for (Map.Entry<String, Integer> entry : colors.entrySet()) {
                tag.setInteger(entry.getKey(), entry.getValue());
            }
            compound.setTag("bodyColors", tag);
            return compound;
        }

        @SideOnly(Side.CLIENT)
        public int getProperColor(FormDisplay formDisplay, String slotId) {
            return getProperColor(formDisplay.getColor(slotId), slotId);
        }

        @SideOnly(Side.CLIENT)
        public int getProperColor(int formColor, String slotId) {
            int customColor = getColor(slotId);
            if (customColor != -1)
                return customColor;
            return formColor;
        }

        @SideOnly(Side.CLIENT)
        public boolean hasAnyColor(FormDisplay formDisplay, String slotId) {
            if (hasColor(slotId))
                return true;
            return formDisplay.hasColor(slotId);
        }

        @SideOnly(Side.CLIENT)
        public int getFurColor(FormDisplay display, DBCData data) {
            if (hasColor(FUR))
                return getColor(FUR);
            return display.getFurColor(data);
        }

        @SideOnly(Side.CLIENT)
        public boolean hasHairColor(DBCData data, FormDisplay display) {
            if (data.Race == DBCRace.NAMEKIAN)
                return false;
            return hasColor(HAIR) || display.bodyColors.hasColor(HAIR);
        }
    }
}
