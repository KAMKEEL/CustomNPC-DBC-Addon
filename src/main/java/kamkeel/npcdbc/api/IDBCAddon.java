package kamkeel.npcdbc.api;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.outline.IOutline;
import noppes.npcs.api.entity.IDBCPlayer;

public interface IDBCAddon extends IDBCPlayer {
    int[] getAllFullAttributes();

    /**
     * @return Player's max body
     */
    int getMaxBody();

    /**
     * @return Player's max hp (body)
     */
    int getMaxHP();

    /**
     * @return Current body as a percentage of max hp (body)
     */
    float getBodyPercentage();

    /**
     * @return Player's max ki
     */
    int getMaxKi();

    /**
     * @return Player's max stamina
     */
    int getMaxStamina();

    /**
     * @return an array of all player attributes
     */
    int[] getAllAttributes();

    /**
     * @param attri               adds attri to player stats
     * @param multiplyAddedAttris if true, adds first then multiplies by multivalue
     * @param multiValue          value to multiply with
     */
    void modifyAllAttributes(int[] attri, boolean multiplyAddedAttris, double multiValue);

    /**
     * @param Num           adds all attributes by Num
     * @param setStatsToNum sets all attributes by Num
     */
    void modifyAllAttributes(int Num, boolean setStatsToNum);

    /**
     * If not setStats, then it will ADD submitted[0] to stats[0]. Submitted must be length of 6
     *
     * @param submitted Adds all attributes by array of attributes respectively i.e atr 0 gets added to index 0 of a
     * @param setStats  sets all attributes to submitted array
     */
    void modifyAllAttributes(int[] submitted, boolean setStats);

    /**
     * 0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
     *
     * @param statid statID to multiply
     * @param multi  value to multi by
     */
    void multiplyAttribute(int statid, double multi);

    /**
     * @param multi multiplies all attributes by multi
     */
    void multiplyAllAttributes(double multi);

    /**
     * A "Full" stat is a stat that has all form multipliers calculated. i.e if base Strength is 10,000, returns 10,000.
     * if SSJ form multi is 20x and is SSJ, returns 200,000. LSSJ returns 350,000, LSSJ Kaioken x40 returns 1,000,000 and so on
     * 0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
     *
     * @param statid ID of attribute
     * @return attribute value
     */
    int getFullAttribute(int statid);

    /**
     * @return Player's race name
     */
    String getRaceName();

    /**
     * @return Name of form player is currently in
     */
    String getCurrentDBCFormName();

    /**
     * @param formName name of form to change mastery of
     * @param amount   sets the current mastery value to amount
     * @param add      adds the amount to current mastery, instead of setting it to it
     */
    void changeDBCMastery(String formName, double amount, boolean add);

    /**
     * @param formName name of form
     * @return Form mastery value
     */
    double getDBCMasteryValue(String formName);

    /**
     * @return Entire form mastery NBT string, aka data32
     */
    String getAllDBCMasteries();

    /**
     * @return True if player is fused and spectator
     */
    boolean isDBCFusionSpectator();


    /**
     * @return True if player is charging a ki attack
     */
    boolean isChargingKi();

    /**
     * @param skillname Check JRMCoreH.DBCSkillNames
     * @return skill level from 1 to 10
     */
    int getSkillLevel(String skillname);

    /**
     * @param attribute 0 for Melee Dmg, 1 for Defense, 3 for Ki Power
     * @return Player's stat, NOT attributes i.e Melee Dmg, not STR
     */
    int getMaxStat(int attribute);

    /**
     * @param attribute check getMaxStat
     * @return Player's stat as a percentage of MaxStat through power release i.e if MaxStat is 1000 and release is 10, returns 100
     */
    int getCurrentStat(int attribute);

    /**
     * @return if Form player is in is 10x base, returns 10
     */
    double getCurrentFormMultiplier();

    int getMajinAbsorptionRace();

    void setMajinAbsorptionRace(int race);

    int getMajinAbsorptionPower();

    void setMajinAbsorptionPower(int power);

    /**
     * @return True if player is transformed into white MUI
     */
    boolean isMUI();

    /**
     * @return True if player is currently KO
     */
    boolean isKO();

    /**
     * @return True if either MUI or UI Omen
     */
    boolean isUI();

    /**
     * @return True if Mystic
     */
    boolean isMystic();

    /**
     * @return True if in Kaioken
     */
    boolean isKaioken();

    /**
     * @return True if in GoD
     */
    boolean isGOD();

    /**
     * @return True if in Legendary
     */
    boolean isLegendary();

    /**
     * @return True if Divine
     */
    boolean isDivine();

    boolean isMajin();

    void setForm(int formID);

    void setForm(IForm form);

    void setFlight(boolean flightOn);

    boolean isFlying();

    void setAllowFlight(boolean allowFlight);

    void setFlightSpeedRelease(int release);

    void setBaseFlightSpeed(float speed);

    void setDynamicFlightSpeed(float speed);

    void setFlightGravity(boolean isEffected);

    void setFlightDefaults();

    void setSprintSpeed(float speed);

    void giveForm(String formName);

    void giveForm(IForm form);

    void removeForm(String formName);

    void removeForm(IForm form);

    //form player transforms to on transformation
    void setSelectedForm(IForm form);


    void setSelectedForm(int formID);

    void removeSelectedForm();

    //////////////////////////////////////////////
    //////////////////////////////////////////////
    // Aura stuff
    void setAura(String auraName);

    void setAura(IAura Aura);

    void removeCustomMastery(IForm form);

    void giveAura(IAura Aura);

    void removeAura(String AuraName);

    void removeAura(IAura Aura);

    void setSelectedAura(IAura Aura);

    void setSelectedAura(int formID);

    void removeSelectedAura();

    /**
     * @return True if player is in any CNPC+ custom form
     */
    boolean isInForm();

    boolean isInForm(IForm form);

    /**
     * @param formID
     * @return True if player is in formID
     */
    boolean isInForm(int formID);

    void setCustomMastery(int formID, float value);

    void setCustomMastery(IForm form, float value);

    void addCustomMastery(int formID, float value);

    void addCustomMastery(IForm form, float value);

    float getCustomMastery(int formID);

    float getCustomMastery(IForm form);

    void removeCustomMastery(int formID);

    IForm getCurrentForm();

    DBCData getDBCData();

    boolean isInAura();

    boolean isInAura(IAura aura);

    void setOutline(IOutline outline);

    IOutline getOutline();
}
