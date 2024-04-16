package kamkeel.npcdbc.data;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.data.PlayerData;

import java.util.HashMap;

/**
 * Store all player CustomForms Data here
 */
public class PlayerCustomFormData {
    public PlayerData parent;
    public int currentForm = 0;
    public int selectedForm = -1;
    public HashMap<Integer, String> unlockedForms = new HashMap<Integer, String>();

    public PlayerCustomFormData(PlayerData parent) {
        this.parent = parent;
    }

    public void addForm(CustomForm form) {
        if (!unlockedForms.containsKey(form.id)) {
            unlockedForms.put(form.id, form.name);
        }
    }

    public String removeForm(CustomForm form) {
        return unlockedForms.remove(form.id);
    }

    public String removeForm(int id) {
        return unlockedForms.remove(id);
    }

    public boolean hasForm(CustomForm form) {
        return unlockedForms.containsKey(form.id);
    }

    public boolean isInCustomForm() {
        return currentForm > 0;
    }

    public String getFormColorCode(CustomForm f) {
        // §2§lHi
        if (f.getMenuName().contains("§")) {
            String s = f.getMenuName();
            int i = s.indexOf("§");
            return s.substring(i, 2);
        }
        return "";
    }

    public boolean isInForm(String formName) {
        return getCurrentForm().getName().equals(formName);
    }

    public CustomForm getCurrentForm() {
        if (currentForm > 0)
            return (CustomForm) FormController.Instance.get(currentForm);

        return null;
    }

    public CustomForm getSelectedForm() {
        return (CustomForm) FormController.Instance.get(selectedForm);
    }

    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger("CurrentForm", currentForm);
        compound.setInteger("SelectedForm", selectedForm);
        compound.setTag("UnlockedForms", NBTTags.nbtIntegerStringMap(unlockedForms));
    }

    public void loadNBTData(NBTTagCompound compound) {
        currentForm = compound.getInteger("CurrentForm");
        selectedForm = compound.getInteger("SelectedForm");
        unlockedForms = NBTTags.getIntegerStringMap(compound.getTagList("UnlockedForms", 10));
    }

    public void updateClient() {
        ((IPlayerFormData) parent).updateFormInfo();
    }
}
