package kamkeel.npcdbc.data;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormKey;
import net.minecraft.nbt.NBTTagCompound;

public class FormWheelData {
    public int slot = -1, formID = -1; //formID now only used for DBC forms
    public boolean isDBC;
    public String formKey = ""; // All customs are now migrated fully to formKey 

    public FormWheelData() {
    }

    public FormWheelData(int slot) {
        this.slot = slot;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound wheel = new NBTTagCompound();
        wheel.setInteger("slot", slot);
        wheel.setInteger("formID", formID);
        wheel.setBoolean("isDBC", isDBC);
        if (!isDBC && !formKey.isEmpty())
            wheel.setString("formKey", formKey);

        compound.setTag("FormWheel" + slot, wheel);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        formID = compound.getInteger("formID");
        isDBC = compound.getBoolean("isDBC");
        if (compound.hasKey("formKey")) {
            formKey = compound.getString("formKey");
        } else if (!isDBC && formID != -1) { //Migrate old formID to formKey
            Form form = (Form) FormController.Instance.get(formID);
            if (form != null)
                formKey = form.key != null ? form.key.toString() : FormKey.custom(form.name).toString();
            else
                formKey = "";
            formID = -1;
        } else {
            formKey = "";
        }
    }

    public void reset() {
        formID = -1;
        isDBC = false;
        formKey = "";
    }
}
