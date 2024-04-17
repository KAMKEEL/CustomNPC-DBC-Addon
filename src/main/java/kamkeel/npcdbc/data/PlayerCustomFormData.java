package kamkeel.npcdbc.data;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.data.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Store all player CustomForms Data here
 */
public class PlayerCustomFormData {
    public PlayerData parent;
    public int currentForm = 0;
    public int selectedForm = -1;
    public HashMap<Integer, String> unlockedForms = new HashMap<Integer, String>();
    public HashMap<Integer, Integer> formMastery = new HashMap<Integer, Integer>();

    public PlayerCustomFormData(PlayerData parent) {
        this.parent = parent;
    }

    public void addForm(CustomForm form) {
        if (!unlockedForms.containsKey(form.id)) {
            unlockedForms.put(form.id, form.name);
            formMastery.put(form.id, 0);
        }
    }

    public String removeForm(CustomForm form) {
        formMastery.remove(form.id);
        return unlockedForms.remove(form.id);
    }

    public String removeForm(int id) {
        formMastery.remove(id);
        return unlockedForms.remove(id);
    }

    public boolean hasForm(CustomForm form) {
        return unlockedForms.containsKey(form.id);
    }

    public boolean isInCustomForm() {
        return currentForm > 0 && getCurrentForm() != null;
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

    public String getColoredName(CustomForm f) {
        return getFormColorCode(f) + f.getName();
    }

    public boolean isInForm(String formName) {
        return getCurrentForm().getName().equals(formName);
    }

    public CustomForm getCurrentForm() {
        if (currentForm > 0)
            return (CustomForm) FormController.Instance.get(currentForm);


        return null;
    }

    public List<String> getAllForms() {
        List<String> list = new ArrayList<>();
        for (Integer id : unlockedForms.keySet()) {
            CustomForm f = getUnlockedForm(id);
            if (!list.contains(getColoredName(f)))
                list.add(getColoredName(f));

        }
        return list;
    }

    public CustomForm getUnlockedForm(int id) {
        if (unlockedForms.containsKey(id))
            return (CustomForm) FormController.Instance.get(unlockedForms.get(id));
        return null;
    }

    public CustomForm getSelectedForm() {
        return (CustomForm) FormController.Instance.get(selectedForm);
    }

    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger("CurrentForm", currentForm);
        compound.setInteger("SelectedForm", selectedForm);
        compound.setTag("UnlockedForms", NBTTags.nbtIntegerStringMap(unlockedForms));
        compound.setTag("FormMastery", NBTTags.nbtIntegerIntegerMap(formMastery));
    }

    public void loadNBTData(NBTTagCompound compound) {
        currentForm = compound.getInteger("CurrentForm");
        selectedForm = compound.getInteger("SelectedForm");
        unlockedForms = NBTTags.getIntegerStringMap(compound.getTagList("UnlockedForms", 10));
        formMastery = NBTTags.getIntegerIntegerMap(compound.getTagList("FormMastery", 10));
    }

    public void updateClient() {
        ((IPlayerFormData) parent).updateFormInfo();
    }

    public void addFormMastery(int formID, int amount) {
        CustomForm form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            int current = formMastery.get(formID);
            int updated = Math.max(current + amount, form.maxMastery);
            formMastery.put(formID, updated);
        }
    }

    public void removeFormMastery(int formID, int amount) {
        CustomForm form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            int current = formMastery.get(formID);
            int updated = Math.max(current - amount, 0);
            formMastery.put(formID, updated);
        }
    }
}
