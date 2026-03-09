package kamkeel.npcdbc.api.effect;

/**
 * Represents a named stat bonus that modifies a player's DBC attributes.
 * <p>
 * Each bonus has a unique name and a type that determines how it stacks:
 * <ul>
 *   <li><b>Type 0 - Percentage:</b> Values are treated as percentage modifiers of the base attribute.
 *       For example, a strength value of {@code 50} grants {@code +50%} of base strength.
 *       A value of {@code -30} reduces strength by {@code 30%} of the base value.
 *       Multiple percentage bonuses are summed together (additive stacking),
 *       and the total is clamped so it cannot reduce an attribute by more than {@code 100%}.</li>
 *   <li><b>Type 1 - Flat:</b> Values are added directly to the attribute as flat amounts.
 *       Multiple flat bonuses are summed together.</li>
 *   <li><b>Type 2 - Multiplicative:</b> Values are treated as percentage modifiers that compound
 *       multiplicatively. Each bonus is converted to a factor ({@code 1 + value/100}) and all
 *       factors are multiplied together. For example, two {@code -50} bonuses result in
 *       {@code 0.5 * 0.5 = 0.25} (75% total reduction), not 100%.
 *       Individual factors are clamped to {@code >= 0} (cannot invert an attribute).
 *       Multiplicative bonuses are applied <b>before</b> Percentage and Flat bonuses.</li>
 * </ul>
 * <p>
 * <b>Order of application:</b> Multiplicative (Type 2) → Percentage (Type 0) → Flat (Type 1).
 * The final attribute value is floored to {@code 1} to prevent division-by-zero.
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
}
