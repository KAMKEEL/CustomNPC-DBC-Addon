package kamkeel.npcdbc.api;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.outline.IOutline;
import noppes.npcs.api.entity.IDBCPlayer;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.entity.IPlayer;

/**
 * This interface acts as a replacement for {@link IPlayer#getDBCPlayer()}
 *
 * It extends features of the base IDBCPlayer as well adds
 * functionality to integrate it with the various CNPC+ DBC Addon frameworks such as custom forms and auras.
 */
@SuppressWarnings({"rawtypes", "unused"})
public interface IDBCAddon extends IDBCPlayer {
    /**
     * @return Refer to {@link IDBCAddon#getAllAttributes()} and {@link IDBCAddon#getFullAttribute(int)}
     */
    int[] getAllFullAttributes();

    /**
     * @return The mind a player is currently using for their skills
     */
    int getUsedMind();

    /**
     * @return The mind the player has currently available.
     */
    int getAvailableMind();

    /**
     * Set a players lock on state!
     * @param lockOnTarget Reference to new target Entity or null to remove lock on.
     */
    void setLockOnTarget(IEntityLivingBase lockOnTarget);

    /**
     * This will only work if the player has the ki fist skill
     * @param on Should ki fist be enabled
     */
    void setKiFistOn(boolean on);

    /**
     * This will only work if the player has the ki protection skill
     * @param on Should ki protection be enabled
     */
    void setKiProtectionOn(boolean on);

    /**
     * This will only work if the player has Ki Fist & Ki Infuse skills <br>
     * <br>
     * 0 - No ki weapons <br>
     * 1 - Ki Blade <br>
     * 2 - Ki Scythe <br>
     * @param type ki weapon type
     */
    void setKiWeaponType(int type);

    /**
     * @return True if ki fist is enabled
     */
    boolean kiFistOn();

    /**
     * @return True if ki protection is enabled
     */
    boolean kiProtectionOn();

    /**
     * Checks what ki weapon the player has enabled <br>
     * <br>
     * 0 - No ki weapons <br>
     * 1 - Ki Blade <br>
     * 2 - Ki Scythe <br>
     * @return The ki weapon type
     */
    int getKiWeaponType();

    /**
     * Checks if player has turbo on.
     * @return True or false
     */
    boolean isTurboOn();

    /**
     * Sets the turbo state for the player
     * @param on true turns turbo on, false turns it off.
     */
    void setTurboState(boolean on);

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
     * @param skillname Acceptable skill names:
     *                  <code>"Fusion", "Jump", "Dash", "Fly", "Endurance", <br>
     *                  "PotentialUnlock", "KiSense", "Meditation", "Kaioken", "GodForm", <br>
     *                  "OldKaiUnlock", "KiProtection", "KiFist", "KiBoost", "DefensePenetration", <br>
     *                  "KiInfuse", "UltraInstinct", "InstantTransmission", "GodOfDestruction"</code>
     * @return skill level from 1 to 10. Or 0 if the player doesn't have that skill
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
     * @return True if in God of Destruction
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

    /**
     * @return True if player is using Majin
     */
    boolean isMajin();

    /**
     * If the player has a given form, it forces them to transform to that form.
     * @param formID Form ID of the custom form you want to force the player into
     */
    void setCustomForm(int formID);

    /**
     * If the player has a given form, it forces them to transform to that form.
     * @param form Form object of the custom form you want to force the player into
     */
    void setCustomForm(IForm form);

    /**
     * If the player has a given form or <code>ignoreUnlockCheck</code> is true, it forces them to transform to that form.
     * @param formID Form ID of the custom form you want to force the player into
     * @param ignoreUnlockCheck Should this ignore checking if a player has a form unlocked
     */
    void setCustomForm(int formID, boolean ignoreUnlockCheck);

    /**
     * If the player has a given form or <code>ignoreUnlockCheck</code> is true, it forces them to transform to that form.
     * @param form Form object of the custom form you want to force the player into
     * @param ignoreUnlockCheck Should this ignore checking if a player has a form unlocked
     */
    void setCustomForm(IForm form, boolean ignoreUnlockCheck);

    void setFlight(boolean flightOn);

    boolean isFlying();

    void setAllowFlight(boolean allowFlight);

    void setFlightSpeedRelease(int release);

    void setBaseFlightSpeed(float speed);

    void setDynamicFlightSpeed(float speed);

    void setFlightGravity(boolean isEffected);

    void setFlightDefaults();

    void setSprintSpeed(float speed);

    boolean hasCustomForm(String formName);

    boolean hasCustomForm(int formID);

    IForm[] getCustomForms();

    /**
     * Gives a player a custom form by the given name.
     * @param formName Name of the form to give the player.
     */
    void giveCustomForm(String formName);

    /**
     * Gives a player a custom form.
     * @param form Form object to give the player.
     */
    void giveCustomForm(IForm form);

    /**
     * Removes the given form by name.
     * <br><br>
     * Removing mastery is dependent on configs
     * @param formName Name of the form to remove.
     */
    void removeCustomForm(String formName);

    /**
     * Removes the given form.
     * <br><br>
     * Removing mastery is dependent on configs
     * @param form Form object to remove.
     */
    void removeCustomForm(IForm form);

    /**
     * Removes the given form by name.
     * @param formName Name of the form to remove.
     * @param removesMastery does removing this form remove mastery
     */
    void removeCustomForm(String formName, boolean removesMastery);

    /**
     * Removes the given form.
     * @param form Form object to remove
     * @param removesMastery does removing this form remove mastery
     */
    void removeCustomForm(IForm form, boolean removesMastery);

    /**
     * Selects a custom form for the player.
     * <br>
     * This does not force a transformation. <br>
     * It only selects the form they will transform into whenever they choose to transform.
     * @param form Form object.
     */
    void setSelectedForm(IForm form);

    /**
     * Selects a custom form for the player.
     * <br>
     * This does not force a transformation. <br>
     * It only selects the form they will transform into whenever they choose to transform.
     * @param formID Form ID.
     */
    void setSelectedForm(int formID);

    /**
     * Clears form selection.
     */
    void removeSelectedForm();

    //////////////////////////////////////////////
    //////////////////////////////////////////////
    // Aura stuff

    /**
     * Sets the current custom aura <b>if the player has it unlocked.</b>
     * @param auraName Name of the aura to set.
     */
    void setAuraSelection(String auraName);

    /**
     * Sets the current custom aura <b>if the player has it unlocked.</b>
     * @param aura Aura object to set.
     */
    void setAuraSelection(IAura aura);

    /**
     * Sets the current custom aura <b>if the player has it unlocked.</b>
     * @param auraID ID of the aura to set.
     */
    void setAuraSelection(int auraID);

    /**
     * Unlocks the given aura for the player.
     * @param auraName Name of the aura to unlock.
     */
    void giveAura(String auraName);

    boolean hasAura(String auraName);

    boolean hasAura(int auraId);

    /**
     * Unlocks the given aura for the player.
     * @param aura Aura object to unlock for the player.
     */
    void giveAura(IAura aura);

    /**
     * Unlocks the given aura for the player.
     * @param auraID ID of the aura to unlock for the player.
     */
    void giveAura(int auraID);

    /**
     * Removes an aura from the player.
     * @param auraName Name of the aura to remove.
     */
    void removeAura(String auraName);

    /**
     * Remove an aura from the player.
     * @param aura Aura object to remove.
     */
    void removeAura(IAura aura);

    /**
     * Remove an aura from the player.
     * @param auraID ID of the aura to remove.
     */
    void removeAura(int auraID);

    /**
     * Sets the custom aura for the player <br>
     * <h2>THE PLAYER DOESN'T NEED TO HAVE THIS AURA UNLOCKED</h2>
     * @param auraName ID of the aura.
     */
    void setAura(String auraName);

    /**
     * Sets the custom aura for the player <br>
     * <h2>THE PLAYER DOESN'T NEED TO HAVE THIS AURA UNLOCKED</h2>
     * @param aura Aura object
     */
    void setAura(IAura aura);

    /**
     * Sets the custom aura for the player <br>
     * <h2>THE PLAYER DOESN'T NEED TO HAVE THIS AURA UNLOCKED</h2>
     * @param auraID ID of the aura.
     */
    void setAura(int auraID);

    /**
     * Removes a custom aura selection and current aura running for the player.
     */
    void removeCurrentAura();

    /**
     * Removes a custom aura selection for the player.
     */
    void removeAuraSelection();

    /**
     * @return True if player is in any CNPC+ custom form
     */
    boolean isInCustomForm();

    /**
     * @param form Form object.
     * @return True if the player is in a given form.
     */
    boolean isInCustomForm(IForm form);

    /**
     * @param formID formID
     * @return True if player is in formID
     */
    boolean isInCustomForm(int formID);

    /**
     * Sets the mastery of a form if the player has it unlocked, otherwise it won't change anything.
     * @param formID ID of the form to change the mastery of.
     * @param value New mastery value.
     */
    void setCustomMastery(int formID, float value);

    /**
     * Sets the mastery of a form if the player has it unlocked, otherwise it won't change anything.
     * @param form The form you want to change the mastery of.
     * @param value New mastery value.
     */
    void setCustomMastery(IForm form, float value);

    /**
     * Sets the mastery of a form.
     * @param formID ID of the form to change the mastery of.
     * @param value New mastery value.
     * @param ignoreUnlockCheck If set to true, it will adjust the form mastery even if the player doesn't have the form unlocked.
     */
    void setCustomMastery(int formID, float value, boolean ignoreUnlockCheck);

    /**
     * Sets the mastery of a form.
     * @param form The form you want to change the mastery of.
     * @param value New mastery value.
     * @param ignoreUnlockCheck If set to true, it will adjust the form mastery even if the player doesn't have the form unlocked.
     */
    void setCustomMastery(IForm form, float value, boolean ignoreUnlockCheck);

    /**
     * Modifies the mastery of a form ONLY if the player has the form unlocked.
     * @param formID ID of the form to change the mastery of.
     * @param value Amount of mastery to add.
     */
    void addCustomMastery(int formID, float value);

    /**
     * Modifies the mastery of a form ONLY if the player has the form unlocked.
     * @param form The form you want to change the mastery of.
     * @param value Amount of mastery to add.
     */
    void addCustomMastery(IForm form, float value);

    /**
     * Modifies the mastery of a form.
     * @param formID ID of the form to change the mastery of.
     * @param value Amount of mastery to add.
     * @param ignoreUnlockCheck If set to true, it will adjust the form mastery even if the player doesn't have the form unlocked.
     */
    void addCustomMastery(int formID, float value, boolean ignoreUnlockCheck);

    /**
     * Modifies the mastery of a form.
     * @param form The form you want to change the mastery of.
     * @param value Amount of mastery to add.
     * @param ignoreUnlockCheck If set to true, it will adjust the form mastery even if the player doesn't have the form unlocked.
     */
    void addCustomMastery(IForm form, float value, boolean ignoreUnlockCheck);

    /**
     * Check the form mastery. If the player is fused, it sums masteries of both players.
     * @param formID Form ID to check.
     * @return Mastery of the given form.
     */
    float getCustomMastery(int formID);

    /**
     * Check the form mastery. If the player is fused, it sums masteries of both players.
     * @param form Form to check.
     * @return Mastery of the given form.
     */
    float getCustomMastery(IForm form);

    /**
     * Check the form mastery.
     * @param formID Form ID to check.
     * @param checkFusion Should it perform a fusion check? If the player is fused, it sums masteries of both players.
     * @return Mastery of the given form.
     */
    float getCustomMastery(int formID, boolean checkFusion);

    /**
     * Check the form mastery.
     * @param form Form to check.
     * @param checkFusion Should it perform a fusion check? If the player is fused, it sums masteries of both players.
     * @return Mastery of the given form.
     */
    float getCustomMastery(IForm form, boolean checkFusion);

    void removeCustomMastery(int formID);

    void removeCustomMastery(IForm form);

    IForm getCurrentForm();

    boolean isInAura();

    boolean isInAura(IAura aura);

    boolean isInAura(String auraName);

    boolean isInAura(int auraID);

    void setOutline(IOutline outline);

    void setOutline(String outlineName);

    void setOutline(int outlineID);

    IOutline getOutline();

    /**
     * @return IPlayer object of the player you are fused with
     */
    IPlayer<?> getFusionPartner();
    /**
     * Fires a Ki Attack in the Head Direction of the NPC
     * @param type          Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     * @param speed         Speed of Ki Attack [0 - 100]
     * @param damage        Damage for Ki Attack
     * @param hasEffect     True for Explosion
     * @param color         Color of Ki Attack [0 - 30] -&gt; <br>
     *                      0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta", <br>
     *                      11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple", <br>
     *                      21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     * @param density       Density of Ki Attack &gt; 0
     * @param hasSound      Play Impact Sound of Ki Attack
     * @param chargePercent Charge Percentage of Ki Attack [0 - 100]
     */
    void fireKiAttack(byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent);
    /**
     * Fires an IKiAttack with its internal params
     * @param kiAttack ki attack to shoot
     */
    void fireKiAttack(IKiAttack kiAttack);
    /**
     *
     * @return True if player is releasing ki
     */
    boolean isReleasing();
    /**
     *
     * @return True if player is releasing ki and has meditation skill
     */
    boolean isMeditating();
    /**
     *
     * @return True if player is using majin super regen
     */
    boolean isSuperRegen();
    /**
     *
     * @return True if player is dodging/swooping
     */
    boolean isSwooping();

    boolean isInMedicalLiquid();

    IKiAttack getCurrentSelectedAttack();
}
