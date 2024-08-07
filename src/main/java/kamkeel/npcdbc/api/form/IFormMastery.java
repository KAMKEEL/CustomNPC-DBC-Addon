package kamkeel.npcdbc.api.form;

/**
 * This interface is heavily based on how DBC calculates its form masteries. Please check any race's form_mastery.cfg config to
 * get a better understanding on how this interface functions
 * <p>
 * Example: Strength attribute is 100,000,
 * <p>
 * A flat level of 1.5x causes this to be 150,000 out of the gate even at form level 0.
 * <p>
 * A PerLevel of 0.01x causes the attribute to increase 1.01x per form level. At level 1, strength is 101,000. At level 100, strength is 200,000.
 * A -0.01x causes strength to be 50,000 at level 50. Negative values are useful for stuff like Ki Drain.
 * <p>
 * A MaxLevel caps the effective multiplier of all the above. A max level of 1.5x makes sure that the flat level and perlevel multis will not go above 1.5x, even at max form level.
 * A max level of 0.1x (min in this case, thus the minormax) and a negative perlevel value of -0.01x makes sure that Ki Drain at player's max form level will only be 10x lower than at level 0, and not any lower.
 */
public interface IFormMastery {
    /**
     * @return percentage of max body player needs to be under to access form
     */
    float getHealthRequirement();

    void setHealthRequirement(float healthRequirementInPercent);

    /**
     * @param type Legal: "attribute","kidrain","heat","pain","healthrequirement,"damageNegation","dodge","tailcutchance"
     * @param type1 3 Legal: "flat", "perlevel", "minormax"
     * @return value of specified type
     */
    float getMulti(String type, String type1);

    /**
     * @param type  Legal: "attribute","kidrain","heat","pain","healthrequirement,"damageNegation","dodge","tailcutchance"
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

    int getPainTime();

    /**
     * @param painTime Time player will be in pain for after reaching 100% form heat in minutes at form level 0.
     *                 <p>
     *                 If player descends when they are at 25% of their full heat bar, they will receive the same % of pain as their heatbar.
     *                 Descending at 25% heat while having a painTime of 100 minutes will cause 25 minutes of pain
     *                 <p>
     *                 This time decreases with pain mastery. Setting this to 10 and minormax of pain to 0.0 completely disables pain
     *                 at max form level
     */
    void setPainTime(int painTime);

    boolean hasPainTime();

    int getMaxHeat();

    /**
     * @param maxHeat Seconds it takes to reach 100% heat. Setting this to 120 means player will reach full form heat in 120 seconds.
     *                Player gains 1 heat per second at form level 0 by default. When player reaches maxHeat value of heat, they receive Pain status effect for painTime minutes
     *                and they can't transform back to the same form as long as they are in pain.
     *                <p>
     *                The time the player takes to reach this maxHeat value increases with heat mastery. Setting this to 100 and minormax of pain to 0.1 means player gains 0.1 heat per second
     *                at max form level. Setting minormax to 0 completely disables heat at max form level
     */
    void setMaxHeat(int maxHeat);

    /**
     * @return True if maxHeat is greater than 1
     */
    boolean hasHeat();

    float getDamageNegation();

    /**
     * @param damageNegation Percentage value of damage to negate at form level 0. Setting this to 10 means that 10% of all incoming damage is negated, which is the
     *                       equivalent to receiving 90% of incoming damage.
     *                       Setting this to 100% means all incoming damage is negated, taking 0 damage.
     *                       <p>
     *                       This damage increases with damageNegation mastery. Setting this to 50% and max to 1.5 means player receives 1.5x the 50%, an effective
     *                       75% damage negation at max form level
     */
    void setDamageNegation(float damageNegation);

    /**
     * @return True if damageNegation is bigger than 0
     */
    boolean hasDamageNegation();

    float getDodgeChance();

    /**
     * @param dodgeChance From 0-100, the chance to dodge any incoming attack at form level 0
     *                    <p>
     *                    This chances increases with dodge mastery. Setting this to 50% and max to 1.5 means player has a 75% chance to dodge any attack
     *                    at max form level
     */
    void setDodgeChance(float dodgeChance);

    /**
     * @return True if dodgeChance is bigger than 0
     */
    boolean hasDodge();

    float getKiDrain();

    /**
     * @param kiDrain percentage of Ki to drain every KiDrainTimer ticks at form level 0
     *                <p>
     *                This drain decreases with kiDrain mastery. Setting this to 10% and min to 0.1x means player has a 1% of their ki drained per tick
     *                at max form level
     */
    void setKiDrain(float kiDrain);

    /**
     * @return True if kiDrain is greater than 0
     */
    boolean hasKiDrain();

    int getKiDrainTimer();

    /**
     * @param timeInTicks time to reduce ki by KiDrain value in ticks
     */
    void setKiDrainTimer(int timeInTicks);

    /**
     * Saves CustomForm with the New Form Mastery Modifications
     *
     * @return IFormMastery self object
     */
    IFormMastery save();


    void setPowerPointCost(int cost);
    void setPowerPointMultiNormal(float multi);
    void setPowerPointMultiBasedOnPoints(float multi);
    void setAbsorptionMulti(float multi);
    int getPowerPointCost();
    float getPowerPointMultiNormal();
    float getPowerPointMultiBasedOnPoints();
    float getAbsorptionMulti();

    boolean isAbsorptionBoostEnabled();
    boolean isPowerPointBoostEnabled();
}
