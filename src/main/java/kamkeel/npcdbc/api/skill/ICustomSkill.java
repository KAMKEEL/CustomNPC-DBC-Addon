package kamkeel.npcdbc.api.skill;

import noppes.npcs.api.entity.IPlayer;

public interface ICustomSkill {
    /**
     * @return ID of the skill
     */
    int getId();

    /**
     * @return String literal ID.
     */
    String getStringId();

    /**
     * @return Name of the skill as it appears in the menu.
     */
    String getDisplayName();

    /**
     * Set a new display name for the skill.
     * @param name Name to set
     */
    void setDisplayName(String name);

    /**
     * @return Skill's description that shows in the menu
     */
    String getDescription();

    /**
     * Set a new description for a skill
     * @param description new description
     */
    void setDescription(String description);

    /**
     * @param level Level to check for (if <code>level > #getMaxLevel()</code>, level is treated as max level)
     * @return Upgrade cost for a given level
     */
    int getTPCost(int level);

    /**
     * @param level Level to check for (if <code>level > #getMaxLevel()</code>, level is treated as max level)
     * @return Upgrade cost for a given level
     */
    int getMindCost(int level);
    int getMaxLevel();

    /**
     * Set a new max level
     * @param level Level in range <code>1 <= level <= 10</code>
     */
    void setMaxLevel(int level);

    /**
     * @param level Level to check for (if <code>level > #getMaxLevel()</code>, level is treated as max level)
     * @return Total TP cost it takes to get from LVL 0 to given level
     */
    int getTotalTPCost(int level);

    /**
     * @param level Level to check for (if <code>level > #getMaxLevel()</code>, level is treated as max level)
     * @return Total Mind cost it takes to get from LVL 0 to given level
     */
    int getTotalMindCost(int level);

    /**
     * @param player Player to check
     * @return If the player has learned this skill
     */
    boolean doesPlayerHaveSkill(IPlayer player);

    /**
     * @param player Player to check
     * @param level Minimum level (if <code>level > #getMaxLevel()</code>, level is treated as max level)
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
     * @param player player to teach
     * @param postEvent whether the event should be posted to scripts (only posted if the player doesn't have the skill already)
     */
    void teachPlayerSkill(IPlayer player, boolean postEvent);

    /**
     * Teaches the player this skill if they don't already have it unlocked. <br>
     * Does not take TP, only mind. <br>
     * The event is not posted. <br>
     * Equivalent of calling {@linkplain ICustomSkill#teachPlayerSkill(IPlayer, int, boolean) teachPlayerSkill(player, 1, false)}
     *
     * @param player player to teach
     */
    void teachPlayerSkill(IPlayer player);

    /**
     * If the player doesn't have this skill or their current level is below the one provided,
     * their level is set to the new level. <br>
     * Does not take TP, only mind. <br>
     * The event is posted only if {@code postEvent == true} **and** the skill is newly learned.
     *
     * @param player player to teach
     * @param level level to set (if <code>level > #getMaxLevel()</code>, level is treated as max level)
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
     * @param level level to set (if <code>level > #getMaxLevel()</code>, level is treated as max level)
     */
    void teachPlayerSkill(IPlayer player, int level);


    /**
     * Specifies upgrade TP costs for each level.<br><br>
     * If array is shorter than amount of levels, the last cost is repeated to fill in the blank.<br>
     * If array is too long, it is truncated to the amount of levels.
     * @param array Array of costs; If empty or null, costs are set to 0.
     */
    void setTPCostsArray(int[] array);
    /**
     * Specifies upgrade Mind costs for each level.<br><br>
     * If array is shorter than amount of levels, the last cost is repeated to fill in the blank.<br>
     * If array is too long, it is truncated to the amount of levels.
     * @param array Array of costs; If empty or null, costs are set to 0.
     */
    void setMindCostsArray(int[] array);

    void save();
}
