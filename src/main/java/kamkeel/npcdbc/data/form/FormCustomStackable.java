package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.controllers.FormController;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

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

        for (Map.Entry<Integer, FormStack> entry : formStacks.entrySet()) {
            NBTTagCompound stackCompound = entry.getValue().writeToNBT();
            compound.setTag("stack" + entry.getKey(), stackCompound);
        }

        root.setTag("stackableCustomForms", compound);
        return root;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound stackCompound = compound.getCompoundTag("stackableCustomForms");
        this.customStackable = stackCompound.getBoolean("customStackable");

        this.formStacks.clear();
        for (int i = 0; i < 4; i++) {
            if (stackCompound.hasKey("stack" + i)) {
                FormStack stack = new FormStack(parent);
                stack.readFromNBT(stackCompound.getCompoundTag("stack" + i));
                this.formStacks.put(i, stack);
            }
        }
    }

    public void setFormStack(int id, FormStack stack) {
        if (id > 3 || id < 0)
            return;

        if (stack.isEmpty()) {
            this.formStacks.remove(id);
            return;
        }

        // Remove entry at this slot before comparing so we don't
        // reject a stack that is replacing itself at the same slot
        FormStack old = this.formStacks.remove(id);
        if (!compareStacks(stack)) {
            if (old != null)
                this.formStacks.put(id, old);
            return;
        }

        this.formStacks.put(id, stack);
    }

    public FormStack getFormStack(int id) {
        return this.formStacks.get(id);
    }

    public boolean compareStacks(FormStack newStack) {
        int newFrom = newStack.getFromID();

        if (newFrom == -1)
            return true;

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
