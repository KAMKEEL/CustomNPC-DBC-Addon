package kamkeel.npcdbc.api.effect;

import noppes.npcs.api.entity.IPlayer;

/**
 * Interface for handling custom effects within the NPCDBC mod.
 * <p>
 * Provides methods to check, apply, remove, and clear effects on players.
 * This includes managing effect duration, levels, and verifying the existence
 * of specific effects.
 * </p>
 */
public interface IDBCEffectHandler {

    /**
     * Checks if the specified player currently has the effect with the given ID.
     *
     * @param player the player to check
     * @param id     the effect ID to look for
     * @return {@code true} if the player has the effect, otherwise {@code false}
     */
    boolean hasEffect(IPlayer player, int id);

    /**
     * Applies the effect with the specified ID to the given player using default duration and level.
     *
     * @param player the player to apply the effect to
     * @param id     the effect ID to be applied
     */
    void applyEffect(IPlayer player, int id);

    /**
     * Applies the effect with the specified ID to the given player with a custom duration.
     *
     * @param player   the player to apply the effect to
     * @param id       the effect ID to be applied
     * @param duration the duration for which the effect should last, in ticks
     */
    void applyEffect(IPlayer player, int id, int duration);

    /**
     * Applies the effect with the specified ID to the given player with a custom duration and level.
     *
     * @param player   the player to apply the effect to
     * @param id       the effect ID to be applied
     * @param duration the duration for which the effect should last, in ticks
     * @param level    the level or strength of the effect
     */
    void applyEffect(IPlayer player, int id, int duration, byte level);

    /**
     * Clears all effects managed by the NPCDBC effect handler from the specified player.
     *
     * @param player the player from which to clear all effects
     */
    void clearDBCEffects(IPlayer player);

    /**
     * Removes the effect with the specified ID from the given player.
     *
     * @param player the player from which the effect should be removed
     * @param id     the effect ID to be removed
     */
    void removeEffect(IPlayer player, int id);

    /**
     * Retrieves the remaining duration of the effect with the specified ID for the given IPlayer.
     * Uses the DBC effect index.
     *
     * @param player the IPlayer to check
     * @param id     the effect ID
     * @return the remaining duration in seconds, or -1 if the effect is not active
     */
    int getEffectDuration(IPlayer player, int id);

    /**
     * Retrieves the level of the effect with the specified ID for the given IPlayer.
     * Uses the DBC effect index.
     *
     * @param player the IPlayer to check
     * @param id     the effect ID
     * @return the effect level, or -1 if the effect is not active
     */
    int getEffectLevel(IPlayer player, int id);
}
