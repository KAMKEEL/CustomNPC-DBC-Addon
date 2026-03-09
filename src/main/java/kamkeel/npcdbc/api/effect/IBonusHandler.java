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
 *   <li><b>Type 0 - Percentage:</b> Modifies attributes by a percentage of the base value.
 *       Multiple percentage bonuses stack additively (e.g. two {@code +25} bonuses = {@code +50%}).
 *       The total percentage is clamped to {@code -100%} minimum to prevent negative attributes.</li>
 *   <li><b>Type 1 - Flat:</b> Adds a flat amount directly to the attribute.
 *       Multiple flat bonuses stack additively.</li>
 *   <li><b>Type 2 - Multiplicative:</b> Modifies attributes by a compounding percentage.
 *       Each bonus is converted to a factor ({@code 1 + value/100}) and all factors are
 *       multiplied together. Two {@code -50} bonuses = {@code 0.5 * 0.5 = 0.25} (75% reduction).
 *       Applied <b>before</b> Percentage and Flat bonuses.</li>
 * </ul>
 * <p>
 * <b>Order of application:</b> Multiplicative (Type 2) → Percentage (Type 0) → Flat (Type 1).
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
     * Values are percentage modifiers: {@code 50} = +50% of base, {@code -30} = -30% of base.
     *
     * @param name Unique name for this bonus
     * @param str  Strength percentage modifier
     * @param dex  Dexterity percentage modifier
     * @param wil  Willpower percentage modifier
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
     * @param type {@code 0} for Percentage (values are % of base attribute, additive stacking),
     *             {@code 1} for Flat (values are added directly),
     *             {@code 2} for Multiplicative (values are compounding %, each converts to factor {@code 1 + value/100})
     * @param str  Strength modifier
     * @param dex  Dexterity modifier
     * @param wil  Willpower modifier
     * @param con  Constitution modifier
     * @param spi  Spirit modifier
     * @return The created bonus (not yet applied)
     */
    IPlayerBonus createBonus(String name, int type, float str, float dex, float wil, float con, float spi);

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
     *
     * @param player The player to apply the bonus to
     * @param name   Unique name for this bonus
     * @param str    Strength percentage modifier
     * @param dex    Dexterity percentage modifier
     * @param wil    Willpower percentage modifier
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
