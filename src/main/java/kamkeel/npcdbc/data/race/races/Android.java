package kamkeel.npcdbc.data.race.races;

import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumDBCClasses;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.builder.RaceBuilder;
import kamkeel.npcdbc.data.race.stats.RaceAttributeConfig;

import static kamkeel.npcdbc.AddonRegistries.RACES;
import static kamkeel.npcdbc.data.race.races.android.AndroidLayers.*;

public class Android {

    private static final String ANDROID_NS = "npcdbc:android";

    public static void init() {
        // Triggers class loading — static fields below self-register into AddonRegistries.RACES.
    }

    // ════════════════════════════════════════════════════════════════
    // Race
    // ════════════════════════════════════════════════════════════════

    public static final Race RACE = RaceBuilder.create(DBCRace.ANDROID, "android", "Android", ANDROID_NS)
        .racialSkill()
            .maxLevel(5)
            .displayName("Enhancements")
            .description("Nano machines, son.")
            .and()
        .stats()
            .allClasses()
                .startAttr().str(15).dex(10).con(10).will(15).mnd(67).spi(5).and()
                .statMulti()
                    .melee(2.5).defense(4.0).body(20.0).stamina(3.5)
                    .energyPower(5.2).energyPool(40.0).maxSkills(0.15)
                    .speed(1.0).regenBody(1.0).regenStamina(1.0).regenEnergy(1.0).flySpeed(1.0)
                .and()
            .forClass(EnumDBCClasses.MARTIAL_ARTIST)
                .statBonus().melee(30).energyPower(20).flySpeed(10).and()
            .forClass(EnumDBCClasses.SPIRITUALIST)
                .statBonus()
                    .melee(20).defense(10).body(-10).stamina(-10)
                    .energyPower(30).energyPool(10).speed(10)
                    .regenBody(-10).regenStamina(-10).regenEnergy(10).flySpeed(20)
                .and()
            .forClass(EnumDBCClasses.WARRIOR)
                .statBonus()
                    .melee(40).defense(-10).body(10).stamina(10)
                    .energyPower(10).energyPool(-10).speed(-10)
                    .regenBody(10).regenStamina(10).regenEnergy(-10)
                .and()
            .and()
        .and()
        .attributeConfig()
            .base()
                .multi(1, 1, 1, 1, 1, 1)
                .flat(0, 0, 0, 0, 0, 0)
                .and()
            .mystic()
                .values()
                    .multi(1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f)
                    .flat(0, 0, 0, 0, 0, 0)
                    .and()
                .formula(RaceAttributeConfig.MysticFormula.ATTRIBUTE_MULTI_PLUS_SKILL)
                .attrBonusPerSkillLevel(0.06f)
                .and()
            .stackables()
                .baseAttrBonusPerSkillLevel(0.06f)
                .godAttrMultiRace(1.0f)
                .uiAttrMultiRace(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f)
                .legendaryAppliesInBase(true)
                .and()
            .and()
        .display()
            .hairType("H")
            .addChain(BASE_GROUP)
            .mouthSlots(3)
            .noseSlots(3)
            .eyeSlots(3)
            .and()
        .build(RACES);
}
