package kamkeel.npcdbc.data.npc;

import kamkeel.npcdbc.api.npc.IDBCDisplay;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

public class DBCDisplay implements IDBCDisplay {

    public boolean enabled = false;

    public String hairCode = "", hairType = "";

    public int hairColor, eyeColor, bodyCM = 16297621, bodyC1, bodyC2, bodyC3, furColor;

    public int race = 4, rage;

    public int noseType = 1, mouthType = 1, eyeType = 0;
    private EnumAuraTypes enumAuraTypes = EnumAuraTypes.None;

    public NBTTagCompound writeToNBT(NBTTagCompound comp) {
        comp.setBoolean("DBCDisplayEnabled", enabled);
        if (enabled) {
            comp.setInteger("race", race);

            comp.setString("DBCHairType", hairType);
            comp.setString("DBCHair", hairCode);
            comp.setInteger("DBCHairColor", hairColor);

            comp.setInteger("DBCEyeColor", eyeColor);
            comp.setInteger("furColor", furColor);
            comp.setInteger("bodyCM", bodyCM);
            comp.setInteger("bodyC1", bodyC1);
            comp.setInteger("bodyC2", bodyC2);
            comp.setInteger("bodyC3", bodyC3);

            comp.setInteger("DBCDisplayAura", enumAuraTypes.ordinal());


        }
        return comp;
    }

    public void readFromNBT(NBTTagCompound comp) {
        enabled = comp.getBoolean("DBCDisplayEnabled");
        if (enabled) {
            race = comp.getInteger("race");

            hairType = comp.getString("DBCHairType");
            hairCode = comp.getString("DBCHair");
            hairColor = comp.getInteger("DBCHairColor");

            eyeColor = comp.getInteger("DBCEyeColor");
            furColor = comp.getInteger("furColor");
            bodyCM = comp.getInteger("bodyCM");
            bodyC1 = comp.getInteger("bodyC1");
            bodyC2 = comp.getInteger("bodyC2");
            bodyC3 = comp.getInteger("bodyC3");

            enumAuraTypes = EnumAuraTypes.values()[comp.getInteger("DBCDisplayAura") % EnumAuraTypes.values().length];

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
    public int getHairColor() {
        return hairColor;
    }

    @Override
    public void setHairColor(int hairColor) {
        this.hairColor = hairColor;
    }

    @Override
    public int getEyeColor() {
        return eyeColor;
    }

    @Override
    public void setEyeColor(int eyeColor) {
        this.eyeColor = eyeColor;
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
    public String getHairType(String type) {
        String s = type.toLowerCase();
        if (s.equals("base") || s.equals("ssj") || s.equals("ssj2") || s.equals("ssj3") || s.equals("ssj4") || s.equals("oozaru") || s.equals(""))
            return hairType;
        else
            throw new CustomNPCsException("Invalid type!");
    }
}
