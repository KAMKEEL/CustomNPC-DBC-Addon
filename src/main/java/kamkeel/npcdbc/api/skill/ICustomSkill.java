package kamkeel.npcdbc.api.skill;

public interface ICustomSkill extends ISkill {

    /**
     * @return Name of the skill as it appears in the menu.
     */
    String getDisplayName();

    /**
     * Set a new display name for the skill.
     *
     * @param name Name to set
     */
    void setDisplayName(String name);

    /**
     * @return Skill's description that shows in the menu
     */
    String getDescription();

    /**
     * Set a new description for a skill
     *
     * @param description new description
     */
    void setDescription(String description);

    /**
     * Set a new max level
     *
     * @param level Level in range <code>1 <= level <= 10</code>
     */
    void setMaxLevel(int level);

    /**
     * Specifies upgrade TP costs for each level.<br><br>
     * If array is shorter than amount of levels, the last cost is repeated to fill in the blank.<br>
     * If array is too long, it is truncated to the amount of levels.
     *
     * @param array Array of costs; If empty or null, costs are set to 0.
     */
    void setTPCostsArray(int[] array);

    /**
     * Specifies upgrade Mind costs for each level.<br><br>
     * If array is shorter than amount of levels, the last cost is repeated to fill in the blank.<br>
     * If array is too long, it is truncated to the amount of levels.
     *
     * @param array Array of costs; If empty or null, costs are set to 0.
     */
    void setMindCostsArray(int[] array);

    void save();
}
