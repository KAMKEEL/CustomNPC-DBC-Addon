package kamkeel.npcdbc.data.outline;

import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.OutlineController;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.util.ValueUtil;

public class Outline implements IOutline {
    public int id = -1;
    public String name, menuName;

    public Color innerColor = new Color(0x00ffff, 1), outerColor = new Color(0xffffff, 1);
    public float size = 1f, speed = 1f, noiseSize = 1f, colorSmoothness = 0.2f, colorInterpolation = 0.55f, pulsingSpeed = 0;

    public Outline() {
    }


    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setFloat("size", size);
        compound.setFloat("speed", speed);
        compound.setFloat("noiseSize", noiseSize);
        compound.setFloat("colorSmoothness", colorSmoothness);
        compound.setFloat("colorInterpolation", colorInterpolation);
        compound.setFloat("pulsingSpeed", pulsingSpeed);

        innerColor.writeToNBT(compound, "inner");
        outerColor.writeToNBT(compound, "outer");
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("ID"))
            id = compound.getInteger("ID");
        else if (AnimationController.Instance != null)
            id = FormController.Instance.getUnusedId();
        name = compound.getString("name");
        size = compound.getFloat("size");
        speed = compound.getFloat("speed");
        noiseSize = compound.getFloat("noiseSize");
        colorSmoothness = compound.getFloat("colorSmoothness");
        colorInterpolation = compound.getFloat("colorInterpolation");
        pulsingSpeed = compound.getFloat("pulsingSpeed");

        innerColor.readFromNBT(compound, "inner");
        outerColor.readFromNBT(compound, "outer");
    }

    @Override
    public void setInnerColor(int color, float alpha) {
        innerColor.setColor(color, ValueUtil.clamp(alpha, 0, 1));
    }

    @Override
    public void setOuterColor(int color, float alpha) {
        outerColor.setColor(color, ValueUtil.clamp(alpha, 0, 1));
    }

    @Override
    public Outline setSize(float size) {
        this.size = ValueUtil.clamp(size, 0, 5);
        return this;
    }

    @Override
    public Outline setNoiseSize(float size) {
        this.noiseSize = ValueUtil.clamp(size, 0, 50);
        return this;
    }

    @Override
    public Outline setSpeed(float speed) {
        this.speed = ValueUtil.clamp(size, 0, 50);
        return this;
    }

    @Override
    public Outline setPulsingSpeed(float speed) {
        this.pulsingSpeed = ValueUtil.clamp(speed, 0, 50);
        return this;
    }

    @Override
    public Outline setColorSmoothness(float smoothness) {
        this.colorSmoothness = ValueUtil.clamp(smoothness, 0, 1);
        return this;
    }

    @Override
    public Outline setColorInterpolation(float interp) {
        this.colorInterpolation = ValueUtil.clamp(interp, 0, 1);
        return this;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getMenuName() {
        return menuName;
    }

    @Override
    public void setMenuName(String name) {
        if (name.contains("&"))
            name = name.replace("&", "§");

        this.menuName = name;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int newID) {
        id = newID;
    }

    @Override
    public IOutline clone() {
        Outline outline = new Outline();
        outline.readFromNBT(writeToNBT());
        return outline;
    }

    @Override
    public IOutline save() {
        return OutlineController.Instance.saveOutline(this);
    }
}
