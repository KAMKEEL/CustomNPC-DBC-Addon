package kamkeel.npcdbc.data;

import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.config.ConfigDBCGeneral;
import kamkeel.npcdbc.controllers.*;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormMastery;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.util.ValueUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Store all player Addon DBC Info here
 */
public class PlayerDBCInfo {
    public PlayerData parent;

    public int currentForm = -1;
    public int selectedForm = -1, selectedDBCForm = -1,tempSelectedDBCForm = -1;

    public int currentAura = -1;
    public int selectedAura = -1;
    public HashSet<Integer> unlockedAuras = new HashSet<Integer>();

    public HashSet<Integer> unlockedForms = new HashSet<Integer>();
    public HashMap<Integer, Float> formLevels = new HashMap<Integer, Float>();
    public HashMap<Integer, Integer> formTimers = new HashMap<>();
    public FormWheelData[] formWheel = new FormWheelData[6];

    public Map<Integer, PlayerEffect> currentEffects;

    public PlayerDBCInfo(PlayerData parent) {
        this.parent = parent;

        for (int i = 0; i < formWheel.length; i++)
            formWheel[i] = new FormWheelData(i);
    }

    public void addForm(Form form) {
        if(form == null)
            return;

        unlockedForms.add(form.id);
        if(!formLevels.containsKey(form.id))
            formLevels.put(form.id, 0f);
    }

    public void addFormWheel(int wheelSlot, FormWheelData data) {
        if (wheelSlot > 5)
            return;
        formWheel[wheelSlot].readFromNBT(data.writeToNBT(new NBTTagCompound()));
    }

    public boolean hasFormUnlocked(int id) {
        return unlockedForms.contains(id);
    }

    public boolean removeForm(Form form) {
        if(form == null)
            return false;
        formLevels.remove(form.id);
        return unlockedForms.remove(form.id);
    }

    public boolean removeForm(int id) {
        formLevels.remove(id);
        return unlockedForms.remove(id);
    }

    public void removeFormWheel(int wheelSlot) {
        if (wheelSlot <= 5 && wheelSlot >= 0)
            formWheel[wheelSlot].reset();
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
        if(form == null)
            return false;
        return unlockedForms.contains(form.id);
    }

    public boolean isInCustomForm() {
        return currentForm > -1 && getCurrentForm() != null;
    }

    public String getFormColorCode(Form f) {
        if (f != null && f.getMenuName().contains("§")) {
            String s = f.getMenuName();
            int i = s.indexOf("§");
            return s.substring(i, 2);
        }
        return "";
    }

    public String getColoredName(Form f) {
        if(f == null)
            return "";
        return getFormColorCode(f) + f.getName();
    }

    public boolean isInForm(String formName) {
        Form form = getCurrentForm();
        if(form == null)
            return false;
        return form.getName().equals(formName);
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
        resetFormData(true, true);
    }
    public void resetFormData(boolean removeForms, boolean removeMasteries) {
        TransformController.handleFormDescend(parent.player, -10);
        currentForm = -1;
        selectedForm = -1;
        if(removeForms)
            unlockedForms.clear();
        if(removeMasteries)
            formLevels.clear();

        for (FormWheelData formWheelData : formWheel) formWheelData.reset();
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
            formLevels.put(f.id, 0f);

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

    public void addAura(Aura aura) {
        if(aura == null)
            return;
        unlockedAuras.add(aura.id);
    }

    public boolean hasAuraUnlocked(int id) {
        return unlockedAuras.contains(id);
    }

    public boolean removeAura(Aura aura) {
        if(aura == null)
            return false;
        return unlockedAuras.remove(aura.id);
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

    public boolean hasAura(Aura aura) {
        if(aura == null)
            return false;
        return unlockedAuras.contains(aura.id);
    }

    public boolean isInCustomAura() {
        return currentAura > -1 && getCurrentAura() != null;
    }

    public boolean isInAura(String AuraName) {
        Aura aura = getCurrentAura();
        if(aura == null)
            return false;
        return aura.getName().equals(AuraName);
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
        resetChar(ConfigDBCGeneral.FORMS_CLEAR_ON_RESET, ConfigDBCGeneral.FORM_MASTERIES_CLEAR_ON_RESET);
    }

    public void resetChar(boolean removeForms, boolean removeMasteries) {
        resetFormData(removeForms, removeMasteries);
        if(ConfigDBCGeneral.AURAS_CLEAR_ON_RESET)
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
        dbcCompound.setInteger("SelectedDBCForm", selectedDBCForm);
        dbcCompound.setTag("UnlockedForms", NBTTags.nbtIntegerSet(unlockedForms));
        dbcCompound.setTag("FormMastery", NBTTags.nbtIntegerFloatMap(formLevels));
        dbcCompound.setTag("FormTimers", NBTTags.nbtIntegerIntegerMap(formTimers));

        for (int i = 0; i < formWheel.length; i++)
            formWheel[i].writeToNBT(dbcCompound);

        dbcCompound.setInteger("CurrentAura", currentAura);
        dbcCompound.setInteger("SelectedAura", selectedAura);
        dbcCompound.setTag("UnlockedAuras", NBTTags.nbtIntegerSet(unlockedAuras));
        DBCData.get(parent.player).stats.saveEffectsNBT(dbcCompound);


        compound.setTag("DBCInfo", dbcCompound);
    }

    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound dbcCompound = compound.getCompoundTag("DBCInfo");

        currentForm = dbcCompound.getInteger("CurrentForm");
        selectedForm = dbcCompound.hasKey("SelectedForm") ? dbcCompound.getInteger("SelectedForm") : -1;
        selectedDBCForm = dbcCompound.hasKey("SelectedDBCForm") ? dbcCompound.getInteger("SelectedDBCForm") : -1;
        unlockedForms = NBTTags.getIntegerSet(dbcCompound.getTagList("UnlockedForms", 10));
        formLevels = NBTTags.getIntegerFloatMap(dbcCompound.getTagList("FormMastery", 10));
        formTimers = NBTTags.getIntegerIntegerMap(dbcCompound.getTagList("FormTimers", 10));

        for (int i = 0; i < formWheel.length; i++)
            formWheel[i].readFromNBT(dbcCompound.getCompoundTag("FormWheel" + i));

        currentAura = dbcCompound.getInteger("CurrentAura");
        selectedAura = dbcCompound.getInteger("SelectedAura");
        unlockedAuras = NBTTags.getIntegerSet(dbcCompound.getTagList("UnlockedAuras", 10));

        loadEffects(dbcCompound);
    }

    private void loadEffects(NBTTagCompound dbcCompound){
        if(FMLCommonHandler.instance().getEffectiveSide().isClient() || this.parent.player == null)
            return;

        ConcurrentHashMap<Integer, PlayerEffect> currentEffects = new ConcurrentHashMap<>();
        if (dbcCompound.hasKey("addonActiveEffects", 9)) {
            NBTTagList nbttaglist = dbcCompound.getTagList("addonActiveEffects", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                PlayerEffect playerEffect = PlayerEffect.readEffectData(nbttagcompound1);
                if (playerEffect != null) {
                    currentEffects.put(playerEffect.id, playerEffect);
                }
            }
        }
        StatusEffectController.Instance.playerEffects.put(Utility.getUUID(parent.player), currentEffects);

    }
}
