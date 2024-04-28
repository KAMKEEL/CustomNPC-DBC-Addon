package kamkeel.npcdbc.api.form;

/**
 * This interface is heavily based on how DBC calculates its form masteries. Please check any race's form_mastery.cfg config to
 * get a better understanding on how this interface functions
 *
 * Example: Strength attribute is 100,000,
 *
 * A flat level of 1.5x causes this to be 150,000 out of the gate even at form level 0.
 *
 * A PerLevel of 0.01x causes the attribute to increase 1.01x per form level. At level 1, strength is 101,000. At level 100, strength is 200,000.
 * A -0.01x causes strength to be 50,000 at level 50. Negative values are useful for stuff like Ki Drain.
 *
 * A MaxLevel caps the effective multiplier of all the above. A max level of 1.5x makes sure that the flat level and perlevel multis will not go above 1.5x, even at max form level.
 * A max level of 0.1 and negative perlevel value of -0.01x makes sure that Ki Drain at max level will only be 10x lower than at level 0, and not any lower.
 *
 */
public interface IFormMastery {
    /**
     * @return percentage of max body player needs to be under to access form
     */
    float getHealthRequirement();

    void setHealthRequirement(float healthRequirementInPercent);

    /**
     * @param type 3 Legal: "flat", "perlevel", "max"
     * @return value of specified type
     */


    /**
     * @param type  4 Legal: "attribute","kidrain","strain","healthrequirement"
     * @param type1 3 Legal: "flat", "perlevel", "minormax"
     * @return value of specified type
     */
    float getMulti(String type, String type1);

    /**
     * @param type  4 Legal: "attribute","kidrain","strain","healthrequirement"
     * @param type1 3 Legal: "flat", "perlevel", "minormax"
     * @param value amount to change type1 of type by
     */
    void setMulti(String type, String type1, float value);

    /**
     * @param eventType 4 Legal: "update", "attack", "damaged", "fireki"
     * @param type      4 Legal: "gain", "flat", "permind", "max"
     * @return the value of specified type within eventType
     */
    float getGain(String eventType, String type);

    /**
     * @param eventType 4 Legal: "update", "attack", "damaged", "fireki"
     * @param type      4 Legal: "gain", "flat", "permind", "max"
     * @param value     amount to change type by within eventType
     */
    void setGain(String eventType, String type, float value);

    /**
     * @return the max level this form can reach
     */
    float getMaxLevel();

    void setMaxLevel(float value);

    /**
     * @return form level at which instant transformation unlocks
     */
    float getInstantTransformationUnlockLevel();

    void setInstantTransformationUnlockLevel(float value);

    boolean hasInstantTransformationUnlockLevel();

    float getDamageNegation();

    void setDamageNegation(float damageNegation);

    boolean hasDamageNegation();

    float getDodgeChance();

    void setDodgeChance(float dodgeChance);

    boolean hasDodge();

    int getKiDrain();

    /**
     * @param kiDrain percentage of Ki to drain every KiDrainTimer ticks
     */
    void setKiDrain(int kiDrain);

    boolean hasKiDrain();

    int getKiDrainTimer();

    /**
     * @param timeInTicks time to reduce ki by KiDrain by in ticks
     */
    void setKiDrainTimer(int timeInTicks);

    /**
     * Saves CustomForm with the New Form Mastery Modifications
     *
     * @return IFormMastery self object
     */
    IFormMastery save();
}
