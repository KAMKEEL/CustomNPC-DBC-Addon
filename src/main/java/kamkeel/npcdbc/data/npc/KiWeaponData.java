package kamkeel.npcdbc.data.npc;

import kamkeel.npcdbc.api.npc.IKiWeaponData;
import net.minecraft.nbt.NBTTagCompound;

public class KiWeaponData implements IKiWeaponData {
    public boolean isEnabled;
    public byte weaponType;
    public int color = -1;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public float scaleX;
    public float scaleY;
    public float scaleZ;
    public int damage;

    public void readFromNBT(NBTTagCompound compound){
        isEnabled = compound.getBoolean("isEnabled");
        weaponType = compound.getByte("weaponType");
        color = compound.getInteger("color");
        offsetX = compound.getFloat("offsetX");
        offsetY = compound.getFloat("offsetY");
        offsetZ = compound.getFloat("offsetZ");
        scaleX = compound.getFloat("scaleX");
        scaleY = compound.getFloat("scaleY");
        scaleZ = compound.getFloat("scaleZ");
        damage = compound.getInteger("damage");

    }

    public void saveToNBT(NBTTagCompound compound){
        compound.setBoolean("isEnabled", isEnabled);

        // Not doing a `if(isEnabled)` check because I want this to persist if players want to
        // make it toggleable in a fight without losing color/scale data
        compound.setByte("weaponType", weaponType);
        compound.setInteger("color", color);
        compound.setFloat("offsetX", offsetX);
        compound.setFloat("offsetY", offsetY);
        compound.setFloat("offsetZ", offsetZ);
        compound.setFloat("scaleX", scaleX);
        compound.setFloat("scaleY", scaleY);
        compound.setFloat("scaleZ", scaleZ);
        compound.setInteger("damage", damage);
    }


    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public void setWeaponType(int type) {
        if(type != 0 && type != 1)
            return;
        this.weaponType = (byte) type;
    }

    @Override
    public int getWeaponType() {
        return 0;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getColor(){
        return this.color;
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
        if(scale < 0)
            scale = 0;
        this.scaleX = scale;
    }

    @Override
    public void setYScale(float scale) {
        if(scale < 0)
            scale = 0;
        this.scaleY = scale;
    }

    @Override
    public void setZScale(float scale) {
        if(scale < 0)
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

    @Override
    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public int getDamage() {
        return this.damage;
    }
}
