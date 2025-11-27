package kamkeel.npcdbc.api.skill;

public interface ISkillHandler {

    ICustomSkill getSkill(int id);
    ICustomSkill getSkill(String stringLiteralId);

    void delete(int id);
    void delete(ICustomSkill skill);

    /**
     * Calls {@linkplain ISkillHandler#createSkill(String, int) createSkill(stringLiteralId, 1)}
     */
    ICustomSkill createSkill(String stringLiteralId);

    /**
     * Calls {@linkplain ISkillHandler#createSkill(String, int) createSkill(stringLiteralId, maxLevel, null, null)}
     */
    ICustomSkill createSkill(String stringLiteralId, int maxLevel);

    /**
     * Creates a new skill or returns an existing one with the given ID. <br>
     * If skill existed, no changes happen to it.
     * @param stringLiteralId String ID which can be used to look up the skill with.
     * @param maxLevel Max level of a skill. Minimum 1, Max 10.
     * @param tpCosts Array of TP costs. Refer to {@linkplain ICustomSkill#setTPCostsArray(int[])}.
     * @param mindCosts Array of Mind costs. Refer to {@linkplain ICustomSkill#setMindCostsArray(int[])}.
     * @return newly created skill.
     */
    ICustomSkill createSkill(String stringLiteralId, int maxLevel, int[] tpCosts, int[] mindCosts);

    ICustomSkill saveSkill(ICustomSkill iskill);
}
