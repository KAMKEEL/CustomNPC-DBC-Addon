package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.data.ability.conditions.ConditionKiThreshold;
import kamkeel.npcdbc.data.ability.conditions.ConditionForm;
import kamkeel.npcs.controllers.AbilityController;

public class DBCConditions {
    public static void register() {
        if (AbilityController.Instance == null) return;

        AbilityController ctrl = AbilityController.Instance;

        ctrl.registerCondition(ConditionKiThreshold::new);
        ctrl.registerCondition(ConditionForm::new);
    }
}
