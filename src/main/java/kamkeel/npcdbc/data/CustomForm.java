package kamkeel.npcdbc.data;

import kamkeel.npcdbc.api.ICustomForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.FormController;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.AnimationController;

public class CustomForm implements ICustomForm {

    public int id = -1; // Only for internal usage
    public String name;
    public int race = DBCRace.HUMAN;
    public float allMulti = 1.0f;

    public float strengthMulti = 1.0f;
    public float dexMulti = 1.0f;
    public float willMulti = 1.0f;

    public boolean kaiokenStackable = true, uiStackable = true;
    public float kaiokenMulti = 1.0f, UIMulti = 1.0f;

    public int auraColor = 1;


    public CustomForm(){}

    public CustomForm(int id, String name) {
        this.id = id;
        this.name = name;
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
    public int getRace() {
        return race;
    }

    @Override
    public void setRace(int race) {
        this.race = race;
    }

    @Override
    public float getAllMulti() {
        return allMulti;
    }

    @Override
    public void setAllMulti(float allMulti) {
        this.allMulti = allMulti;
    }

    @Override
    public void setAttributeMulti(int id, float multi) {
        switch (id) {
            case 0:
                strengthMulti = multi;
                break;
            case 1:
                dexMulti = multi;
                break;
            case 3:
                willMulti = multi;
                break;

        }
    }

    @Override
    public float getAttributeMulti(int id) {
        switch (id) {
            case 0:
                return strengthMulti;
            case 1:
                return dexMulti;
            case 3:
                return willMulti;

        }
        return 1.0f;
    }


    @Override
    public boolean isKaiokenStackable() {
        return kaiokenStackable;
    }


    @Override
    public boolean isUIStackable() {
        return uiStackable;
    }

    @Override
    public void stackKaioken(boolean stackKaioken) {
        kaiokenStackable = stackKaioken;
    }

    @Override
    public void stackUI(boolean stackUI) {
        uiStackable = stackUI;
    }

    @Override
    public float getKaiokenMulti() {
        return kaiokenMulti;
    }

    @Override
    public void setKaiokenMulti(float kaiokenMulti) {
        this.kaiokenMulti = kaiokenMulti;
    }

    @Override
    public float getUIMulti() {
        return UIMulti;
    }

    @Override
    public void setUIMulti(float UIMulti) {
        this.UIMulti = UIMulti;
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
    public int getID() {
        return id;
    }

    @Override
    public void setID(int newID) {
        id = newID;
    }

    public void readFromNBT(NBTTagCompound compound){
        if(compound.hasKey("ID")){
            id = compound.getInteger("ID");
        }
        else if (AnimationController.Instance != null) {
            id = FormController.Instance.getUnusedId();
        }
        name = compound.getString("Name");

        // ADD REST

    }

    public NBTTagCompound writeToNBT(){
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("Name", name);

        // ADD REST


        return compound;
    }

    @Override
    public ICustomForm save() {
        return FormController.Instance.saveForm(this);
    }
}
