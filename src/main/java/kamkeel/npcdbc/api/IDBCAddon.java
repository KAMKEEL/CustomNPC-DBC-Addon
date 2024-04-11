package kamkeel.npcdbc.api;

import noppes.npcs.api.entity.IDBCPlayer;

public interface IDBCAddon extends IDBCPlayer {
    int[] getAllFullStats();

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
     *
     * @param attri adds attri to player stats
     * @param multiplyAddedAttris if true, adds first then multiplies by multivalue
     * @param multiValue  value to multiply with
     */
    void modifyAllAttributes(int[] attri, boolean multiplyAddedAttris, double multiValue);

    /**
     *
     * @param Num adds all attributes by Num
     * @param setStatsToNum sets all attributes by Num
     */
    void modifyAllAttributes(int Num, boolean setStatsToNum);

    /**
     * If not setStats, then it will ADD submitted[0] to stats[0]. Submitted must be length of 6
     * @param submitted Adds all attributes by array of attributes respectively i.e atr 0 gets added to index 0 of a
     * @param setStats sets all attributes to submitted array
     */
    void modifyAllAttributes(int[] submitted, boolean setStats);

    /**
     *  0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
     * @param statid statID to multiply
     * @param multi value to multi by
     */
    void multiplyAttribute(int statid, double multi);

    /**
     *
     * @param multi multiplies all attributes by multi
     */
    void multiplyAllAttributes(double multi);

    /**
     * A "Full" stat is a stat that has all form multipliers calculated. i.e if base Strength is 10,000, returns 10,000.
     *  if SSJ form multi is 20x and is SSJ, returns 200,000. LSSJ returns 350,000, LSSJ Kaioken x40 returns 1,000,000 and so on
     *  0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
     * @param statid ID of Stat
     * @return stat value
     */
    int getFullStat(int statid);

    /**
     * @return Name of race ID
     * @param race 0 to 5
     */
    String getRaceName(int race);

    /**
     * @return Player's race name
     */
    String getRaceName();

    /**
     * @param race ID (0 to 5),
     * @param form ID (0 to 3 for humans/namekians, 0 to 14 for Saiyans/Half, 0 to 7 for arcosians, 0 to 4 for majins)
     * @return form name i.e "SSFullPow"
     */
    String getFormName(int race, int form);

    /**
     * @return Name of form player is currently in
     */
    String getCurrentFormName();

    /**
     * @param formName name of form to change mastery of
     * @param amount   sets the current mastery value to amount
     * @param add      adds the amount to current mastery, instead of setting it to it
     */
    void changeFormMastery(String formName, double amount, boolean add);

    /**
     * @param formName name of form
     * @return Form mastery value
     */
    double getFormMasteryValue(String formName);

    /**
     * @return Entire form mastery NBT string, aka data32
     */
    String getAllFormMasteries();

    /**
     * @param raceid Race ID
     * @param formId Form ID
     * @return All  config data for a Form Mastery i.e Max Level, Instant Transform Unlock, Required Masteries
     */
    String[] getAllFormMasteryData(int raceid, int formId);

    /**
     * @param race Race ID
     * @param nonRacial nonRacial forms are Kaioken/UI/Mystic/GOD
     * @return Number of forms a race has, i.e Saiyan has 14 racial and 4 non racial
     */
    int getAllFormsLength(int race, boolean nonRacial);

    /**
     * @param race Race ID
     * @param nonRacial check getAllFormsLength(int race, boolean nonRacial)
     * @return An array containing all forms the race has
     */
    String[] getAllForms(int race, boolean nonRacial);

    /**
     * @return True if player is fused and spectator
     */
    boolean isDBCFusionSpectator();

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

    /**
     * @return True if player is transformed into white MUI
     */
    boolean isMUI();
}
