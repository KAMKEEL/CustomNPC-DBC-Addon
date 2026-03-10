package kamkeel.npcdbc.api.skill;

import noppes.npcs.api.entity.IPlayer;

public interface ISkill {
    /**
     * @return ID of the skill
     */
    int getId();

    /**
     * @return String literal ID.
     */
    String getStringId();

    /**
     * @param level Level to check for (if {@code level > #getMaxLevel()}, level is treated as max level)
     * @return Upgrade cost for a given level
     */
    int getTPCost(int level);

    /**
     * @param level Level to check for (if {@code level > #getMaxLevel()}, level is treated as max level)
     * @return Upgrade cost for a given level
     */
    int getMindCost(int level);

    int getMaxLevel();

    /**
     * @param level Level to check for (if {@code level > #getMaxLevel()}, level is treated as max level)
     * @return Total TP cost it takes to get from LVL 0 to given level
     */
    int getTotalTPCost(int level);

    /**
     * @param level Level to check for (if {@code level > #getMaxLevel()}, level is treated as max level)
     * @return Total Mind cost it takes to get from LVL 0 to given level
     */
    int getTotalMindCost(int level);

    /**
     * @param player Player to check
     * @return If the player has learned this skill
     */
    default boolean doesPlayerHaveSkill(IPlayer player) { return doesPlayerHaveSkill(player, 1); }

    /**
     * @param player Player to check
     * @param level  Minimum level (if {@code level > #getMaxLevel()}, level is treated as max level)
     * @return If the player has a given skill at a certain level or higher.
     */
    boolean doesPlayerHaveSkill(IPlayer player, int level);

    /**
     * Teaches the player this skill if they don't already have it unlocked. <br>
     * Does not take TP, only mind. <br>
     * Does not check if the player has enough mind. <br>
     * The event is posted only if {@code postEvent == true} **and** the player does not already have the skill. <br>
     * Equivalent of calling {@linkplain ICustomSkill#teachPlayerSkill(IPlayer, int, boolean)
     * teachPlayerSkill(player, 1, postEvent)}
     *
     * @param player    player to teach
     * @param postEvent whether the event should be posted to scripts (only posted if the player doesn't have the skill already)
     */
    default void teachPlayerSkill(IPlayer player, boolean postEvent) { teachPlayerSkill(player, 1, postEvent); }

    /**
     * Teaches the player this skill if they don't already have it unlocked. <br>
     * Does not take TP, only mind. <br>
     * The event is not posted. <br>
     * Equivalent of calling {@linkplain ICustomSkill#teachPlayerSkill(IPlayer, int, boolean) teachPlayerSkill(player, 1, false)}
     *
     * @param player player to teach
     */
    default void teachPlayerSkill(IPlayer player) { teachPlayerSkill(player, 1, false);}

    /**
     * If the player doesn't have this skill or their current level is below the one provided,
     * their level is set to the new level. <br>
     * Does not take TP, only mind. <br>
     * The event is posted only if {@code postEvent == true} **and** the skill is newly learned.
     *
     * @param player    player to teach
     * @param level     level to set (if {@code level > #getMaxLevel()}, level is treated as max level)
     * @param postEvent whether the event should be posted to scripts (only posted if the skill is newly learned)
     */
    void teachPlayerSkill(IPlayer player, int level, boolean postEvent);

    /**
     * If the player doesn't have this skill or their current level is below the one provided,
     * their level is set to the new level. <br>
     * Does not take TP, only mind. <br>
     * The event is not posted. <br>
     * Equivalent of calling {@linkplain ICustomSkill#teachPlayerSkill(IPlayer, int, boolean) teachPlayerSkill(player, level, false)}
     *
     * @param player player to teach
     * @param level  level to set (if {@code level > #getMaxLevel()}, level is treated as max level)
     */
    default void teachPlayerSkill(IPlayer player, int level) { teachPlayerSkill(player, level, false); }

    /**
     * Equivalent to {@linkplain #unlearnSkill(IPlayer, boolean) unlearnSkill(player, false)}.
     *
     * @param player Player to unlearn the skill from
     */
    default void unlearnSkill(IPlayer player) {
        unlearnSkill(player, false);
    }

    /**
     * Unlearns a skill.
     *
     * @param player Player to edit
     * @param postEvent Post the event for scripts to catch.
     */
    void unlearnSkill(IPlayer player, boolean postEvent);

    default boolean tryToProgressLevel(IPlayer player) {
        return tryToProgressLevel(player, false);
    }

    boolean tryToProgressLevel(IPlayer player, boolean postEvent);

    int getLevel(IPlayer player);
    void setLevel(IPlayer player, int level);
}
