package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.data.ability.conditions.*;
import kamkeel.npcs.controllers.AbilityController;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import kamkeel.npcs.util.Register;

public class DBCConditions {
    public static final Register.Conditions CONDITIONS = Register.Conditions.create("npcdbc", "DBC Addon");

    public static final AbilityCondition KI_THRESHOLD = CONDITIONS.register("ki_threshold", ConditionKiThreshold::new);
    public static final AbilityCondition STAMINA_THRESHOLD = CONDITIONS.register("stamina_threshold", ConditionStaminaThreshold::new);
    public static final AbilityCondition FORM = CONDITIONS.register("form", ConditionForm::new);
    public static final AbilityCondition RACE = CONDITIONS.register("race", ConditionRace::new);
    public static final AbilityCondition STAT = CONDITIONS.register("stat", ConditionDBCStat::new);
    public static final AbilityCondition LEVEL = CONDITIONS.register("level", ConditionDBCLevel::new);
    public static final AbilityCondition CLASS = CONDITIONS.register("class", ConditionDBCClass::new);
    public static final AbilityCondition SKILL = CONDITIONS.register("skill", ConditionSkill::new);
    public static final AbilityCondition LOCKED_ON = CONDITIONS.register("locked_on", ConditionLockedOn::new);

    public static void register() {
        if (AbilityController.Instance == null) return;

        CONDITIONS.register();
    }
}
