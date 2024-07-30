package kamkeel.npcdbc.data;

import kamkeel.npcdbc.controllers.*;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormMastery;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.util.ValueUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
    private HashMap<Integer, Integer> formWheel = new HashMap<>();

    public PlayerDBCInfo(PlayerData parent) {
        this.parent = parent;
    }

    public void addForm(Form form) {
        if (!unlockedForms.contains(form.id)) {
            unlockedForms.add(form.id);
            formLevels.put(form.id, 0f);
        }
    }

    public void addFormWheel(int wheelSlot, int formID) {
        if (wheelSlot > 5)
            return;
        formWheel.put(wheelSlot, formID);
    }

    public boolean hasFormUnlocked(int id) {
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

    public boolean removeFormWheel(int wheelSlot) {
        if (wheelSlot > 5)
            return false;
        return formWheel.remove(wheelSlot, -1);
    }

    public Form getWheelSlot(int wheelSlot) {
        return (Form) FormController.getInstance().get(formWheel.get(wheelSlot));
    }

    public Form getForm(int id) {
        if (unlockedForms.contains(id))
            return (Form) FormController.getInstance().get(id);

        return null;
    }

    public Map getFormWheel() {
        return formWheel;
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

    public boolean isInForm(int formID) {
        return formID == currentForm;
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

    public void clearAllForms() {
        TransformController.handleFormDescend(parent.player, -10);
        currentForm = -1;
        selectedForm = -1;
        unlockedForms.clear();
        formLevels.clear();
    }

    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    // Form mastery stuff
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
        if (!formLevels.containsKey(f.id))
            return;

        float playerLevel = formLevels.get(f.id);
        float fullGain = fm.calculateFullGain(gainType, playerLevel, data.MND);

        playerLevel = ValueUtil.clamp(playerLevel + fullGain, 0, fm.maxLevel); //updated level
        formLevels.replace(f.id, playerLevel);
        updateClient();
    }

    public void addFormLevel(int formID, float amount) {
        Form form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            float current = formLevels.get(formID);
            float updated = ValueUtil.clamp(current + amount, 0, ((FormMastery) form.getMastery()).maxLevel);
            formLevels.put(formID, updated);
            updateClient();
        }
    }

    public void setFormLevel(int formID, float amount) {
        Form form = FormController.getInstance().customForms.get(formID);
        if (form != null) {
            float updated = ValueUtil.clamp(amount, 0, ((FormMastery) form.getMastery()).maxLevel);
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

    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    // Form timer stuff
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
                TransformController.handleFormDescend(parent.player, 0);
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


    ///////////////////////////////////////////
    ///////////////////////////////////////////
    // Aura stuff

    public void addAura(Aura Aura) {
        if (!unlockedAuras.contains(Aura.id)) {
            unlockedAuras.add(Aura.id);
        }
    }

    public boolean hasAuraUnlocked(int id) {
        return unlockedAuras.contains(id);
    }

    public boolean removeAura(Aura Aura) {
        return unlockedAuras.remove(Aura.id);
    }

    public boolean removeAura(int id) {
        return unlockedAuras.remove(id);
    }

    public Aura getAura(int id) {
        if (unlockedAuras.contains(id))
            return (Aura) AuraController.getInstance().get(id);

        return null;
    }

    public boolean hasSelectedAura() {
        return selectedAura > -1 && getSelectedAura() != null;
    }

    public boolean hasAura(Aura Aura) {
        return unlockedAuras.contains(Aura.id);
    }

    public boolean isInCustomAura() {
        return currentAura > -1 && getCurrentAura() != null;
    }

    public boolean isInAura(String AuraName) {
        return getCurrentAura().getName().equals(AuraName);
    }

    public Aura getCurrentAura() {
        Form form = getCurrentForm();

        if (form != null && form.display.hasAura())
            return form.display.getAur();
        else if (currentAura > -1)
            return (Aura) AuraController.Instance.get(currentAura);

        return null;
    }

    public Aura getUnlockedAura(int id) {
        if (unlockedAuras.contains(id))
            return (Aura) AuraController.Instance.get(id);
        return null;
    }

    public Aura getSelectedAura() {
        return (Aura) AuraController.Instance.get(selectedAura);
    }

    public void clearAllAuras() {
        unlockedAuras.clear();
        currentAura = -1;
        selectedAura = -1;
    }

    ///////////////////////////////////////////
    ///////////////////////////////////////////
    // Data handler
    public void resetChar() {
        clearAllForms();
        clearAllAuras();

        StatusEffectController.getInstance().clearEffects(parent.player);
        BonusController.getInstance().clearBonuses(parent.player);

        updateClient();
    }

    public void updateClient() {
        ((IPlayerDBCInfo) parent).updateDBCInfo();
    }

    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound dbcCompound = new NBTTagCompound();
        dbcCompound.setInteger("CurrentForm", currentForm);
        dbcCompound.setInteger("SelectedForm", selectedForm);
        dbcCompound.setTag("UnlockedForms", NBTTags.nbtIntegerSet(unlockedForms));
        dbcCompound.setTag("FormMastery", NBTTags.nbtIntegerFloatMap(formLevels));
        dbcCompound.setTag("FormTimers", NBTTags.nbtIntegerIntegerMap(formTimers));
        dbcCompound.setTag("FormWheel", NBTTags.nbtIntegerIntegerMap(formWheel));

        dbcCompound.setInteger("CurrentAura", currentAura);
        dbcCompound.setInteger("SelectedAura", selectedAura);
        dbcCompound.setTag("UnlockedAuras", NBTTags.nbtIntegerSet(unlockedAuras));

        compound.setTag("DBCInfo", dbcCompound);
    }

    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound dbcCompound = compound.getCompoundTag("DBCInfo");

        currentForm = dbcCompound.getInteger("CurrentForm");
        selectedForm = dbcCompound.getInteger("SelectedForm");
        unlockedForms = NBTTags.getIntegerSet(dbcCompound.getTagList("UnlockedForms", 10));
        formLevels = NBTTags.getIntegerFloatMap(dbcCompound.getTagList("FormMastery", 10));
        formTimers = NBTTags.getIntegerIntegerMap(dbcCompound.getTagList("FormTimers", 10));

        if (!dbcCompound.hasKey("FormWheel")) {
            for (int i = 0; i < 6; i++)
                formWheel.put(i, -1);
            dbcCompound.setTag("FormWheel", NBTTags.nbtIntegerIntegerMap(formWheel));
        }
        formWheel = NBTTags.getIntegerIntegerMap(dbcCompound.getTagList("FormWheel", 10));

        currentAura = dbcCompound.getInteger("CurrentAura");
        selectedAura = dbcCompound.getInteger("SelectedAura");
        unlockedAuras = NBTTags.getIntegerSet(dbcCompound.getTagList("UnlockedAuras", 10));
    }
}
