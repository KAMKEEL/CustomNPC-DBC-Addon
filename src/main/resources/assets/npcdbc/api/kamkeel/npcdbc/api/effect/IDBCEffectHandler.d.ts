/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.effect
 */

/**
 * Interface for handling custom effects within the NPCDBC mod.
 * <p>
 * Provides methods to check, apply, remove, and clear effects on players.
 * This includes managing effect duration, levels, and verifying the existence
 * of specific effects.
 * </p>
 */
export interface IDBCEffectHandler {
    /**
     * Checks if the specified player currently has the effect with the given ID.
     *
     * @param player the player to check
     * @param id     the effect ID to look for
     * @return {@code true} if the player has the effect, otherwise {@code false}
     */
    hasEffect(player: IPlayer, id: number): boolean;
    /**
     * Applies the effect with the specified ID to the given player using default duration and level.
     *
     * @param player the player to apply the effect to
     * @param id     the effect ID to be applied
     */
    applyEffect(player: IPlayer, id: number): void;
    /**
     * Applies the effect with the specified ID to the given player with a custom duration.
     *
     * @param player   the player to apply the effect to
     * @param id       the effect ID to be applied
     * @param duration the duration for which the effect should last, in ticks
     */
    applyEffect(player: IPlayer, id: number, duration: number): void;
    /**
     * Applies the effect with the specified ID to the given player with a custom duration and level.
     *
     * @param player   the player to apply the effect to
     * @param id       the effect ID to be applied
     * @param duration the duration for which the effect should last, in ticks
     * @param level    the level or strength of the effect
     */
    applyEffect(player: IPlayer, id: number, duration: number, level: number): void;
    /**
     * Clears all effects managed by the NPCDBC effect handler from the specified player.
     *
     * @param player the player from which to clear all effects
     */
    clearDBCEffects(player: IPlayer): void;
    /**
     * Removes the effect with the specified ID from the given player.
     *
     * @param player the player from which the effect should be removed
     * @param id     the effect ID to be removed
     */
    removeEffect(player: IPlayer, id: number): void;
    /**
     * Retrieves the remaining duration of the effect with the specified ID for the given IPlayer.
     * Uses the DBC effect index.
     *
     * @param player the IPlayer to check
     * @param id     the effect ID
     * @return the remaining duration in seconds, or -1 if the effect is not active
     */
    getEffectDuration(player: IPlayer, id: number): number;
    /**
     * Retrieves the level of the effect with the specified ID for the given IPlayer.
     * Uses the DBC effect index.
     *
     * @param player the IPlayer to check
     * @param id     the effect ID
     * @return the effect level, or -1 if the effect is not active
     */
    getEffectLevel(player: IPlayer, id: number): number;
}

