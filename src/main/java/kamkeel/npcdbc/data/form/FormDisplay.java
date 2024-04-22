package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.api.form.IFormDisplay;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.scripted.CustomNPCsException;

public class FormDisplay implements IFormDisplay {

    private Form parent;

    public float formSize = 1.0f;

    public String hairCode = "";
    public String hairType = "";
    public int hairColor = -1;

    public int eyeColor = -1;
    public int auraColor = -1;

    public String bodyType = "";
    public int bodyCM = -1, bodyC1 = -1, bodyC2 = -1, bodyC3 = -1, furColor = -1;
    public boolean hasMask = false;

    public FormDisplay(Form parent) {
        this.parent = parent;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound rendering = compound.getCompoundTag("rendering");
        auraColor = rendering.getInteger("auraColor");
        eyeColor = rendering.getInteger("eyeColor");
        hairColor = rendering.getInteger("hairColor");
        furColor = rendering.getInteger("furColor");
        hairCode = rendering.getString("hairCode");
        hairType = rendering.getString("hairType");
        bodyType = rendering.getString("bodyType");
        bodyCM = rendering.getInteger("bodyCM");
        bodyC1 = rendering.getInteger("bodyC1");
        bodyC2 = rendering.getInteger("bodyC2");
        bodyC3 = rendering.getInteger("bodyC3");
        hasMask = rendering.getBoolean("hasMask");
        formSize = rendering.getFloat("formSize");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound rendering = new NBTTagCompound();
        compound.setTag("rendering", rendering);
        rendering.setInteger("auraColor", auraColor);
        rendering.setInteger("eyeColor", eyeColor);
        rendering.setInteger("hairColor", hairColor);
        rendering.setString("hairCode", hairCode);
        rendering.setString("hairType", hairType);
        rendering.setString("bodyType", bodyType);
        rendering.setInteger("furColor", furColor);
        rendering.setInteger("bodyCM", bodyCM);
        rendering.setInteger("bodyC1", bodyC1);
        rendering.setInteger("bodyC2", bodyC2);
        rendering.setInteger("bodyC3", bodyC3);
        rendering.setBoolean("hasMask", hasMask);
        rendering.setFloat("formSize", formSize);
        return rendering;
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
    public float getSize() {
        return formSize;
    }

    @Override
    public void setSize(float size) {
        formSize = Math.max(Math.min(size, 10), 0.05f);
    }

    @Override
    public boolean hasSize() {
        return formSize != 1f;
    }

    @Override
    public boolean hasColor(String type) {
        switch (type.toLowerCase()) {
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
        throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur");
    }

    @Override
    public void setColor(String type, int color) {
        switch (type.toLowerCase()) {
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
                throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur");
        }
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
    public int getNameColor() {
        if (hairType.equals("ssj4") || hairType.equals("oozaru"))
            return furColor;
        else
            return hairColor;
    }

    @Override
    public String getHairType(String type) {
        String s = type.toLowerCase();
        if (s.equals("base") || s.equals("ssj") || s.equals("ssj2") || s.equals("ssj3") || s.equals("ssj4") || s.equals("oozaru") || s.equals(""))
            return hairType;
        else
            throw new CustomNPCsException("Invalid type!");
    }

    @Override
    public int getColor(String type) {
        switch (type.toLowerCase()) {
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
        throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur");
    }

    @Override
    public int getAuraColor() {
        return auraColor;
    }

    @Override
    public void setAuraColor(int auraColor) {
        this.auraColor = auraColor;
    }

    @Override
    public boolean hasMask() {
        return hasMask;
    }

    @Override
    public void hasMask(boolean hasMask) {
        this.hasMask = hasMask;
    }


    public IFormDisplay save() {
        if (parent != null)
            parent.save();
        return this;
    }
}
