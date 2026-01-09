package kamkeel.npcdbc.data.npc;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.api.npc.IKiWeaponData;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import net.minecraft.nbt.NBTTagCompound;

public class KiWeaponData implements IKiWeaponData {
    public byte weaponType;

    public Color color = new Color(-1, 0.6f);

    public float offsetX, offsetY, offsetZ;
    public float scaleX = 1, scaleY = 1, scaleZ = 1;

    public void readFromNBT(NBTTagCompound compound, String name) {
        NBTTagCompound weapon = compound.getCompoundTag(name);
        weaponType = weapon.getByte("weaponType");
        color.readFromNBT(weapon, "color");
        offsetX = weapon.getFloat("offsetX");
        offsetY = weapon.getFloat("offsetY");
        offsetZ = weapon.getFloat("offsetZ");

        scaleX = weapon.getFloat("scaleX");
        scaleY = weapon.getFloat("scaleY");
        scaleZ = weapon.getFloat("scaleZ");
    }

    public void saveToNBT(NBTTagCompound comp, String name) {
        NBTTagCompound weapon = new NBTTagCompound();

        // Not doing a `if(isEnabled)` check because I want this to persist if players want to
        // make it toggleable in a fight without losing color/scale data
        weapon.setByte("weaponType", weaponType);
        color.writeToNBT(weapon, "color");
        weapon.setFloat("offsetX", offsetX);
        weapon.setFloat("offsetY", offsetY);
        weapon.setFloat("offsetZ", offsetZ);

        NBTTagCompound scale = new NBTTagCompound();
        weapon.setFloat("scaleX", scaleX);
        weapon.setFloat("scaleY", scaleY);
        weapon.setFloat("scaleZ", scaleZ);

//        weapon.setTag("scale", scale);

        comp.setTag(name, weapon);
    }


    @Override
    public boolean isEnabled() {
        return weaponType != 0;
    }

    @Override
    public void setWeaponType(int type) {
        if (type < 0 || type > 2)
            return;

        this.weaponType = (byte) type;
    }

    @Override
    public int getWeaponType() {
        return weaponType;
    }

    @Override
    public void setColor(int color, float alpha) {
        this.color.setColor(color, alpha);
    }

    @Override
    public int getColor() {
        return color.color;
    }

    @Override
    public void setXOffset(float offset) {
        this.offsetX = offset;
    }

    @Override
    public void setYOffset(float offset) {
        this.offsetY = offset;
    }

    @Override
    public void setZOffset(float offset) {
        this.offsetZ = offset;
    }

    @Override
    public float getXOffset() {
        return this.offsetX;
    }

    @Override
    public float getYOffset() {
        return this.offsetY;
    }

    @Override
    public float getZOffset() {
        return this.offsetZ;
    }

    @Override
    public void setXScale(float scale) {
        if (scale < 0)
            scale = 0;
        this.scaleX = scale;
    }

    @Override
    public void setYScale(float scale) {
        if (scale < 0)
            scale = 0;
        this.scaleY = scale;
    }

    @Override
    public void setZScale(float scale) {
        if (scale < 0)
            scale = 0;
        this.scaleZ = scale;
    }

    @Override
    public float getXScale() {
        return this.scaleX;
    }

    @Override
    public float getYScale() {
        return this.scaleY;
    }

    @Override
    public float getZScale() {
        return this.scaleZ;
    }

    public static int getColorByAuraType(AuraDisplay auraDisplay) {
        if (auraDisplay == null)
            return getColorByAuraTypeName("");

        if (auraDisplay.color1 == -1) {
            if (auraDisplay.type != EnumAuraTypes3D.None && auraDisplay.type != EnumAuraTypes3D.Base) {
                return getColorByAuraTypeName(auraDisplay.type.getName());
            }

            return getColorByAuraTypeName(auraDisplay.type2D.getName());

        }
        return auraDisplay.color1;
    }

    public static int getColorByAuraTypeName(String name) {
        switch (name) {
            case "ssgod":
                return 0xFFC125;
            case "ssb":
                return 0x2ACDEE;
            case "shinka":
                return 0x007FFF;
            case "ssrose":
                return 0x730015;
            case "ssroseevo":
                return 0xD6164C;
            case "ultimate":
                return 0xFAB513;
            case "mui":
            case "ui":
                return 0xF0F0F0;
            case "godofdestructiontoppo":
            case "godofdestruction":
                return 0xBE32CF;
            case "jiren":
                return 0xDD0000;
            default:
                return 0xA8FFFF;
        }
    }
}
