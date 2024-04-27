package kamkeel.npcdbc.api.effect;

import noppes.npcs.api.entity.IPlayer;

public interface IStatusEffectHandler {

    /**
     *
     * @param player Player
     * @param id ID of Status Effect -- Only DBC Addon
     * @return True if Player has Effect
     */
    boolean hasEffect(IPlayer player, int id);

    /**
     *
     * -1 : Does not have effect
     * -100 : Has infinite effect
     *
     * @param player Player
     * @param id ID of Status Effect -- Only DBC Addon
     * @return Effect Time
     */
    int getEffectDuration(IPlayer player, int id);

    /**
     * @param player Player
     * @param id ID of Status Effect
     * @param duration Duration of Effect in Seconds | -100 : Has infinite effect
     * @param level Level of Effect [Some effects not affected by level]
     */
    void applyEffect(IPlayer player, int id, int duration, byte level);

    /**
     * @param player Player
     * @param id ID of Status Effect
     */
    void removeEffect(IPlayer player, int id);
}
