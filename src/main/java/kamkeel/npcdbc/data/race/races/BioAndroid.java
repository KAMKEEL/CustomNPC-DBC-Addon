package kamkeel.npcdbc.data.race.races;

import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumDBCClasses;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormKey;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.builder.FormBuilder;
import kamkeel.npcdbc.data.race.builder.FormTreeBuilder;
import kamkeel.npcdbc.data.race.builder.RaceBuilder;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.races.bioandroid.BioAndroidLayers;
import kamkeel.npcdbc.data.race.stats.RaceAttributeConfig;

import static kamkeel.npcdbc.AddonRegistries.FORMS;
import static kamkeel.npcdbc.AddonRegistries.RACES;
import static kamkeel.npcdbc.constants.BodyLayer.*;
import static kamkeel.npcdbc.data.race.races.bioandroid.BioAndroidLayers.*;

public class BioAndroid {

    private static final String BIO_ANDROID_NS = "npcdbc:bio_android";

    public static void init() {
        // Triggers class loading — static fields below self-register into AddonRegistries.RACES.
    }

    // ════════════════════════════════════════════════════════════════
    // Forms
    // ════════════════════════════════════════════════════════════════

    public static final Form SEMI_PERFECT = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "semi_perfect"))
        .menuName("Semi-Perfect")
        .strengthMulti(2.0f).dexMulti(1.8f).willMulti(1.5f)
        .display()
            .bodyType(BioAndroidLayers.SEMI_PERFECT)
            .and()
        .build(FORMS);

    public static final Form PERFECT = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "perfect"))
        .menuName("Perfect")
        .strengthMulti(4.0f).dexMulti(3.5f).willMulti(3.0f)
        .display()
            .bodyType(BioAndroidLayers.PERFECT)
            .color(EYE_LEFT, 0xdb8bdf)
            .color(EYE_RIGHT, 0xdb8bdf)
            .color(BODY_C1, 0xd5dbd9)
            .color(BODY_C2, 0xd7c827)
            .color(BODY_C3, 0x6c2f7c)
            .customizable(true)
            .and()
        .build(FORMS);

    public static final Form SUPER_PERFECT = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "super_perfect"))
        .menuName("Super Perfect")
        .strengthMulti(4.0f).dexMulti(3.5f).willMulti(3.0f)
        .display()
            .bodyType(BioAndroidLayers.PERFECT)
            .color(EYE_LEFT, 0xdb8bdf)
            .color(EYE_RIGHT, 0xdb8bdf)
            .color(BODY_C1, 0xd5dbd9)
            .color(BODY_C2, 0xd7c827)
            .color(BODY_C3, 0x6c2f7c)
            .auraColor(0xFEDA00)
            .customizable(true)
            .and()
        .build(FORMS);

    public static final Form MAX = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "max"))
        .menuName("Max")
        .strengthMulti(8.0f).dexMulti(7.0f).willMulti(6.0f)
        .display()
            .bodyType(BioAndroidLayers.MAX)
            .color(EYE_LEFT, 0xc86637)
            .color(EYE_RIGHT, 0xc86637)
            .color(BODY_CM, 0xc22023)
            .color(BODY_C1, 0x9abe35)
            .color(BODY_C2, 0xc22023)
            .color(BODY_C3, 0x701b58)
            .berserk(true)
            .customizable(true)
            .and()
        .build(FORMS);

    public static final Form GOD = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "god"))
        .menuName("God")
        .strengthMulti(8.0f).dexMulti(7.0f).willMulti(6.0f)
        .display()
            .bodyType(BioAndroidLayers.PERFECT)
            .color(EYE_LEFT, 0xE40426)
            .color(EYE_RIGHT, 0xE40426)
            .color(BODY_CM, 0xb50125)
            .color(BODY_C1, 0xd5dbd9)
            .color(BODY_C2, 0xd7c827)
            .color(BODY_C3, 0x6c2f7c)
            .customizable(true)
            .and()
        .build(FORMS);

    // ════════════════════════════════════════════════════════════════
    // Form Tree
    // ════════════════════════════════════════════════════════════════

    public static final FormTree BIO_ANDROID_FORMS = FormTreeBuilder.create(BIO_ANDROID_NS)
        .branch(SEMI_PERFECT).child(PERFECT).child(SUPER_PERFECT)
        .branch(MAX)
        .branch(GOD)
        .build();

    // ════════════════════════════════════════════════════════════════
    // Race
    // ════════════════════════════════════════════════════════════════

    public static final Race RACE = RaceBuilder.create(DBCRace.BIO_ANDROID, "bio_android", "Bio-Android", BIO_ANDROID_NS)
        .formTree(BIO_ANDROID_FORMS)
        .racialSkill()
            .maxLevel(5)
            .displayName("Evolution")
            .description("Like a soon to be broken man once said, you're either perfect, or you're not me.")
            .level(1, SEMI_PERFECT, 100, 10)
            .level(2, PERFECT, 200, 20)
            .level(3, SUPER_PERFECT, 300, 30)
            .level(4, MAX, 300, 30)
            .level(5, GOD, 400, 30)
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
            .hairType("X")
            .addChain(BASE_GROUP)
            .addChain(SEMI_PERFECT_GROUP)
            .addChain(PERFECT_GROUP)
            .addChain(MAX_GROUP)
            .and()
        .build(RACES);
}
