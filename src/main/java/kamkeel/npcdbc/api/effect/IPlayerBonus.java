package kamkeel.npcdbc.api.effect;

/**
 * Represents a named bonus that modifies a player's DBC attributes and/or statistics.
 * <p>
 * A single bonus carries two independent sets of values:
 * <ul>
 *   <li><b>Attributes</b> - Strength, Dexterity, Willpower, Constitution, Spirit.
 *       These are applied to the raw attribute, so everything downstream of the attribute
 *       (racial multipliers, stat scaling, release) multiplies them. A flat attribute bonus
 *       is therefore amplified by the rest of the pipeline.</li>
 *   <li><b>Statistics</b> - Melee, Defense, Body, Stamina, EnergyPower, EnergyPool, MaxSkills.
 *       These are applied to the finished statistic instead, so a flat stat bonus is not
 *       amplified by racial or stat scaling. Use these when you want a direct
 *       "+N melee damage" style bonus rather than raw attribute points.</li>
 * </ul>
 * Both sets share this bonus's {@link #getType() type}. Values left at {@code 0} are ignored,
 * so a bonus can carry attributes only, statistics only, or both.
 * <p>
 * Statistic bonuses are applied after form multipliers and stat scaling but before the
 * release percentage, so they scale with release the same way every other statistic does.
 * <p>
 * Each bonus has a unique name and a type that determines how it stacks:
 * <ul>
 *   <li><b>Type 0 - Percentage:</b> Values are a <b>fraction</b> of the base value, not a 0-100 percent.
 *       {@code 1.2} adds {@code 120%} of the base, {@code 0.5} adds {@code 50%}, and {@code -0.2}
 *       removes {@code 20%}. Multiple percentage bonuses are summed together (additive stacking),
 *       and the total is clamped to {@code -1.0} so it cannot remove more than the whole base value.</li>
 *   <li><b>Type 1 - Flat:</b> Values are added directly as flat amounts.
 *       Multiple flat bonuses are summed together.</li>
 *   <li><b>Type 2 - Multiplicative:</b> Values are a <b>0-100 style percent</b> that compounds
 *       multiplicatively. Each bonus is converted to a factor ({@code 1 + value/100}) and all
 *       factors are multiplied together. For example, two {@code -50} bonuses result in
 *       {@code 0.5 * 0.5 = 0.25} (75% total reduction), not 100%.
 *       Individual factors are clamped to {@code >= 0} (cannot invert a value).
 *       Multiplicative bonuses are applied <b>before</b> Percentage and Flat bonuses.</li>
 * </ul>
 * Note that Type 0 and Type 2 use different scales: Type 0 takes {@code 0.5} for half, Type 2 takes {@code 50}.
 * <p>
 * <b>Order of application:</b> Multiplicative (Type 2) → Percentage (Type 0) → Flat (Type 1).
 * The final value is floored to {@code 1} to prevent division-by-zero.
 * <p>
 * Bonuses are identified by name. Applying a bonus with the same name as an existing one
 * will overwrite the previous bonus.
 *
 * @see IBonusHandler
 */
public interface IPlayerBonus {

    /**
     * @return The unique name identifying this bonus
     */
    String getName();

    /**
     * Returns the bonus type.
     *
     * @return {@code 0} for Percentage, {@code 1} for Flat, {@code 2} for Multiplicative
     */
    byte getType();

    /**
     * Sets the bonus type.
     *
     * @param type {@code 0} for Percentage, {@code 1} for Flat, {@code 2} for Multiplicative
     */
    void setType(byte type);

    /**
     * @return The Strength modifier value (percentage, flat, or multiplicative depending on type)
     */
    float getStrength();

    /**
     * @param strength The Strength modifier value to set
     */
    void setStrength(float strength);

    /**
     * @return The Dexterity modifier value (percentage, flat, or multiplicative depending on type)
     */
    float getDexterity();

    /**
     * @param dexterity The Dexterity modifier value to set
     */
    void setDexterity(float dexterity);

    /**
     * @return The Willpower modifier value (percentage, flat, or multiplicative depending on type)
     */
    float getWillpower();

    /**
     * @param willpower The Willpower modifier value to set
     */
    void setWillpower(float willpower);

    /**
     * @return The Constitution modifier value (percentage, flat, or multiplicative depending on type)
     */
    float getConstitution();

    /**
     * @param constitution The Constitution modifier value to set
     */
    void setConstitution(float constitution);

    /**
     * @return The Spirit modifier value (percentage, flat, or multiplicative depending on type)
     */
    float getSpirit();

    /**
     * @param spirit The Spirit modifier value to set
     */
    void setSpirit(float spirit);

    /**
     * Returns a statistic modifier by ID.
     *
     * @param statID One of {@code Melee (0)}, {@code Defense (1)}, {@code Body (2)}, {@code Stamina (3)},
     *               {@code EnergyPower (4)}, {@code EnergyPool (5)}, {@code MaxSkills (6)}
     * @return The modifier value, or {@code 0} if the ID is outside that range
     */
    float getStat(int statID);

    /**
     * Sets a statistic modifier by ID. IDs outside the supported range are ignored.
     * <p>
     * Statistics above {@code MaxSkills} (Speed, regeneration rates, FlySpeed) do not run through
     * the DBC stat pipeline and cannot be modified by a bonus.
     *
     * @param statID One of {@code Melee (0)}, {@code Defense (1)}, {@code Body (2)}, {@code Stamina (3)},
     *               {@code EnergyPower (4)}, {@code EnergyPool (5)}, {@code MaxSkills (6)}
     * @param value  The modifier value (percentage, flat, or multiplicative depending on type)
     */
    void setStat(int statID, float value);

    /**
     * @return The Melee statistic modifier (percentage, flat, or multiplicative depending on type)
     */
    float getMelee();

    /**
     * @param melee The Melee statistic modifier to set
     */
    void setMelee(float melee);

    /**
     * @return The Defense statistic modifier (percentage, flat, or multiplicative depending on type)
     */
    float getDefense();

    /**
     * @param defense The Defense statistic modifier to set
     */
    void setDefense(float defense);

    /**
     * @return The Body statistic modifier (percentage, flat, or multiplicative depending on type)
     */
    float getBody();

    /**
     * @param body The Body statistic modifier to set
     */
    void setBody(float body);

    /**
     * @return The Stamina statistic modifier (percentage, flat, or multiplicative depending on type)
     */
    float getStamina();

    /**
     * @param stamina The Stamina statistic modifier to set
     */
    void setStamina(float stamina);

    /**
     * @return The Energy Power statistic modifier (percentage, flat, or multiplicative depending on type)
     */
    float getEnergyPower();

    /**
     * @param energyPower The Energy Power statistic modifier to set
     */
    void setEnergyPower(float energyPower);

    /**
     * @return The Energy Pool statistic modifier (percentage, flat, or multiplicative depending on type)
     */
    float getEnergyPool();

    /**
     * @param energyPool The Energy Pool statistic modifier to set
     */
    void setEnergyPool(float energyPool);

    /**
     * @return The Max Skills statistic modifier (percentage, flat, or multiplicative depending on type)
     */
    float getMaxSkills();

    /**
     * @param maxSkills The Max Skills statistic modifier to set
     */
    void setMaxSkills(float maxSkills);
}
