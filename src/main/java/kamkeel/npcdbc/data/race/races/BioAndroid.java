package kamkeel.npcdbc.data.race.races;

import kamkeel.npcdbc.api.Color;

import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumDBCClasses;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormKey;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.builder.DisplayComponentBuilder;
import kamkeel.npcdbc.data.race.builder.FormBuilder;
import kamkeel.npcdbc.data.race.builder.FormTreeBuilder;
import kamkeel.npcdbc.data.race.builder.RaceBuilder;
import kamkeel.npcdbc.data.race.display.DisplayComponent;
import kamkeel.npcdbc.data.race.display.RaceDisplay;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.stats.RaceAttributeConfig;

import static kamkeel.npcdbc.AddonRegistries.FORMS;
import static kamkeel.npcdbc.AddonRegistries.RACES;
import static kamkeel.npcdbc.constants.BodyLayer.*;
public class BioAndroid {

    private static final String BIO_ANDROID_NS = "npcdbc:bio_android";

    public static void init() {
        // Triggers class loading — static fields below self-register into AddonRegistries.RACES.
    }
    // ════════════════════════════════════════════════════════════════
    // Forms
    // ════════════════════════════════════════════════════════════════
    public static final String TYPE_SEMI_PERFECT ="semi_perfect", TYPE_PERFECT ="perfect";
    public static final Form SEMI_PERFECT = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "semi_perfect"))
        .menuName("Semi-Perfect")
        .strengthMulti(2.0f).dexMulti(1.8f).willMulti(1.5f)
        .display()
            .bodyType(TYPE_SEMI_PERFECT)
            .color(BODY_CM, 0x00ffff)
            .and()
        .build(FORMS);

    public static final Form PERFECT = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "perfect"))
        .menuName("Perfect")
        .strengthMulti(4.0f).dexMulti(3.5f).willMulti(3.0f)
        .display()
            .bodyType(TYPE_PERFECT)
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
            .bodyType(TYPE_PERFECT)
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
            .bodyType(TYPE_PERFECT)
            .color(BODY_CM, 0xc22023)
            .color(BODY_C1, 0x9abe35)
            .color(BODY_C2, 0xc22023)
            .color(BODY_C3, 0x701b58)
            .customizable(true)
            .and()
        .build(FORMS);

    public static final Form GOD = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "god"))
        .menuName("God")       
        .strengthMulti(8.0f).dexMulti(7.0f).willMulti(6.0f)
        .display()
            .bodyType(TYPE_PERFECT)
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
    // Display
    // ════════════════════════════════════════════════════════════════

    public static final DisplayComponent BASE_FACE = DisplayComponentBuilder.create(RaceDisplay.COMPONENT_FACE)
        .layer(EYEBASE)
            .texture("imperfect/face/eye_base.png")
            .color(0xFFFFFF, true)
        .and()
        .layer(EYE_LEFT)
            .texture("imperfect/face/eye_left.png")
        .and()
        .layer(EYE_RIGHT)
            .texture("imperfect/face/eye_right.png")
        .and()
        .layer(NOSE)
            .texture("imperfect/face/nose.png")
            .color(ctx -> new Color(ctx.bodyC2()))
        .and()
            .layer(MOUTH)
            .texture("imperfect/face/mouth.png")
            .color(ctx -> new Color(ctx.bodyC2()))
        .and()
        .build();

    public static final DisplayComponent BASE_BODY = DisplayComponentBuilder.create(RaceDisplay.COMPONENT_BODY)
        .layer(BODY_CM)
            .texture("imperfect/bio_imperfect_0.png")
            .color(0x568D32)
            .and()
        .layer(BODY_C1, "Body Layer 1")
            .texture("imperfect/bio_imperfect_1.png")
            .color(0xB7C913)
            .and()
        .layer(BODY_C2, "Body Layer 2")
            .texture("imperfect/bio_imperfect_2.png")
            .color(0xFCB054)
            .and()
        .layer(BODY_C3, "Body Layer 3")
            .texture("imperfect/bio_imperfect_3.png")
            .color(0x909CC4)
            .and()
        .layer(BODY_C4, "Body Layer 4")
            .texture("imperfect/bio_imperfect_4.png")
            .color(0xFFFFFF, true)
            .and()
        .build();

    public static final DisplayComponent SEMI_PERFECT_FACE = DisplayComponentBuilder.create(RaceDisplay.COMPONENT_FACE)
        .layer(EYEBASE)
            .texture("semiperfect/face/eye_base.png")
            .color(0xFFFFFF, true)
            .and()
        .layer(EYE_LEFT)
            .texture("semiperfect/face/eye_left.png")
            .color(0xDFEEEE)
            .and()
        .layer(EYE_RIGHT)
            .texture("semiperfect/face/eye_right.png")
            .color(0xDFEEEE)
            .and()
        .layer(NOSE)
            .texture("semiperfect/face/nose.png")
            .color(0xFFFFFF, true)
            .and()
        .layer(MOUTH)
            .texture("semiperfect/face/mouth.png")
            .color(0xFFE0FA, true)
            .and()
        .build();

    public static final DisplayComponent SEMI_PERFECT_BODY = DisplayComponentBuilder.create(RaceDisplay.COMPONENT_BODY)
        .layer(BODY_CM)
            .texture("semiperfect/bio_semiperfect_0.png")
            .color(0x568D32)
            .and()
        .layer(BODY_C1, "Body Layer 1")
            .texture("semiperfect/bio_semiperfect_1.png")
            .color(0xB7C913)
            .and()
        .layer(BODY_C2, "Body Layer 2")
            .texture("semiperfect/bio_semiperfect_2.png")
            .color(0xFCB054)
            .and()
        .layer(BODY_C3, "Body Layer 3")
            .texture("semiperfect/bio_semiperfect_3.png")
            .color(0x909CC4)
            .and()
        .layer(BODY_C4, "Body Layer 4")
            .texture("semiperfect/bio_semiperfect_4.png")
            .color(0xFFFFFF, true)
            .and()
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
                .multi(1, 1, 1, 1,1, 1)
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
            .renderer("npcdbc:bio_android")
            .hairType("X")
            .addComponent(BASE_FACE)
            .addComponent(BASE_BODY)
            .and()
        .build(RACES);
}
