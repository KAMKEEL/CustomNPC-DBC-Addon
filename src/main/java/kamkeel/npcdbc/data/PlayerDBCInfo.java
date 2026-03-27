package kamkeel.npcdbc.data;

import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.config.ConfigDBCGeneral;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.controllers.*;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.form.FormKey;
import kamkeel.npcdbc.data.form.FormMastery;
import kamkeel.npcdbc.data.form.FormMasteryLinkData;
import kamkeel.npcdbc.data.overlay.OverlayManager;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import kamkeel.npcdbc.util.NBTHelper;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
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

    private String currentFormKey = null;
    private String selectedFormKey = null;
    public int selectedDBCForm = -1, tempSelectedDBCForm = -1;
    public int lastFormBeforeStack = -1;

    public int currentAura = -1;
    public int selectedAura = -1;
    public HashSet<Integer> unlockedAuras = new HashSet<Integer>();

    private String currentRaceKey = null;
    
    /** Addon-side branch cursor for multi-branch custom race FormTrees. 0 = first branch (default). */
    private int selectedFormBranch = 0;

    public HashSet<String> unlockedForms = new HashSet<>();
    public HashMap<String, Float> formLevels = new HashMap<>();
    public HashMap<String, Integer> formTimers = new HashMap<>();
    public HashMap<String, FormDisplay.BodyColor> configuredFormColors = new HashMap<>();
    public FormWheelData[] formWheel = new FormWheelData[6];

    public OverlayManager overlayManager = new OverlayManager();

    public PlayerDBCInfo(PlayerData parent) {
        this.parent = parent;

        for (int i = 0; i < formWheel.length; i++)
            formWheel[i] = new FormWheelData(i);

    }

    public void addForm(Form form) {
        if (form == null)
            return;

        String key = form.getKeyString();
        unlockedForms.add(key);
        if (!formLevels.containsKey(key))
            formLevels.put(key, 0f);
    }

    public void addFormWheel(int wheelSlot, FormWheelData data) {
        if (wheelSlot < 0 || wheelSlot > 5)
            return;
        formWheel[wheelSlot].readFromNBT(data.writeToNBT(new NBTTagCompound()));
    }

    // ── unlockedForms ──────────────────────────────────────────────

    public boolean hasFormUnlocked(String key) {
        return unlockedForms.contains(key) || hasRacialForm(key);
    }

    public boolean removeForm(Form form) {
        return removeForm(form, ConfigDBCGeneral.FORM_MASTERIES_CLEAR_ON_REMOVE);
    }

    public boolean removeForm(String key) {
        return removeForm(key, ConfigDBCGeneral.FORM_MASTERIES_CLEAR_ON_REMOVE);
    }

    public boolean removeForm(Form form, boolean removesMastery) {
        if (form == null)
            return false;
        return removeForm(form.getKeyString(), removesMastery);
    }

    public boolean removeForm(String key, boolean removesMastery) {
        if (key == null || key.isEmpty())
            return false;
        if (removesMastery)
            formLevels.remove(key);
        return unlockedForms.remove(key);
    }

    public void removeFormWheel(int wheelSlot) {
        if (wheelSlot <= 5 && wheelSlot >= 0)
            formWheel[wheelSlot].reset();
    }

    public Form getForm(String key) {
        if (key == null || key.isEmpty()) return null;
        if (unlockedForms.contains(key))
            return FormController.getInstance().getFromKey(key);
        return null;
    }

    public boolean hasSelectedForm() {
        return selectedFormKey != null && getSelectedForm() != null;
    }

    public boolean hasForm(Form form) {
        if (form == null)
            return false;
        String key = form.getKeyString();
        if (hasRacialForm(key))
            return true;

        return unlockedForms.contains(key);
    }

    public boolean isInCustomForm() {
        return currentFormKey != null && getCurrentForm() != null;
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
        if (f == null)
            return "";
        return getFormColorCode(f) + f.getName();
    }

    public boolean isInForm(String key) {
        Form form = getCurrentForm();
        if (form == null)
            return false;
        return form.getKeyString().equals(key);
    }

    public boolean isInForm(int formID) {
        Form current = getCurrentForm();
        return current != null && current.id == formID;
    }

    public Form getCurrentForm() {
        if (currentFormKey == null) return null;
        Form f = FormController.Instance.getFromKey(currentFormKey);
        if (f != null) return f;
        return null;
    }

    public String getCurrentFormKey() {
        return currentFormKey;
    }

    public void setCurrentForm(Form form) {
        if (form == null) {
            currentFormKey = null;
            return;
        }
        currentFormKey = form.key != null ? form.key.toString() : FormKey.custom(form.name).toString();
    }

    public void setCurrentForm(String key) {
        currentFormKey = key;
    }

    public void clearCurrentForm() {
        currentFormKey = null;
    }

    public Form getUnlockedForm(String key) {
        if (key == null || key.isEmpty()) return null;
        if (unlockedForms.contains(key))
            return FormController.Instance.getFromKey(key);
        return null;
    }

    public Form getSelectedForm() {
        if (selectedFormKey == null) return null;
        
        Form f = FormController.Instance.getFromKey(selectedFormKey);
        if (f != null) return f;
        return null;
    }

    public String getSelectedFormKey() {
        return selectedFormKey;
    }

    public void setSelectedForm(Form form) {
        selectedFormKey = form != null ? form.key.toString() : null;
        selectedDBCForm = tempSelectedDBCForm = -1;
    }

    public void setSelectedForm(String key) {
        selectedFormKey = key;
    }

    public void clearSelectedForm() {
        selectedFormKey = null;
        selectedDBCForm = tempSelectedDBCForm = -1;
    }

    public void clearAllForms() {
        resetFormData(true, true);
    }

    public void resetFormData(boolean removeForms, boolean removeMasteries) {
        TransformController.handleFormDescend(parent.player, -10);
        clearCurrentForm();
        clearSelectedForm();
        if (removeForms)
            unlockedForms.clear();
        if (removeMasteries)
            formLevels.clear();

        for (FormWheelData formWheelData : formWheel) formWheelData.reset();
    }

    
    public void setSelectedFormBranch(int selectedFormBranch) {
        this.selectedFormBranch = selectedFormBranch;
    }
    
    public int getSelectedFormBranch() {
        return selectedFormBranch;
    }

    // ── formLevels ───────────────────────────────────────────────

    public void updateCurrentFormMastery(String gainType) {
        Form current = getCurrentForm();
        if (current != null)
            updateFormMastery(current.getKeyString(), gainType);
    }

    public void updateFormMastery(String key, String gainType) {
        Form f = FormController.getInstance().getFromKey(key);
        if (f == null || !isInCustomForm() || parent.player == null)
            return;

        DBCData data = DBCData.get(parent.player);
        if (data == null)
            return;

        FormMastery fm = (FormMastery) f.getMastery();
        if (!formLevels.containsKey(key))
            formLevels.put(key, 0f);

        float playerLevel = formLevels.get(key);
        float fullGain = fm.calculateFullGain(gainType, playerLevel, data.MND);

        playerLevel = ValueUtil.clamp(playerLevel + fullGain, 0, fm.maxLevel);
        formLevels.replace(key, playerLevel);
        updateClient();
    }
    
    public void addFormLevel(String key, float amount) {
        Form form = FormController.getInstance().getFromKey(key);
        if (form != null) {
            float current = formLevels.getOrDefault(key, 0f);
            float updated = ValueUtil.clamp(current + amount, 0, ((FormMastery) form.getMastery()).maxLevel);
            formLevels.put(key, updated);
            updateClient();
        }
    }

    public void setFormLevel(String key, float amount) {
        setFormLevel(key, amount, true);
    }

    public void setFormLevel(String key, float amount, boolean updateClient) {
        Form form = FormController.getInstance().getFromKey(key);
        if (form != null) {
            float updated = ValueUtil.clamp(amount, 0, ((FormMastery) form.getMastery()).maxLevel);
            formLevels.put(key, updated);
            if (updateClient)
                updateClient();
        }
    }

    public void removeFormMastery(String key) {
        if (key == null || key.isEmpty()) return;
        formLevels.remove(key);
        updateClient();
    }

    public float getFormLevel(String key) {
        return getFormLevel(key, true);
    }

    public float getFormLevel(String key, boolean checkFusion) {
        if (key == null || key.isEmpty())
            return 0f;

        float mastery = formLevels.getOrDefault(key, 0f);
        if (!checkFusion || parent.player == null)
            return mastery;

        NBTTagCompound compound = parent.player.getEntityData().getCompoundTag("PlayerPersisted");
        if (isFused(compound)) {
            EntityPlayer fusedPlayer = getSpectatorEntity(compound);
            if (fusedPlayer != null) {
                float otherPlayerMastery = PlayerDataUtil.getDBCInfo(fusedPlayer).formLevels.getOrDefault(key, 0f);
                mastery = mastery + otherPlayerMastery;
            }
        }

        return mastery;
    }

    public float getFormLevel(Form form) {
        if (form == null) return 0f;
        return getFormLevel(form.getKeyString(), true);
    }

    public float getCurrentLevel() {
        Form current = getCurrentForm();
        return current != null ? getFormLevel(current.getKeyString()) : 0f;
    }

    // ── formTimers ───────────────────────────────────────────────

    public void addTimer(String key, int timeInTicks) {
        if (!formTimers.containsKey(key))
            formTimers.put(key, timeInTicks);

        formTimers.replace(key, timeInTicks);
    }

    public void decrementTimer(String key) {
        if (formTimers.containsKey(key)) {
            int currentTime = formTimers.get(key);
            if (currentTime > 0)
                formTimers.replace(key, currentTime - 1);
            else if (currentTime == 0) {
                TransformController.handleFormDescend(parent.player, 0);
                formTimers.remove(key);
            }
        }
    }

    public int getTimer(String key) {
        if (formTimers.containsKey(key))
            return formTimers.get(key);
        return -1;
    }

    public boolean hasTimer(String key) {
        if (formTimers.containsKey(key))
            return formTimers.get(key) > -1;
        return false;
    }

    /// ////////////////////////////////////////
    /// ////////////////////////////////////////
    // Aura stuff
    public void addAura(Aura aura) {
        if (aura == null)
            return;
        unlockedAuras.add(aura.id);
    }

    public boolean hasAuraUnlocked(int id) {
        return unlockedAuras.contains(id);
    }

    public boolean removeAura(Aura aura) {
        if (aura == null)
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
        if (aura == null)
            return false;
        return unlockedAuras.contains(aura.id);
    }

    public boolean isInCustomAura() {
        return currentAura > -1 && getCurrentAura() != null;
    }

    public boolean isInAura(String AuraName) {
        Aura aura = getCurrentAura();
        if (aura == null)
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

    /// ////////////////////////////////////////
    /// ////////////////////////////////////////
    // Data handler
    public void resetChar() {
        resetChar(ConfigDBCGeneral.FORMS_CLEAR_ON_RESET, ConfigDBCGeneral.FORM_MASTERIES_CLEAR_ON_RESET);
    }

    public void resetChar(boolean removeForms, boolean removeMasteries) {
        resetFormData(removeForms, removeMasteries);
        if (ConfigDBCGeneral.AURAS_CLEAR_ON_RESET)
            clearAllAuras();

        configuredFormColors.clear();

        currentRaceKey = null;
        selectedFormBranch = 0;

        DBCEffectController.getInstance().clearDBCEffects(parent.player);
        BonusController.getInstance().clearBonuses(parent.player);

        updateClient();
    }

    public void updateClient() {
        this.handleLinkedFormMastery();
        ((IPlayerDBCInfo) parent).updateDBCInfo();
    }

    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound dbcCompound = new NBTTagCompound();
        dbcCompound.setString("CurrentFormKey", currentFormKey != null ? currentFormKey : "");
        dbcCompound.setString("SelectedFormKey", selectedFormKey != null? selectedFormKey : "");
        dbcCompound.setInteger("SelectedDBCForm", selectedDBCForm);
        dbcCompound.setInteger("LastFormBeforeStack", lastFormBeforeStack);
        dbcCompound.setTag("UnlockedForms", NBTHelper.nbtStringSet(unlockedForms));
        dbcCompound.setTag("FormMastery", NBTHelper.nbtStringFloatMap(formLevels));
        dbcCompound.setTag("FormTimers", NBTHelper.nbtStringIntMap(formTimers));
        dbcCompound.setTag("ConfigurableFormColors",
            NBTHelper.nbtStringObjectMap(
                configuredFormColors,
                bodyColor -> bodyColor.writeToNBT(new NBTTagCompound()),
                (key, colors) -> !colors.isEmpty()
            ));

        for (int i = 0; i < formWheel.length; i++)
            formWheel[i].writeToNBT(dbcCompound);


        dbcCompound.setInteger("CurrentAura", currentAura);
        dbcCompound.setInteger("SelectedAura", selectedAura);
        dbcCompound.setTag("UnlockedAuras", NBTTags.nbtIntegerSet(unlockedAuras));
        
        dbcCompound.setString("CurrentRace", currentRaceKey != null ? currentRaceKey : "");
        dbcCompound.setInteger("SelectedBranchIndex", selectedFormBranch);

        saveBonuses(dbcCompound);

        dbcCompound.setTag("OverlayManager", overlayManager.writeToNBT());
        compound.setTag("DBCInfo", dbcCompound);
    }

    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound dbcCompound = compound.getCompoundTag("DBCInfo");

        //Migrate old CurrentForm id
        if (dbcCompound.hasKey("CurrentFormKey")) {
            currentFormKey = dbcCompound.getString("CurrentFormKey");
            if (currentFormKey.isEmpty()) currentFormKey = null;
        } else if (dbcCompound.hasKey("CurrentForm")) {
            int legacyId = dbcCompound.getInteger("CurrentForm");
            if (legacyId > -1) {
                Form legacyForm = (Form) FormController.Instance.get(legacyId);
                if (legacyForm != null)
                    currentFormKey = legacyForm.key != null ? legacyForm.key.toString() : FormKey.custom(legacyForm.name).toString();
            }
            dbcCompound.removeTag("CurrentForm");
        }

        //Migrate old SelectedForm id
        if (dbcCompound.hasKey("SelectedFormKey")) {
            selectedFormKey = dbcCompound.getString("SelectedFormKey");
            if (selectedFormKey.isEmpty()) selectedFormKey = null;
        } else if (dbcCompound.hasKey("SelectedForm")) {
            int legacyId = dbcCompound.getInteger("SelectedForm");
            if (legacyId > -1) {
                Form legacyForm = (Form) FormController.Instance.get(legacyId);
                if (legacyForm != null)
                    selectedFormKey = legacyForm.key != null ? legacyForm.key.toString() : FormKey.custom(legacyForm.name).toString();
            }
            dbcCompound.removeTag("SelectedForm");
        }
        
        selectedDBCForm = dbcCompound.hasKey("SelectedDBCForm") ? dbcCompound.getInteger("SelectedDBCForm") : -1;
        lastFormBeforeStack = dbcCompound.hasKey("LastFormBeforeStack") ? dbcCompound.getInteger("LastFormBeforeStack") : -1;

        // UnlockedForms migration: TAG_STRING list = new format, TAG_COMPOUND list = legacy int format
        NBTTagList unlockedList = dbcCompound.getTagList("UnlockedForms", 10);
        if (unlockedList.tagCount() > 0) {
            // Legacy int format (TAG_COMPOUND via nbtIntegerSet)
            HashSet<Integer> legacySet = NBTTags.getIntegerSet(unlockedList);
            unlockedForms = new HashSet<>();
            for (int id : legacySet) {
                Form f = (Form) FormController.getInstance().get(id);
                if (f != null)
                    unlockedForms.add(f.getKeyString());
            }
        } else {
            NBTTagList stringList = dbcCompound.getTagList("UnlockedForms", Constants.NBT.TAG_STRING);
            if (stringList.tagCount() > 0) {
                unlockedForms = NBTHelper.getStringSet(stringList);
            } else {
                unlockedForms = new HashSet<>();
            }
        }

        // FormMastery migration: check first compound for "key" vs "id"
        NBTTagList masteryList = dbcCompound.getTagList("FormMastery", Constants.NBT.TAG_COMPOUND);
        if (masteryList.tagCount() > 0 && masteryList.getCompoundTagAt(0).hasKey("key")) {
            formLevels = NBTHelper.getStringFloatMap(masteryList);
        } else if (masteryList.tagCount() > 0) {
            HashMap<Integer, Float> legacyLevels = NBTTags.getIntegerFloatMap(masteryList);
            formLevels = new HashMap<>();
            for (Map.Entry<Integer, Float> entry : legacyLevels.entrySet()) {
                Form f = (Form) FormController.getInstance().get(entry.getKey());
                if (f != null)
                    formLevels.put(f.getKeyString(), entry.getValue());
            }
        } else {
            formLevels = new HashMap<>();
        }

        // FormTimers migration
        NBTTagList timerList = dbcCompound.getTagList("FormTimers", Constants.NBT.TAG_COMPOUND);
        if (timerList.tagCount() > 0 && timerList.getCompoundTagAt(0).hasKey("key")) {
            formTimers = NBTHelper.getStringIntMap(timerList);
        } else if (timerList.tagCount() > 0) {
            HashMap<Integer, Integer> legacyTimers = NBTTags.getIntegerIntegerMap(timerList);
            formTimers = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : legacyTimers.entrySet()) {
                Form f = (Form) FormController.getInstance().get(entry.getKey());
                if (f != null)
                    formTimers.put(f.getKeyString(), entry.getValue());
            }
        } else {
            formTimers = new HashMap<>();
        }

        for (int i = 0; i < formWheel.length; i++)
            formWheel[i].readFromNBT(dbcCompound.getCompoundTag("FormWheel" + i));


        currentAura = dbcCompound.getInteger("CurrentAura");
        selectedAura = dbcCompound.getInteger("SelectedAura");
        unlockedAuras = NBTTags.getIntegerSet(dbcCompound.getTagList("UnlockedAuras", 10));

        // CurrentRace migration: String = new format, Integer = legacy int format
        if (dbcCompound.hasKey("CurrentRace", 8)) {
            // NBT type 8 = TAG_String — new format
            currentRaceKey = dbcCompound.getString("CurrentRace");
            if (currentRaceKey.isEmpty()) currentRaceKey = null;
        } else if (dbcCompound.hasKey("CurrentRace")) {
            // Legacy int format
            int legacyId = dbcCompound.getInteger("CurrentRace");
            if (legacyId > -1) {
                Race legacyRace = RaceController.getInstance().get(legacyId);
                if (legacyRace != null)
                    currentRaceKey = legacyRace.getName();
            }
        }
        selectedFormBranch = dbcCompound.hasKey("SelectedBranchIndex") ? dbcCompound.getInteger("SelectedBranchIndex") : 0;

        // ConfigurableFormColors migration
        if (dbcCompound.hasKey("ConfigurableFormColors")) {
            NBTTagList colorList = dbcCompound.getTagList("ConfigurableFormColors", Constants.NBT.TAG_COMPOUND);
            if (colorList.tagCount() > 0 && colorList.getCompoundTagAt(0).hasKey("key")) {
                configuredFormColors = NBTHelper.getStringObjectMap(
                    colorList,
                    (colorCompound) -> {
                        FormDisplay.BodyColor color = new FormDisplay.BodyColor();
                        color.readFromNBT(colorCompound);
                        return color;
                    },
                    (key, color) -> {
                        Form f = FormController.getInstance().getFromKey(key);
                        return f != null && !color.isEmpty();
                    }
                );
            } else {
                HashMap<Integer, FormDisplay.BodyColor> legacyColors = NBTHelper.javaIntegerObjectMap(
                    colorList,
                    (colorCompound) -> {
                        FormDisplay.BodyColor color = new FormDisplay.BodyColor();
                        color.readFromNBT(colorCompound);
                        return color;
                    },
                    (slot, color) -> FormController.getInstance().has(slot) && !color.isEmpty()
                );
                configuredFormColors = new HashMap<>();
                for (Map.Entry<Integer, FormDisplay.BodyColor> entry : legacyColors.entrySet()) {
                    Form f = (Form) FormController.getInstance().get(entry.getKey());
                    if (f != null)
                        configuredFormColors.put(f.getKeyString(), entry.getValue());
                }
            }
        }

        loadBonuses(dbcCompound);

        if (dbcCompound.hasKey("OverlayManager"))
            overlayManager.readFromNBT(dbcCompound.getCompoundTag("OverlayManager"));
    }


    private void loadBonuses(NBTTagCompound dbcCompound) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() || this.parent.player == null)
            return;

        ConcurrentHashMap<String, PlayerBonus> currentBonuses = new ConcurrentHashMap<>();
        if (dbcCompound.hasKey("addonBonus", 9)) {
            NBTTagList nbttaglist = dbcCompound.getTagList("addonBonus", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                PlayerBonus bonus = PlayerBonus.readBonusData(nbttagcompound1);
                currentBonuses.put(bonus.name, bonus);
            }
        }

        BonusController.getInstance().playerBonus.put(Utility.getUUID(parent.player), currentBonuses);
    }

    private void saveBonuses(NBTTagCompound dbcCompound) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT || parent.player == null)
            return;

        Map<String, PlayerBonus> test = BonusController.getInstance().getPlayerBonus(parent.player);
        if (test == null || test.isEmpty())
            return;

        NBTTagList nbttaglist = new NBTTagList();

        for (PlayerBonus bonus : test.values()) {
            nbttaglist.appendTag(bonus.writeBonusData(new NBTTagCompound()));
        }
        dbcCompound.setTag("addonBonus", nbttaglist);
    }

    private void handleLinkedFormMastery() {
        Form form = getCurrentForm();
        if (form == null)
            return;

        DBCData data = DBCData.get(parent.player);

        if (!form.mastery.masteryLink.hasLinkData(data.getRace()))
            return;

        FormMasteryLinkData.LinkData linkData = form.mastery.masteryLink.masteryLinks.get((int) data.Race);
        if (linkData.isCustomLink) {
            handleCustomLinking(data, form, linkData.formID);
        } else {
            handleDBCLinking(data, form, linkData.formID);
        }
    }

    private void handleCustomLinking(DBCData data, Form form, int formID) {
        Form otherForm = (Form) FormController.getInstance().get(formID);
        if (otherForm == null)
            return;

        String key = form.getKeyString();
        String otherKey = otherForm.getKeyString();
        float currentFormMastery = formLevels.getOrDefault(key, 0.0f);
        float otherFormMastery = formLevels.getOrDefault(otherKey, 0.0f);

        float highest = Math.max(currentFormMastery, otherFormMastery);
        setFormLevel(key, highest, false);
        setFormLevel(otherKey, highest, false);
    }

    private void handleDBCLinking(DBCData data, Form form, int dbcForm) {
        int jrmcFormID = DBCForm.getJRMCFormID(dbcForm, data.Race);

        if (jrmcFormID == -1)
            return;

        double currentDBCFormLevel = data.stats.getDBCMastery(jrmcFormID);
        float currentCustomMastery = formLevels.getOrDefault(form.getKeyString(), 0.0f);

        double highest = Math.max(currentCustomMastery, currentDBCFormLevel);
        data.stats.setDBCMastery(jrmcFormID, highest);
        setFormLevel(form.getKeyString(), (float) highest, false);
    }


    private boolean isFused(NBTTagCompound compound) {
        String statusEffects = compound.getString("jrmcStatusEff");
        String fusionString = compound.getString("jrmcFuzion");
        if (JRMCoreH.StusEfcts(10, statusEffects) || JRMCoreH.StusEfcts(11, statusEffects))
            return true;
        if (fusionString.contains(",")) {
            String[] fusionMembers = fusionString.split(",");
            return fusionMembers.length == 3;
        }
        return false;
    }

    private String getSpectatorName(NBTTagCompound compound) {
        String fusionString = compound.getString("jrmcFuzion");
        if (fusionString.contains(",")) {
            String[] fusionMembers = fusionString.split(",");
            if (fusionMembers.length == 3)
                return fusionMembers[1];

        }
        return "";
    }

    private boolean isFusionSpectator(NBTTagCompound compound) {
        String statusEffects = compound.getString("jrmcStatusEff");
        if (JRMCoreH.StusEfcts(11, statusEffects)) {
            return true;
        }
        String fusionString = compound.getString("jrmcFuzion");
        if (fusionString.contains(",")) {
            String[] fusionMembers = fusionString.split(",");
            if (fusionMembers.length == 3)
                return fusionMembers[1].equalsIgnoreCase(parent.player.getCommandSenderName());

        }
        return false;
    }

    private EntityPlayer getSpectatorEntity(NBTTagCompound compound) {
        if (isFused(compound) && !isFusionSpectator(compound)) {
            String spectator = getSpectatorName(compound);
            if (!spectator.isEmpty()) {
                EntityPlayer specEntity = null;
                if (parent.player.worldObj.isRemote) {
                    specEntity = parent.player.worldObj.getPlayerEntityByName(spectator);
                } else {
                    specEntity = NoppesUtilServer.getPlayerByName(spectator);
                }
                return specEntity;
            }
        }
        return null;
    }

    public void setFormColorConfig(Form form, FormDisplay.BodyColor colors) {
        String key = form.getKeyString();
        if (colors.isEmpty())
            configuredFormColors.remove(key);
        else
            configuredFormColors.put(key, colors);
    }

    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    // RACE
    public boolean isCustomRace() {
        return currentRaceKey != null && getCurrentRace() != null;
    }

    public Race getCurrentRace() {
        if (currentRaceKey == null) return null;
        return RaceController.getInstance().getByName(currentRaceKey);
    }

    public Race getRace() {
        return getCurrentRace();
    }

    public String getCurrentRaceKey() {
        return currentRaceKey;
    }

    public void setCurrentRace(Race race) {
        currentRaceKey = race != null ? race.getName() : null;
    }

    public void setCurrentRace(String key) {
        currentRaceKey = key;
    }

    public void clearCurrentRace() {
        currentRaceKey = null;
    }

    public boolean hasRacialForm(String formKey) {
        if (parent == null || parent.player == null)
            return false;

        return DBCData.get(parent.player).addonRace.hasForm(formKey);
    }
}
