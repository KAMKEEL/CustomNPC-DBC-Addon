package kamkeel.npcdbc.api.effect;

import noppes.npcs.api.entity.IPlayer;

public interface IStatusEffectHandler {

    IStatusEffect createEffect(String name);

    IStatusEffect getEffect(String name);

    void deleteEffect(String name);

    /**
     *
     * @param player Player
     * @param id ID of Status Effect -- Only DBC Addon
     * @return True if Player has Effect
     */
    boolean hasEffect(IPlayer player, int id);

    boolean hasEffect(IPlayer player, IStatusEffect effect);

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

    int getEffectDuration(IPlayer player, IStatusEffect effect);

    /**
     * @param player Player
     * @param id ID of Status Effect
     * @param duration Duration of Effect in Seconds | -100 : Has infinite effect
     * @param level Level of Effect [Some effects not affected by level]
     */
    void applyEffect(IPlayer player, int id, int duration, byte level);

    void applyEffect(IPlayer player, IStatusEffect effect, int duration, byte level);

    /**
     * @param player Player
     * @param id ID of Status Effect
     */
    void removeEffect(IPlayer player, int id);

    void removeEffect(IPlayer player, IStatusEffect effect);

    void clearEffects(IPlayer player);

    ICustomEffect saveEffect(ICustomEffect customEffect);
}
