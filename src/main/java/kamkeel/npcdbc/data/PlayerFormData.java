package kamkeel.npcdbc.data;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormMastery;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.data.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Store all player Addon Form Data here
 */
public class PlayerFormData {
    public PlayerData parent;
    public int currentForm = -1;
    public int selectedForm = -1;
    public HashMap<Integer, String> unlockedForms = new HashMap<Integer, String>();
    public HashMap<Integer, Float> formLevels = new HashMap<Integer, Float>();

    public PlayerFormData(PlayerData parent) {
        this.parent = parent;
    }

    public void addForm(Form form) {
        if (!unlockedForms.containsKey(form.id)) {
            unlockedForms.put(form.id, form.name);
            formLevels.put(form.id, 0f);
        }
    }

    public boolean hasUnlocked(int id) {
        return unlockedForms.containsKey(id);
    }

    public String removeForm(Form form) {
        formLevels.remove(form.id);
        return unlockedForms.remove(form.id);
    }

    public String removeForm(int id) {
        formLevels.remove(id);
        return unlockedForms.remove(id);
    }

    public boolean hasSelectedForm() {
        return selectedForm > -1 && getSelectedForm() != null;
    }

    public boolean hasForm(Form form) {
        return unlockedForms.containsKey(form.id);
    }

    public boolean isInCustomForm() {
        return currentForm > -1 && getCurrentForm() != null;
    }

    public String getFormColorCode(Form f) {
        // §2§lHi
        if (f.getMenuName().contains("§")) {
            String s = f.getMenuName();
            int i = s.indexOf("§");
            return s.substring(i, 2);
        }
        return "";
    }

    public String getColoredName(Form f) {
        return getFormColorCode(f) + f.getName();
    }

    public boolean isInForm(String formName) {
        return getCurrentForm().getName().equals(formName);
    }

    public Form getCurrentForm() {
        if (currentForm > 0)
            return (Form) FormController.Instance.get(currentForm);
        return null;
    }

    public List<String> getAllForms() {
        List<String> list = new ArrayList<>();
        for (Integer id : unlockedForms.keySet()) {
            Form f = getUnlockedForm(id);
            if (!list.contains(getColoredName(f)))
                list.add(getColoredName(f));

        }
        return list;
    }

    public Form getUnlockedForm(int id) {
        if (unlockedForms.containsKey(id))
            return (Form) FormController.Instance.get(unlockedForms.get(id));
        return null;
    }

    public Form getSelectedForm() {
        return (Form) FormController.Instance.get(selectedForm);
    }

    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger("CurrentForm", currentForm);
        compound.setInteger("SelectedForm", selectedForm);
        compound.setTag("UnlockedForms", NBTTags.nbtIntegerStringMap(unlockedForms));
        compound.setTag("FormMastery", NBTTags.nbtIntegerFloatMap(formLevels));
    }

    public void loadNBTData(NBTTagCompound compound) {
        currentForm = compound.getInteger("CurrentForm");
        selectedForm = compound.getInteger("SelectedForm");
        unlockedForms = NBTTags.getIntegerStringMap(compound.getTagList("UnlockedForms", 10));
        formLevels = NBTTags.getIntegerFloatMap(compound.getTagList("FormMastery", 10));
    }

    public void updateClient() {
        ((IPlayerFormData) parent).updateFormInfo();
    }

    public void updateCurrentFormMastery(String gainType) {
        updateFormMastery(currentForm, gainType);
    }

    public void updateFormMastery(int formID, String gainType) {
        Form f = FormController.getInstance().customForms.get(formID);
        if (f == null || !isInCustomForm() || parent.player == null)
            return;

        DBCData data = DBCData.get(parent.player);
        if (data == null)
            return;
        FormMastery fm = (FormMastery) f.getMastery();
        float playerLevel = formLevels.get(f.id);
        float fullGain = fm.calculateFullGain(gainType, playerLevel, data.MND);

        playerLevel = Math.min(playerLevel + fullGain, fm.maxLevel); //updated level
        formLevels.put(f.id, playerLevel);
        updateClient();
    }

    public void addFormMastery(int formID, int amount) {
        Form form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            float current = formLevels.get(formID);
            float updated = Math.min(current + amount, ((FormMastery) form.getMastery()).maxLevel);
            formLevels.put(formID, updated);
            updateClient();
        }
    }

    public void removeFormMastery(int formID, int amount) {
        Form form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            float current = formLevels.get(formID);
            float updated = Math.max(current - amount, 0);
            formLevels.put(formID, updated);
            updateClient();
        }
    }

    public float getFormLevel(int formID) {
        if (formID != -1 && formLevels.containsKey(formID))
            return formLevels.get(formID);
        return 0f;
    }

    public float getCurrentLevel() {
        return getFormLevel(currentForm);
    }

    public void resetAll() {
        TransformController.handleFormDescend((EntityPlayerMP) parent.player);
        currentForm = -1;
        selectedForm = -1;
        unlockedForms = new HashMap();
        formLevels = new HashMap();
        updateClient();
    }
}
