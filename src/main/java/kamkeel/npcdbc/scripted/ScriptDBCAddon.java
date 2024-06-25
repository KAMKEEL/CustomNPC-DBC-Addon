package kamkeel.npcdbc.scripted;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.JGPlayerMP;
import kamkeel.npcdbc.api.IDBCAddon;
import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.scripted.entity.ScriptDBCPlayer;
import noppes.npcs.util.ValueUtil;

import java.util.Arrays;

import static JinRyuu.JRMCore.JRMCoreH.getInt;

// Implemented by Kam, Ported from Goatee Design
public class ScriptDBCAddon<T extends EntityPlayerMP> extends ScriptDBCPlayer<T> implements IDBCAddon {
    public T player;
    public NBTTagCompound nbt;
    public DBCData dbcData;

    public ScriptDBCAddon(T player) {
        super(player);
        this.player = player;
        this.nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
        dbcData = DBCData.get(player);
    }


    /**
     * @return Player's max body
     */
    @Override
    public int getMaxBody() {
        return dbcData.stats.getMaxBody();
    }

    /**
     * @return Player's max hp (body)
     */
    @Override
    public int getMaxHP() {
        return getMaxBody();
    }

    /**
     * @return Current body as a percentage of max hp (body)
     */
    @Override
    public float getBodyPercentage() {
        return dbcData.stats.getCurrentBodyPercentage();
    }

    /**
     * @return Player's max ki
     */
    @Override
    public int getMaxKi() {
        return dbcData.stats.getMaxKi();
    }

    /**
     * @return Player's max stamina
     */
    @Override
    public int getMaxStamina() {
        return dbcData.stats.getMaxStamina();
    }

    /**
     * @return an array of all player attributes
     */
    @Override
    public int[] getAllAttributes() {
        return dbcData.stats.getAllAttributes();
    }

    /**
     * @param attri               adds attri to player stats
     * @param multiplyAddedAttris if true, adds first then multiplies by multivalue
     * @param multiValue          value to multiply with
     */
    @Override
    public void modifyAllAttributes(int[] attri, boolean multiplyAddedAttris, double multiValue) {
        if (attri.length == 6) {
            int[] stats = getAllAttributes();
            double multi = multiValue;
            if (multiValue == 0 || !multiplyAddedAttris) {
                multi = 1.0;
            }

            int[] newstats = new int[stats.length];
            for (int i = 0; i < stats.length; i++) {
                newstats[i] = (int) ((double) (stats[i] + attri[i]) * multi);
                nbt.setInteger(JRMCoreH.AttrbtNbtI[i], newstats[i]);

            }
            this.setBody(getMaxBody());
            this.setKi(getMaxKi());
            this.setStamina(getMaxStamina());
        }
    }

    /**
     * @param Num           adds all attributes by Num
     * @param setStatsToNum sets all attributes by Num
     */
    @Override
    public void modifyAllAttributes(int Num, boolean setStatsToNum) {

        int[] num = new int[]{Num, Num, Num, Num, Num, Num};
        if (!setStatsToNum) {
            modifyAllAttributes(num, false, 1);
        } else {
            for (int i = 0; i < num.length; i++) {
                nbt.setInteger(JRMCoreH.AttrbtNbtI[i], num[i]);
            }
            this.setBody(getMaxBody());
            this.setKi(getMaxKi());
            this.setStamina(getMaxStamina());
        }
    }

    /**
     * If not setStats, then it will ADD submitted[0] to stats[0]. Submitted must be length of 6
     *
     * @param submitted Adds all attributes by array of attributes respectively i.e atr 0 gets added to index 0 of a
     * @param setStats  sets all attributes to submitted array
     */
    @Override
    public void modifyAllAttributes(int[] submitted, boolean setStats) {
        if (submitted.length == 6) {
            if (!setStats) {
                modifyAllAttributes(submitted, false, 1);
            } else {
                for (int i = 0; i < submitted.length; i++) {
                    nbt.setInteger(JRMCoreH.AttrbtNbtI[i], submitted[i]);
                }
                this.setBody(getMaxBody());
                this.setKi(getMaxKi());
                this.setStamina(getMaxStamina());
            }
        }
    }

    /**
     * 0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
     *
     * @param statid statID to multiply
     * @param multi  value to multi by
     */
    @Override
    public void multiplyAttribute(int statid, double multi) {
        if (multi == 0)
            multi = 1.0;

        int[] stats = getAllAttributes();

        int newstat = (int) Math.floor((double) stats[statid] * multi);
        nbt.setInteger(JRMCoreH.AttrbtNbtI[statid], newstat);

        switch (statid) {
            case 2:
                this.setBody(getMaxBody());
                this.setStamina(getMaxStamina());
                break;
            case 5:
                this.setKi(getMaxKi());
                break;
        }
    }

    /**
     * @param multi multiplies all attributes by multi
     */
    @Override
    public void multiplyAllAttributes(double multi) {
        int[] stats = getAllAttributes();
        if (multi == 0) {
            multi = 1.0;
        }
        int[] newstats = new int[stats.length];
        for (int i = 0; i < stats.length; i++) {
            newstats[i] = (int) Math.floor((double) stats[i] * multi);
            nbt.setInteger(JRMCoreH.AttrbtNbtI[i], newstats[i]);
        }
        this.setBody(getMaxBody());
        this.setKi(getMaxKi());
        this.setStamina(getMaxStamina());
    }

    /**
     * A "Full" attribute is a attribute that has all form multipliers calculated. i.e if base Strength is 10,000, returns 10,000.
     * if SSJ form multi is 20x and is SSJ, returns 200,000. LSSJ returns 350,000, LSSJ Kaioken x40 returns 1,000,000 and so on
     * 0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
     *
     * @param attributeID ID of attribute
     * @return full attribute value
     */
    @Override
    public int getFullAttribute(int attributeID) {
        return dbcData.stats.getFullAttribute(attributeID);
    }

    @Override
    public int[] getAllFullAttributes() {
        return dbcData.stats.getAllFullAttributes();
    }

    /**
     * @return Player's race name
     */
    @Override
    public String getRaceName() {
        if (this.getRace() >= 0 && this.getRace() <= 5) {
            return JRMCoreH.Races[this.getRace()];
        }
        return null;
    }

    /**
     * @return Name of form player is currently in
     */
    @Override
    public String getCurrentDBCFormName() {
        int race = this.getRace();
        int form = (int) this.getForm();
        return DBCAPI.Instance().getFormName(race, form);
    }

    /**
     * @param formName name of form to change mastery of
     * @param amount   sets the current mastery value to amount
     * @param add      adds the amount to current mastery, instead of setting it to it
     */
    @Override
    public void changeDBCMastery(String formName, double amount, boolean add) {
        JGPlayerMP JG = new JGPlayerMP(player);
        NBTTagCompound nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
        JG.setNBT(nbt);
        int race = (int) JG.getRace();

        boolean found = false;
        boolean isKaiokenOn = false;
        boolean isMysticOn = false;
        boolean isUltraInstinctOn = false;
        boolean isGoDOn = false;
        int state = 0;
        int state2 = 0;
        double amountKK = 0;

        switch (formName.toLowerCase()) {
            case "kaioken":
                found = true;
                isKaiokenOn = true;
                amountKK = amount;
                break;
            case "mystic":
                found = true;
                isMysticOn = true;
                break;
            case "ultrainstict":
                found = true;
                isUltraInstinctOn = true;
                break;
            case "godofdestruction":
                found = true;
                isGoDOn = true;
                break;

        }
        if (found) {
            String nonracial = nbt.getString("jrmcFormMasteryNonRacial");
            String[] masteries = nonracial.split(";");
            int foundatindex;
            String newnonracial = "";
            for (int i = 0; i < masteries.length; i++) {
                if (masteries[i].toLowerCase().contains(formName.toLowerCase())) {
                    foundatindex = i;
                    String[] masteryvalues = masteries[i].split(",");
                    double masteryvalue = Double.parseDouble(masteryvalues[1]);
                    if (add) {
                        masteryvalue += amount;
                    } else {
                        masteryvalue = amount;
                    }
                    String newvalue = Double.toString(masteryvalue);
                    switch (foundatindex) {
                        case 0:
                            newnonracial = masteryvalues[0] + "," + newvalue + ";" + masteries[1] + ";" + masteries[2] + ";" + masteries[3];
                            break;
                        case 1:
                            newnonracial = masteries[0] + ";" + masteryvalues[0] + "," + newvalue + ";" + masteries[2] + ";" + masteries[3];
                            break;
                        case 2:
                            newnonracial = masteries[0] + ";" + masteries[1] + ";" + masteryvalues[0] + "," + newvalue + ";" + masteries[3];
                            ;
                            break;
                        case 3:
                            newnonracial = masteries[0] + ";" + masteries[1] + ";" + masteries[2] + ";" + masteryvalues[0] + "," + newvalue;
                            break;
                    }

                    nbt.setString("jrmcFormMasteryNonRacial", newnonracial);
                    break;
                }
            }
        } else {
            for (int i = 0; i < JRMCoreH.trans[race].length; i++) {
                if (JRMCoreH.trans[race][i].equalsIgnoreCase(formName)) {
                    state = i;
                    found = true;
                    JRMCoreH.changeFormMasteriesValue(player, amount, amountKK, add, race, state, state2, isKaiokenOn, isMysticOn, isUltraInstinctOn, isGoDOn, -1);

                }
            }
        }

        if (!found) {
            throw new CustomNPCsException("Invalid \nform name. For non racial form names, use Kaioken, Mystic, UltraInstict and GodOfDestruction. For racial \nform names, check getformName(int race, int form) or getCurrentformName()", new Object[2]);
        }
    }

    /**
     * @param formName name of form
     * @return Form mastery value
     */
    @Override
    public double getDBCMasteryValue(String formName) {

        JGPlayerMP JG = new JGPlayerMP(player);
        NBTTagCompound nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
        JG.setNBT(nbt);
        int race = (int) JG.getRace();
        boolean found = false;
        double valuetoreturn = -1.0;

        switch (formName.toLowerCase()) {
            case "kaioken":
                found = true;
                break;
            case "mystic":
                found = true;
            case "ultrainstict":
                found = true;
                break;
            case "godofdestruction":
                found = true;
                break;

        }
        if (found) {
            String nonracial = nbt.getString("jrmcFormMasteryNonRacial");
            String[] masteries = nonracial.split(";");
            for (String mastery : masteries) {
                if (mastery.toLowerCase().contains(formName.toLowerCase())) {
                    String[] masteryvalues = mastery.split(",");
                    valuetoreturn = Double.parseDouble(masteryvalues[1]);
                    return valuetoreturn;
                }
            }
        } else {
            String racial = nbt.getString("jrmcFormMasteryRacial_" + JRMCoreH.Races[race]);
            String[] masteries = racial.split(";");
            for (String mastery : masteries) {
                if (mastery.toLowerCase().contains(formName.toLowerCase())) {
                    String[] masteryvalues = mastery.split(",");
                    double masteryvalue = Double.parseDouble(masteryvalues[1]);
                    found = true;

                    valuetoreturn = masteryvalue;
                    return valuetoreturn;
                }
            }
        }
        if (!found) {
            throw new CustomNPCsException("Invalid \nform name. For non racial form names, use Kaioken, Mystic, UltraInstict and GodOfDestruction. For racial \nform names, check getformName(int race, int form) or getCurrentformName()", new Object[2]);
        }
        throw new CustomNPCsException("Form Mastery value is -1.0", new Object[3]);
    }

    /**
     * @return Entire form mastery NBT string, aka data32
     */
    @Override
    public String getAllDBCMasteries() {
        return JRMCoreH.getFormMasteryData(player);
    }

    /**
     * @return True if player is fused and spectator
     */
    @Override
    public boolean isDBCFusionSpectator() {
        return JRMCoreH.isFusionSpectator(player);
    }

    @Override
    public boolean isChargingKi() {
        return dbcData.stats.isChargingKiAttack();
    }

    /**
     * @param skillname Check JRMCoreH.DBCSkillNames
     * @return skill level from 1 to 10
     */
    @Override
    public int getSkillLevel(String skillname) {
        int i;
        String[] playerskills = nbt.getString("jrmcSSlts").split(",");
        String[] skillids = JRMCoreH.DBCSkillsIDs;
        String[] skillnames = JRMCoreH.DBCSkillNames;
        boolean skillFound = false;
        boolean playerHasSkill = false;
        for (i = 0; i < skillnames.length; i++) {
            if (skillname.equals(skillnames[i])) {
                skillFound = true;
                for (String playerskill : playerskills) {
                    if (playerskill.contains(skillids[i])) {
                        return JRMCoreH.SklLvl(i, playerskills);
                    }
                }
            }
        }
        if (!skillFound) {
            throw new CustomNPCsException("\nInvalid Skill ID :" + skillname + ". Please re-enter the skill name \nwithout any spaces in between. \ni.e: GodOfDestruction, KiProtection, \nDefensePenetration", new Object[0]);
        }
        if (!playerHasSkill) {
            throw new CustomNPCsException("\nPlayer doesn't have skill " + skillname + "!", new Object[1]);
        }
        return 0;
    }

    /**
     * @param statID 0 for Melee Dmg, 1 for Defense, 3 for Ki Power
     * @return Player's stat, NOT attributes i.e Melee Dmg, not STR
     */
    @Override
    public int getMaxStat(int statID) {
        return dbcData.stats.getMaxStat(statID);
    }

    /**
     * @param statID check getMaxStat
     * @return Player's stat as a percentage of MaxStat through power release i.e if MaxStat is 1000 and release is 10, returns 100
     */
    @Override
    public int getCurrentStat(int statID) {
        return dbcData.stats.getCurrentStat(statID);
    }

    /**
     * @return if Form player is in is 10x base, returns 10
     */
    @Override
    public double getCurrentFormMultiplier() {
        return dbcData.stats.getCurrentMulti();
    }

    @Override
    public int getMajinAbsorptionRace() {
        if (getRace() != 5)
            return 0;
        String s = nbt.getString("jrmcMajinAbsorptionData");
        String[] data = s.split(",");
        String value = data.length >= 3 ? data[1] : "0";
        return Integer.parseInt(value);
    }

    @Override
    public void setMajinAbsorptionRace(int race) {
        if (getRace() != 5)
            return;

        String[] data = nbt.getString("jrmcMajinAbsorptionData").split(",");
        StringBuilder str = new StringBuilder(race + ",");
        for (int i = 1; i < data.length; i++)
            str.append(Arrays.toString(data)).append(",");

        str = new StringBuilder(str.substring(0, str.length() - 1));
        nbt.setString("jrmcMajinAbsorptionData", str.toString());
    }

    @Override
    public int getMajinAbsorptionPower() {
        if (getRace() != 5)
            return 0;
        String s = nbt.getString("jrmcMajinAbsorptionData");
        return JRMCoreH.getMajinAbsorptionValueS(s);
    }

    @Override
    public void setMajinAbsorptionPower(int power) {
        if (getRace() != 5)
            return;

        String[] data = nbt.getString("jrmcMajinAbsorptionData").split(",");
        String str = power + ",";
        for (int i = 1; i < data.length; i++)
            str += data + ",";

        str = str.substring(0, str.length() - 1);
        nbt.setString("jrmcMajinAbsorptionData", str);
    }


    /**
     * @return True if player is currently KO
     */
    @Override
    public boolean isKO() {
        int currentKO = getInt(player, "jrmcHar4va");
        return currentKO > 0;
    }

    /**
     * @return True if either MUI or UI Omen
     */
    public boolean isUI() {
        return dbcData.isForm(DBCForm.UltraInstinct);
    }

    public boolean isMUI() {
        return dbcData.isForm(DBCForm.MasteredUltraInstinct);
    }

    public boolean isMystic() {
        return dbcData.isForm(DBCForm.Mystic);
    }

    public boolean isKaioken() {
        return dbcData.isForm(DBCForm.Kaioken);
    }

    public boolean isGOD() {
        return dbcData.isForm(DBCForm.GodOfDestruction);
    }

    public boolean isLegendary() {
        return dbcData.isForm(DBCForm.Legendary);
    }

    public boolean isDivine() {
        return dbcData.isForm(DBCForm.Divine);
    }

    public boolean isMajin() {
        return dbcData.isForm(DBCForm.Majin);
    }

    @Override
    public void setFlight(boolean flightOn) {
        dbcData.setFlight(flightOn);
    }

    @Override
    public boolean isFlying() {
        return dbcData.isFlying;
    }

    @Override
    public void setAllowFlight(boolean allowFlight) {
        dbcData.flightEnabled = allowFlight;
        dbcData.saveNBTData(false);
    }

    @Override
    public void setFlightSpeedRelease(int release) {
        dbcData.flightSpeedRelease = ValueUtil.clamp(release, 1, 100);
        dbcData.saveNBTData(false);
    }

    @Override
    public void setBaseFlightSpeed(int speed) {
        dbcData.baseFlightSpeed = ValueUtil.clamp(speed, 1, 10);
        dbcData.saveNBTData(false);
    }

    @Override
    public void setDynamicFlightSpeed(int speed) {
        dbcData.dynamicFlightSpeed = ValueUtil.clamp(speed, 1, 10);
        dbcData.saveNBTData(false);
    }

    @Override
    public void setFlightGravity(boolean isEffected) {
        dbcData.flightGravity = isEffected;
        dbcData.saveNBTData(false);
    }

    @Override
    public void setFlightDefaults() {
        dbcData.baseFlightSpeed = 1.0f;
        dbcData.dynamicFlightSpeed = 1.0f;
        dbcData.flightEnabled = true;
        dbcData.flightSpeedRelease = 100;
        dbcData.flightGravity = true;
        dbcData.saveNBTData(false);
    }

    //////////////////////////////////////////////
    //////////////////////////////////////////////
    // Form stuff

    public void giveForm(String formName) {
        IForm form = FormController.Instance.get(formName);
        form.assignToPlayer(player);
    }

    @Override
    public void giveForm(IForm form) {
        giveForm(form.getName());
    }

    public void removeForm(String formName) {
        IForm f = FormController.Instance.get(formName);
        f.removeFromPlayer(player);
    }

    @Override
    public void removeForm(IForm form) {
        removeForm(form.getName());
    }

    @Override
    public void setSelectedForm(IForm form) {
        setSelectedForm(form != null ? form.getID() : -1);
    }

    @Override
    public void setSelectedForm(int formID) {
        PlayerDBCInfo c = PlayerDataUtil.getDBCInfo(player);
        if (FormController.getInstance().has(formID))
            c.selectedForm = formID;
        else
            c.selectedForm = -1;

        c.updateClient();
    }

    @Override
    public void removeSelectedForm() {
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        formData.selectedForm = -1;
        formData.updateClient();
    }

    public boolean isInForm() {
        return PlayerDataUtil.getDBCInfo(player).isInCustomForm();
    }

    @Override
    public boolean isInForm(IForm form) {
        return dbcData.addonFormID == form.getID();
    }

    public boolean isInForm(int formID) {
        return PlayerDataUtil.getDBCInfo(player).isInForm(formID);
    }

    public IForm getCurrentForm() {
        return dbcData.getForm();
    }

    public void setForm(int formID) {
        Form f = (Form) FormController.Instance.get(formID);
        if (f == null)
            throw new CustomNPCsException("Form does not exist!");

        PlayerDBCInfo c = PlayerDataUtil.getDBCInfo(player);
        if (c.hasForm(f)) {
            DBCData d = DBCData.get(player);
            d.State = 0;
            if (d.isForm(DBCForm.Kaioken) && !f.stackable.isFormStackable(DBCForm.Kaioken)) {
                d.State2 = 0;
                d.setForm(DBCForm.Kaioken, false);
            }
            if (d.isForm(DBCForm.UltraInstinct) && !f.stackable.isFormStackable(DBCForm.UltraInstinct)) {
                d.State2 = 0;
                d.setForm(DBCForm.UltraInstinct, false);
            }
            if (d.isForm(DBCForm.GodOfDestruction) && !f.stackable.isFormStackable(DBCForm.GodOfDestruction)) {
                d.setForm(DBCForm.GodOfDestruction, false);
            }
            if (d.isForm(DBCForm.Mystic) && !f.stackable.isFormStackable(DBCForm.Mystic)) {
                d.setForm(DBCForm.Mystic, false);
            }

            c.currentForm = f.id;
            c.updateClient();
        } else
            throw new CustomNPCsException("Player doesn't have form " + f.name + " unlocked!");
    }

    @Override
    public void setForm(IForm form) {
        setForm(form.getID());
    }

    //////////////////////////////////////////////
    //////////////////////////////////////////////
    // Form Mastery stuff
    @Override
    public void setCustomMastery(int formID, float value) {
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        if (formData.hasFormUnlocked(formID)) {
            formData.setFormLevel(formID, value);
            formData.updateClient();
        }

    }

    @Override
    public void setCustomMastery(IForm form, float value) {
        setCustomMastery(form.getID(), value);
    }

    @Override
    public void addCustomMastery(int formID, float value) {
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        if (formData.hasFormUnlocked(formID)) {
            formData.addFormLevel(formID, value);
            formData.updateClient();
        }
    }

    @Override
    public void addCustomMastery(IForm form, float value) {
        addCustomMastery(form.getID(), value);
    }

    @Override
    public float getCustomMastery(int formID) {
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        float level = 0;
        if (formData.hasFormUnlocked(formID)) {
            level = formData.getFormLevel(formID);
        }
        return level;
    }

    @Override
    public float getCustomMastery(IForm form) {
        return getCustomMastery(form.getID());
    }

    @Override
    public void removeCustomMastery(int formID) {
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        if (formData.hasFormUnlocked(formID)) {
            formData.removeFormMastery(formID);
            formData.updateClient();
        }
    }

    @Override
    public void removeCustomMastery(IForm form) {
        removeCustomMastery(form.getID());
    }


    //////////////////////////////////////////////
    //////////////////////////////////////////////
    // Aura stuff
    @Override
    public void giveAura(IAura Aura) {
        giveAura(Aura.getName());
    }

    public void giveAura(String AuraName) {
        IAura aura = AuraController.Instance.get(AuraName);
        aura.assignToPlayer(player);
    }

    @Override
    public void removeAura(String AuraName) {
        IAura aura = AuraController.Instance.get(AuraName);
        aura.removeFromPlayer(player);
    }

    @Override
    public void removeAura(IAura Aura) {
        removeAura(Aura.getName());
    }

    @Override
    public void setSelectedAura(IAura Aura) {
        setSelectedAura(Aura != null ? Aura.getID() : -1);
    }

    @Override
    public void setSelectedAura(int Auraid) {
        PlayerDBCInfo c = PlayerDataUtil.getDBCInfo(player);
        if (AuraController.getInstance().has(Auraid))
            c.selectedAura = Auraid;
        else
            c.selectedAura = -1;

        c.updateClient();
    }

    @Override
    public void removeSelectedAura() {
        PlayerDBCInfo AuraData = PlayerDataUtil.getDBCInfo(player);
        AuraData.selectedAura = -1;
        AuraData.updateClient();
    }

    public IAura getAura() {
        return dbcData.getAura();
    }

    @Override
    public void setAura(String auraName) {
        Aura aura = (Aura) AuraController.Instance.get(auraName);
        PlayerDBCInfo c = PlayerDataUtil.getDBCInfo(player);
        if (c.hasAura(aura)) {
            c.currentAura = aura.id;
            c.updateClient();
        } else
            throw new CustomNPCsException("Player doesn't have aura " + auraName + " unlocked!");
    }

    @Override
    public void setAura(IAura Aura) {
        setAura(Aura.getName());
    }

    @Override
    public DBCData getDBCData() {
        return dbcData;
    }

    @Override
    public boolean isInAura() {
        return PlayerDataUtil.getDBCInfo(player).isInCustomAura();
    }

    @Override
    public boolean isInAura(IAura aura) {
        return aura.getID() == dbcData.auraID;
    }
    //////////////////////////////////////////////
    //////////////////////////////////////////////
}
