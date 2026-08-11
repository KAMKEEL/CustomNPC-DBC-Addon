package kamkeel.npcdbc.api.effect;

import noppes.npcs.api.entity.IPlayer;

/**
 * Handles the creation, application, and removal of player stat bonuses.
 * <p>
 * Bonuses modify a player's DBC attributes (Strength, Dexterity, Willpower, Constitution, Spirit).
 * Each bonus is identified by a unique name per player. Applying a bonus with the same name
 * as an existing one will overwrite it.
 * <p>
 * <b>Bonus Types:</b>
 * <ul>
 *   <li><b>Type 0 - Percentage:</b> Values are a <b>fraction</b> of the base value, not a 0-100 percent.
 *       {@code 1.2} adds {@code 120%} of the base, {@code -0.2} removes {@code 20%}.
 *       Multiple percentage bonuses stack additively, and the total is clamped to {@code -1.0}
 *       so it cannot remove more than the whole base value.</li>
 *   <li><b>Type 1 - Flat:</b> Adds a flat amount directly.
 *       Multiple flat bonuses stack additively.</li>
 *   <li><b>Type 2 - Multiplicative:</b> Values are a <b>0-100 style percent</b> that compounds.
 *       Each bonus is converted to a factor ({@code 1 + value/100}) and all factors are
 *       multiplied together. Two {@code -50} bonuses = {@code 0.5 * 0.5 = 0.25} (75% reduction).
 *       Applied <b>before</b> Percentage and Flat bonuses.</li>
 * </ul>
 * Note that Type 0 and Type 2 use different scales: Type 0 takes {@code 0.5} for half, Type 2 takes {@code 50}.
 * <p>
 * <b>Order of application:</b> Multiplicative (Type 2) → Percentage (Type 0) → Flat (Type 1).
 * <p>
 * A bonus can also modify statistics (Melee, Defense, Body, Stamina, EnergyPower, EnergyPool,
 * MaxSkills) instead of, or alongside, attributes. Use {@link #createBonus(String, int)} and the
 * statistic setters on {@link IPlayerBonus} for that. Attribute bonuses feed the start of the
 * calculation and are amplified by everything downstream; statistic bonuses are applied to the
 * finished statistic, which is what you want for a direct "+N melee damage" style bonus.
 *
 * @see IPlayerBonus
 */
public interface IBonusHandler {

    /**
     * Removes all active bonuses from the given player.
     *
     * @param player The player to clear bonuses from
     */
    void clearBonuses(IPlayer player);

    /**
     * Creates a Percentage bonus (type 0) with Strength, Dexterity, and Willpower modifiers.
     * Constitution and Spirit default to {@code 0}.
     * <p>
     * Values are fractions of the base: {@code 0.5} = +50% of base, {@code -0.3} = -30% of base.
     *
     * @param name Unique name for this bonus
     * @param str  Strength fraction of base
     * @param dex  Dexterity fraction of base
     * @param wil  Willpower fraction of base
     * @return The created bonus (not yet applied)
     */
    IPlayerBonus createBonus(String name, float str, float dex, float wil);

    /**
     * Creates a Flat bonus (type 1) with all five attribute modifiers.
     * Values are added directly to the attributes.
     *
     * @param name Unique name for this bonus
     * @param str  Strength flat amount
     * @param dex  Dexterity flat amount
     * @param wil  Willpower flat amount
     * @param con  Constitution flat amount
     * @param spi  Spirit flat amount
     * @return The created bonus (not yet applied)
     */
    IPlayerBonus createBonus(String name, float str, float dex, float wil, float con, float spi);

    /**
     * Creates a bonus with a specified type and all five attribute modifiers.
     *
     * @param name Unique name for this bonus
     * @param type {@code 0} for Percentage (values are a fraction of the base, additive stacking),
     *             {@code 1} for Flat (values are added directly),
     *             {@code 2} for Multiplicative (values are a compounding 0-100 percent, each converts to factor {@code 1 + value/100})
     * @param str  Strength modifier
     * @param dex  Dexterity modifier
     * @param wil  Willpower modifier
     * @param con  Constitution modifier
     * @param spi  Spirit modifier
     * @return The created bonus (not yet applied)
     */
    IPlayerBonus createBonus(String name, int type, float str, float dex, float wil, float con, float spi);

    /**
     * Creates an empty bonus of the given type, with every attribute and statistic at {@code 0}.
     * Set the values you want through the {@link IPlayerBonus} setters, then apply it with
     * {@link #applyBonus(IPlayer, IPlayerBonus)}.
     * <p>
     * This is the entry point for statistic bonuses, for example a flat melee increase:
     * <pre>
     * var bonus = handler.createBonus("angel_tunic", 1);
     * bonus.setMelee(500);
     * handler.applyBonus(player, bonus);
     * </pre>
     *
     * @param name Unique name for this bonus
     * @param type {@code 0} for Percentage, {@code 1} for Flat, {@code 2} for Multiplicative
     * @return The created bonus (not yet applied)
     */
    IPlayerBonus createBonus(String name, int type);

    /**
     * Checks if the player has an active bonus with the given name.
     *
     * @param player The player to check
     * @param name   The bonus name to look for
     * @return {@code true} if the bonus is active
     */
    boolean hasBonus(IPlayer player, String name);

    /**
     * Checks if the player has an active bonus matching the given bonus's name.
     *
     * @param player The player to check
     * @param bonus  The bonus whose name to look for
     * @return {@code true} if a bonus with the same name is active
     */
    boolean hasBonus(IPlayer player, IPlayerBonus bonus);

    /**
     * Creates and applies a Percentage bonus (type 0) with Strength, Dexterity, and Willpower modifiers.
     * If a bonus with the same name already exists, it will be overwritten.
     * <p>
     * Values are fractions of the base: {@code 0.5} = +50% of base, {@code -0.3} = -30% of base.
     *
     * @param player The player to apply the bonus to
     * @param name   Unique name for this bonus
     * @param str    Strength fraction of base
     * @param dex    Dexterity fraction of base
     * @param wil    Willpower fraction of base
     */
    void applyBonus(IPlayer player, String name, float str, float dex, float wil);

    /**
     * Applies a previously created bonus to the player.
     * If a bonus with the same name already exists, it will be overwritten.
     *
     * @param player The player to apply the bonus to
     * @param bonus  The bonus to apply
     */
    void applyBonus(IPlayer player, IPlayerBonus bonus);

    /**
     * Removes an active bonus from the player by name.
     * Does nothing if no bonus with the given name exists.
     *
     * @param player The player to remove the bonus from
     * @param name   The name of the bonus to remove
     */
    void removeBonus(IPlayer player, String name);

    /**
     * Removes an active bonus from the player matching the given bonus's name.
     * Does nothing if no bonus with the given name exists.
     *
     * @param player The player to remove the bonus from
     * @param bonus  The bonus whose name identifies which bonus to remove
     */
    void removeBonus(IPlayer player, IPlayerBonus bonus);
}
