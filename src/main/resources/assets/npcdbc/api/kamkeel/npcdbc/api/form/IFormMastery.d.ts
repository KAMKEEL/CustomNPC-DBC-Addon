/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.form
 */

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
export interface IFormMastery {
    /**
     * @return percentage of max body player needs to be under to access form
     */
    getHealthRequirement(): number;
    setHealthRequirement(healthRequirementInPercent: number): void;
    /**
     * @param type  Legal: "attribute","kidrain","heat","pain","healthrequirement,"damageNegation","dodge","tailcutchance"
     * @param type1 3 Legal: "flat", "perlevel", "minormax"
     * @return value of specified type
     */
    getMulti(type: string, type1: string): number;
    /**
     * @param type  Legal: "attribute","kidrain","heat","pain","healthrequirement,"damageNegation","dodge","tailcutchance"
     * @param type1 3 Legal: "flat", "perlevel", "minormax"
     * @param value amount to change type1 of type by
     */
    setMulti(type: string, type1: string, value: number): void;
    /**
     * @param eventType 4 Legal: "update", "attack", "damaged", "fireki"
     * @param type      4 Legal: "gain", "flat", "permind", "max"
     * @return the value of specified type within eventType
     */
    getGain(eventType: string, type: string): number;
    /**
     * @param eventType 4 Legal: "update", "attack", "damaged", "fireki"
     * @param type      4 Legal: "gain", "flat", "permind", "max"
     * @param value     amount to change type by within eventType
     */
    setGain(eventType: string, type: string, value: number): void;
    /**
     * @return the max level this form can reach
     */
    getMaxLevel(): number;
    setMaxLevel(value: number): void;
    /**
     * @return form level at which instant transformation unlocks
     */
    getInstantTransformationUnlockLevel(): number;
    setInstantTransformationUnlockLevel(value: number): void;
    hasInstantTransformationUnlockLevel(): boolean;
    getPainTime(): number;
    /**
     * @param painTime Time player will be in pain for after reaching 100% form heat in minutes at form level 0.
     *                 <p>
     *                 If player descends when they are at 25% of their full heat bar, they will receive the same % of pain as their heatbar.
     *                 Descending at 25% heat while having a painTime of 100 minutes will cause 25 minutes of pain
     *                 <p>
     *                 This time decreases with pain mastery. Setting this to 10 and minormax of pain to 0.0 completely disables pain
     *                 at max form level
     */
    setPainTime(painTime: number): void;
    hasPainTime(): boolean;
    getMaxHeat(): number;
    /**
     * @param maxHeat Seconds it takes to reach 100% heat. Setting this to 120 means player will reach full form heat in 120 seconds.
     *                Player gains 1 heat per second at form level 0 by default. When player reaches maxHeat value of heat, they receive Pain status effect for painTime minutes
     *                and they can't transform back to the same form as long as they are in pain.
     *                <p>
     *                The time the player takes to reach this maxHeat value increases with heat mastery. Setting this to 100 and minormax of pain to 0.1 means player gains 0.1 heat per second
     *                at max form level. Setting minormax to 0 completely disables heat at max form level
     */
    setMaxHeat(maxHeat: number): void;
    /**
     * @return True if maxHeat is greater than 1
     */
    hasHeat(): boolean;
    getDamageNegation(): number;
    /**
     * @param damageNegation Percentage value of damage to negate at form level 0. Setting this to 10 means that 10% of all incoming damage is negated, which is the
     *                       equivalent to receiving 90% of incoming damage.
     *                       Setting this to 100% means all incoming damage is negated, taking 0 damage.
     *                       <p>
     *                       This damage increases with damageNegation mastery. Setting this to 50% and max to 1.5 means player receives 1.5x the 50%, an effective
     *                       75% damage negation at max form level
     */
    setDamageNegation(damageNegation: number): void;
    /**
     * @return True if damageNegation is bigger than 0
     */
    hasDamageNegation(): boolean;
    getDodgeChance(): number;
    /**
     * @param dodgeChance From 0-100, the chance to dodge any incoming attack at form level 0
     *                    <p>
     *                    This chances increases with dodge mastery. Setting this to 50% and max to 1.5 means player has a 75% chance to dodge any attack
     *                    at max form level
     */
    setDodgeChance(dodgeChance: number): void;
    /**
     * @return True if dodgeChance is bigger than 0
     */
    hasDodge(): boolean;
    getKiDrain(): number;
    /**
     * @param kiDrain percentage of Ki to drain every KiDrainTimer ticks at form level 0
     *                <p>
     *                This drain decreases with kiDrain mastery. Setting this to 10% and min to 0.1x means player has a 1% of their ki drained per tick
     *                at max form level
     */
    setKiDrain(kiDrain: number): void;
    /**
     * @return True if kiDrain is greater than 0
     */
    hasKiDrain(): boolean;
    getKiDrainTimer(): number;
    /**
     * @param timeInTicks time to reduce ki by KiDrain value in ticks
     */
    setKiDrainTimer(timeInTicks: number): void;
    /**
     * Saves CustomForm with the New Form Mastery Modifications
     *
     * @return IFormMastery self object
     */
    save(): import('./IFormMastery').IFormMastery;
    isAbsorptionBoostEnabled(): boolean;
    isPowerPointBoostEnabled(): boolean;
    setPowerPointCost(cost: number): void;
    setPowerPointGrowth(growth: number): void;
    getPowerPointCost(): number;
    getPowerPointGrowth(): number;
    setPowerPointMultiNormal(multi: number): void;
    setPowerPointMultiBasedOnPoints(multi: number): void;
    getPowerPointMultiNormal(): number;
    getPowerPointMultiBasedOnPoints(): number;
    setAbsorptionMulti(multi: number): void;
    getAbsorptionMulti(): number;
    setDestroyerOn(isOn: boolean): void;
    isDestroyerOn(): boolean;
    setDestroyerEnergyDamage(energyDamage: number): void;
    getDestroyerEnergyDamage(): number;
    getMasteryLinks(): import('./IFormMasteryLinkData').IFormMasteryLinkData;
}

