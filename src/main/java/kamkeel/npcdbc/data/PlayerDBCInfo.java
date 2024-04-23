package kamkeel.npcdbc.data;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormMastery;
import kamkeel.npcdbc.mixin.IPlayerDBCInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.data.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Store all player Addon DBC Info here
 */
public class PlayerDBCInfo {
    public PlayerData parent;
    public int currentForm = -1;
    public int selectedForm = -1;

    public int currentAura = -1;
    public int selectedAura = -1;
    public HashSet<Integer> unlockedAuras = new HashSet<Integer>();

    public HashSet<Integer> unlockedForms = new HashSet<Integer>();
    public HashMap<Integer, Float> formLevels = new HashMap<Integer, Float>();
    public HashMap<Integer, Integer> formTimers = new HashMap<>();

    public PlayerDBCInfo(PlayerData parent) {
        this.parent = parent;
    }

    public void addForm(Form form) {
        if (!unlockedForms.contains(form.id)) {
            unlockedForms.add(form.id);
            formLevels.put(form.id, 0f);
        }
    }

    public boolean hasUnlocked(int id) {
        return unlockedForms.contains(id);
    }

    public boolean removeForm(Form form) {
        formLevels.remove(form.id);
        return unlockedForms.remove(form.id);
    }

    public boolean removeForm(int id) {
        formLevels.remove(id);
        return unlockedForms.remove(id);
    }

    public Form getForm(int id) {
        if (unlockedForms.contains(id))
            return (Form) FormController.getInstance().get(id);

        return null;
    }

    public boolean hasSelectedForm() {
        return selectedForm > -1 && getSelectedForm() != null;
    }

    public boolean hasForm(Form form) {
        return unlockedForms.contains(form.id);
    }

    public boolean isInCustomForm() {
        return currentForm > -1 && getCurrentForm() != null;
    }

    public String getFormColorCode(Form f) {
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

    public Form getUnlockedForm(int id) {
        if (unlockedForms.contains(id))
            return (Form) FormController.Instance.get(id);
        return null;
    }

    public Form getSelectedForm() {
        return (Form) FormController.Instance.get(selectedForm);
    }


    public void updateClient() {
        ((IPlayerDBCInfo) parent).updateDBCInfo();
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

    public void addFormLevel(int formID, float amount) {
        Form form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            float current = formLevels.get(formID);
            float updated = Math.min(current + amount, ((FormMastery) form.getMastery()).maxLevel);
            formLevels.put(formID, updated);
            updateClient();
        }
    }

    public void setFormLevel(int formID, float amount) {
        Form form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            float updated = Math.min(amount, ((FormMastery) form.getMastery()).maxLevel);
            formLevels.put(formID, updated);
            updateClient();
        }
    }

    public void removeFormMastery(int formID) {
        Form form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            formLevels.remove(formID);
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
        unlockedForms = new HashSet<>();
        formLevels = new HashMap();
        updateClient();
    }

    public void addTimer(int formid, int timeInTicks) {
        if (!formTimers.containsKey(formid))
            formTimers.put(formid, timeInTicks);

        formTimers.replace(formid, timeInTicks);

    }

    public void decrementTimer(int formid) {
        if (formTimers.containsKey(formid)) {
            int currentTime = formTimers.get(formid);
            if (currentTime > 0)
                formTimers.replace(formid, currentTime - 1);
            else if (currentTime == 0) {
                TransformController.handleFormDescend(parent.player);
                formTimers.remove(formid);
            }
        }
    }

    public int getTimer(int formid) {
        if (formTimers.containsKey(formid))
            return formTimers.get(formid);
        return -1;

    }

    public boolean hasTimer(int formid) {
        if (formTimers.containsKey(formid))
            return formTimers.get(formid) > -1;
        return false;

    }

    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger("CurrentForm", currentForm);
        compound.setInteger("SelectedForm", selectedForm);
        compound.setTag("UnlockedForms", NBTTags.nbtIntegerSet(unlockedForms));
        compound.setTag("FormMastery", NBTTags.nbtIntegerFloatMap(formLevels));
        compound.setTag("FormTimers", NBTTags.nbtIntegerIntegerMap(formTimers));


        compound.setInteger("CurrentAura", currentAura);
        compound.setInteger("SelectedAura", selectedAura);
        compound.setTag("UnlockedAuras", NBTTags.nbtIntegerSet(unlockedAuras));
    }

    public void loadNBTData(NBTTagCompound compound) {
        currentForm = compound.getInteger("CurrentForm");
        selectedForm = compound.getInteger("SelectedForm");
        unlockedForms = NBTTags.getIntegerSet(compound.getTagList("UnlockedForms", 10));
        formLevels = NBTTags.getIntegerFloatMap(compound.getTagList("FormMastery", 10));
        formTimers = NBTTags.getIntegerIntegerMap(compound.getTagList("FormTimers", 10));

        currentAura = compound.getInteger("CurrentAura");
        selectedAura = compound.getInteger("SelectedAura");
        unlockedAuras = NBTTags.getIntegerSet(compound.getTagList("UnlockedAuras", 10));
    }
}
