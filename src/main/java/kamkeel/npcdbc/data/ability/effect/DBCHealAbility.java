package kamkeel.npcdbc.data.ability.effect;

import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.constants.enums.EnumDBCRaces;
import kamkeel.npcdbc.data.ability.conditions.ConditionRace;
import kamkeel.npcs.controllers.AbilityController;
import kamkeel.npcs.controllers.data.ability.conditions.ConditionFilter;
import kamkeel.npcs.controllers.data.ability.conditions.ConditionHPThreshold;
import kamkeel.npcs.controllers.data.ability.conditions.ConditionThreshold;
import kamkeel.npcs.controllers.data.ability.data.effect.AbilityCustomEffect;
import kamkeel.npcs.controllers.data.ability.enums.TargetingMode;

import java.util.ArrayList;
import java.util.List;

public enum DBCHealAbility {
    NAMEKIAN(new DBCEffectAbility("namek_regen", a -> {
        a.setDisplayName("&2Namekian Regeneration");
        a.setPerAbilityCooldown(true);
        a.setWindUpTicks(44);
        a.setWindUpAnimationName("NamekRegen");
        a.setTargetingMode(TargetingMode.SELF);
        a.setIncludeSelf(true);

        ConditionRace conditionRace = new ConditionRace();
        conditionRace.setRace(EnumDBCRaces.NAMEKIAN);
        conditionRace.setFilter(ConditionFilter.CASTER);

        ConditionHPThreshold conditionHP = new ConditionHPThreshold();
        conditionHP.setThresholdPercent(ConfigDBCGameplay.NamekianRegenMin);
        conditionHP.setThresholdType(ConditionThreshold.ThresholdType.BELOW);

        a.addCondition(conditionRace);
        a.addCondition(conditionHP);

        List<AbilityCustomEffect> list = new ArrayList<>();
        list.add(new AbilityCustomEffect(Effects.NAMEK_REGEN, 60, (byte) 1, 1));
        a.setCustomEffects(list);
    })),
    BIO_ANDROID(new DBCEffectAbility("bio_android_regen", a -> {
        a.setDisplayName("&2Namekian Regeneration");
        a.setPerAbilityCooldown(true);
        a.setWindUpTicks(44);
        a.setWindUpAnimationName("NamekRegen");
        a.setTargetingMode(TargetingMode.SELF);
        a.setIncludeSelf(true);
        a.setHealPercent(20);

//        ConditionRace conditionRace = new ConditionRace();
//        conditionRace.setRace(EnumDBCRaces.NAMEKIAN);
//        conditionRace.setFilter(ConditionFilter.CASTER);

        ConditionHPThreshold conditionHP = new ConditionHPThreshold();
        conditionHP.setThresholdPercent(ConfigDBCGameplay.NamekianRegenMin);
        conditionHP.setThresholdType(ConditionThreshold.ThresholdType.BELOW);

//        a.addCondition(conditionRace);
        a.addCondition(conditionHP);
    }));

    private final DBCEffectAbility ability;

    DBCHealAbility(DBCEffectAbility ability) {
        this.ability = ability;
    }

    public DBCEffectAbility get() {
        if (AbilityController.Instance.getAbility(ability.getName()) != null)
            return (DBCEffectAbility) AbilityController.Instance.getAbility(ability.getName());

        return ability;
    }
}
