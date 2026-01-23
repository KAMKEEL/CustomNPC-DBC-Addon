package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.controllers.FormController;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

public class FormCustomStackable {
    private final Form parent;

    public HashMap<Integer, FormStack> formStacks;
    public boolean customStackable = false;

    public FormCustomStackable(Form parent) {
        this.parent = parent;
        formStacks = new HashMap<>();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound root) {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setBoolean("customStackable", customStackable);

        int i = 0;
        for (FormStack stack : formStacks.values()) {
            NBTTagCompound stackCompound = stack.writeToNBT();
            compound.setTag("stack" + i, stackCompound);
            i++;
        }

        root.setTag("stackableCustomForms", compound);
        return root;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound stackCompound = compound.getCompoundTag("stackableCustomForms");
        this.customStackable = stackCompound.getBoolean("customStackable");

        int i = 0;
        while (stackCompound.hasKey("stack" + i)) {
            FormStack stack = new FormStack(parent);
            stack.readFromNBT(stackCompound.getCompoundTag("stack" + i));
            this.formStacks.put(i, stack);
            i++;
        }
    }

    public void setFormStack(int id, FormStack stack) {
        if (id > 3 || id < 0)
            return;

        if (!compareStacks(stack))
            return;

        this.formStacks.put(id, stack);
    }

    public FormStack getFormStack(int id) {
        return this.formStacks.get(id);
    }

    public boolean compareStacks(FormStack newStack) {
        int newFrom = newStack.getFromID();

        for (FormStack existing : formStacks.values()) {
            if (existing.getFromID() == newFrom) {
                return false;
            }
        }

        return true;
    }

    public boolean isFromForm(Form form) {
        if (form == null) return false;

        for (FormStack stack : formStacks.values()) {
            if (stack.fromForm.id == form.id)
                return true;
        }

        return false;
    }

    public boolean isFromForm(int id) {
        if (FormController.Instance.has(id)) {
            return isFromForm((Form) FormController.Instance.get(id));
        }

        return false;
    }

    public FormCustomStackable save() {
        if (parent != null)
            parent.save();
        return this;
    }
}
