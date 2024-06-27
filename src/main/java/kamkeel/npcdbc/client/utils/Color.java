package kamkeel.npcdbc.client.utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

public class Color {
    public int color;
    public float alpha;

    public Color(int color, float alpha) {
        setColor(color, alpha);
    }

    public void setColor(int color, float alpha) {
        this.color = color;
        this.alpha = alpha;
    }


    @SideOnly(Side.CLIENT)
    public void glColor() {
        GL11.glColor4f(getRedF(), getGreenF(), getBlueF(), alpha);
    }

    @SideOnly(Side.CLIENT)
    public void uniform() {
        ShaderHelper.uniformColor("outerColor", color, alpha);

    }
    public NBTTagCompound writeToNBT(NBTTagCompound compound, String name) {
        compound.setInteger(name + "Color", color);
        compound.setFloat(name + "Alpha", alpha);

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound, String name) {
        setColor(compound.getInteger(name + "color"), compound.getFloat(name + "alpha"));
    }

    public int getRed() {
        return (color >> 16 & 0xFF);
    }

    public float getRedF() {
        return (float) (color >> 16 & 0xFF) / 255f;
    }

    public int getGreen() {
        return (color >> 8 & 0xFF);
    }

    public float getGreenF() {
        return (float) (color >> 8 & 0xFF) / 255f;
    }

    public int getBlue() {
        return (color & 0xFF);
    }

    public float getBlueF() {
        return (float) (color & 0xFF) / 255f;
    }

}
