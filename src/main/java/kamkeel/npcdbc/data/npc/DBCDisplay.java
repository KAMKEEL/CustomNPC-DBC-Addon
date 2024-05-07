package kamkeel.npcdbc.data.npc;

import kamkeel.npcdbc.api.npc.IDBCDisplay;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes;
import net.minecraft.nbt.NBTTagCompound;

public class DBCDisplay implements IDBCDisplay {

    public boolean enabled = false;
    private EnumAuraTypes enumAuraTypes = EnumAuraTypes.None;

    private String hairCode = "";
    private int hairColor = 0x0, eyeColor = 0;


    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("DBCDisplayEnabled", enabled);
        if (enabled) {
            nbttagcompound.setInteger("DBCDisplayAura", enumAuraTypes.ordinal());

            nbttagcompound.setString("DBCHair", hairCode);
            nbttagcompound.setInteger("DBCHairColor", hairColor);

            nbttagcompound.setInteger("DBCEyeColor", eyeColor);

        }
        return nbttagcompound;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        enabled = nbttagcompound.getBoolean("DBCDisplayEnabled");
        if (enabled) {
            enumAuraTypes = EnumAuraTypes.values()[nbttagcompound.getInteger("DBCDisplayAura") % EnumAuraTypes.values().length];

            hairCode = nbttagcompound.getString("DBCHair");
            hairColor = nbttagcompound.getInteger("DBCHairColor");

            eyeColor = nbttagcompound.getInteger("DBCEyeColor");
        }
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
}
