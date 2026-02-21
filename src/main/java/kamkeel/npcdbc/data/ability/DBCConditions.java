package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.data.ability.conditions.ConditionDBCClass;
import kamkeel.npcdbc.data.ability.conditions.ConditionDBCLevel;
import kamkeel.npcdbc.data.ability.conditions.ConditionDBCStat;
import kamkeel.npcdbc.data.ability.conditions.ConditionKiThreshold;
import kamkeel.npcdbc.data.ability.conditions.ConditionForm;
import kamkeel.npcdbc.data.ability.conditions.ConditionRace;
import kamkeel.npcs.controllers.AbilityController;
import kamkeel.npcs.util.Register;

public class DBCConditions {
    public static void register() {
        if (AbilityController.Instance == null) return;

        Register.Conditions conditions = Register.Conditions.create("npcdbc", "DBC Addon");
        conditions.register("ki_threshold", ConditionKiThreshold::new);
        conditions.register("form", ConditionForm::new);
        conditions.register("race", ConditionRace::new);
        conditions.register("stat", ConditionDBCStat::new);
        conditions.register("level", ConditionDBCLevel::new);
        conditions.register("class", ConditionDBCClass::new);
        conditions.register();
    }
}
