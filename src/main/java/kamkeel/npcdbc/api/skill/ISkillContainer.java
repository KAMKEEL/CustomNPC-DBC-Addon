package kamkeel.npcdbc.api.skill;

import noppes.npcs.api.entity.IPlayer;

/**
 * A skill container contains data about how much a {@link IPlayer} has upgraded their {@link ICustomSkill}.
 */
public interface ISkillContainer {
    /**
     * @return The player this skill container belongs to.
     */
    IPlayer getPlayer();

    /**
     * @return ID of the skill this container is holding.
     */
    int getSkillID();

    /**
     * @return Skill object this container is holding.
     */
    ICustomSkill getSkill();

    /**
     * Upgrade a skill to a certain level for no TP. Doesn't post events. <br>
     * If level is set to 0, {@linkplain ISkillContainer#unlearnSkill unlearnSkill(true)} is called instead.
     * @param level Level to set.
     */
    void setLevel(int level);

    /**
     * @return Get current level of this skill container
     */
    int getLevel();

    /**
     * Equivalent to {@linkplain ISkillContainer#tryToProgressLevel(boolean) tryToProgressLevel(false)}.
     * @return If upgrade attempt finished successfully.
     */
    boolean tryToProgressLevel();

    /**
     * Attempts to progress a level if TP & Mind are sufficient. <br>
     * It <bold>does</bold> take the TP.
     * @param postEvent Post the event to scripts to catch.
     * @return
     */
    boolean tryToProgressLevel(boolean postEvent);

    /**
     * Equivalent to {@linkplain ISkillContainer#unlearnSkill(boolean) unlearnSkill(false)}.
     */
    void unlearnSkill();

    /**
     * Unlearns a skill.
     * @param postEvent Post the event for scripts to catch.
     */
    void unlearnSkill(boolean postEvent);
}
