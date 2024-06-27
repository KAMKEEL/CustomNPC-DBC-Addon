package kamkeel.npcdbc.data.outline;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.OutlineController;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.AnimationController;

public class Outline implements IOutline {
    public int id;
    public String name, menuName;

    public int innerColor, outerColor;
    public float innerAlpha = 1f, outerAlpha = 1f, innerSize = 1f, outerSize = 1f;

    public Outline() {
    }

    public Outline(int innerColor, int outerColor) {
        this.innerColor = innerColor;
        this.outerColor = outerColor;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setInteger("innerColor", innerColor);
        compound.setInteger("outerColor", outerColor);

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("ID"))
            id = compound.getInteger("ID");
        else if (AnimationController.Instance != null)
            id = FormController.Instance.getUnusedId();

        name = compound.getString("name");
        outerColor = compound.getInteger("outerColor");
        innerColor = compound.getInteger("innerColor");

    }

    public void setColor() {
        innerColor = 0xff0000;
        outerColor = 0x0;
    }

    public Outline setAlpha(float inner, float outer) {
        if (inner > 0)
            innerAlpha = Math.min(inner, 1f);
        if (outer > 0)
            outerAlpha = Math.min(outer, 1f);
        return this;
    }

    public Outline setSize(float inner, float outer) {
        if (inner > 0)
            innerSize = Math.min(inner, 5f);
        if (outer > 0)
            outerSize = Math.min(outer, 5f);
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
    public IOutline save() {
        return OutlineController.Instance.saveOutline(this);
    }

    @Override
    public IOutline clone() {
        Outline outline = new Outline();
        outline.readFromNBT(writeToNBT());
        return outline;
    }
}
