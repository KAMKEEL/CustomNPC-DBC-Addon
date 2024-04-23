package kamkeel.npcdbc.data.aura;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.AnimationController;

public class Aura implements IAura {
    public String name, menuName;
    public int id;

    public AuraDisplay display = new AuraDisplay(this);

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setString("menuName", menuName);

        display.writeToNBT(compound);


        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("ID"))
            id = compound.getInteger("ID");
        else if (AnimationController.Instance != null)
            id = FormController.Instance.getUnusedId();

        name = compound.getString("name");
        menuName = compound.getString("menuName");

        display.readFromNBT(compound);

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
    public IAura save() {
        return AuraController.Instance.saveAura(this);
    }


}
