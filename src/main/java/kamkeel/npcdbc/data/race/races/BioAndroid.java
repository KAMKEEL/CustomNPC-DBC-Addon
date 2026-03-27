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

import static kamkeel.npcdbc.AddonRegistries.FORMS;
import static kamkeel.npcdbc.AddonRegistries.RACES;

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
        .color(RaceDisplay.LAYER_BODY_CM, 0x00ffff)
        .and()
        .build(FORMS);

    public static final Form PERFECT = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "perfect"))
        .menuName("Perfect")
        .strengthMulti(4.0f).dexMulti(3.5f).willMulti(3.0f)
        .display()
//        .color(ColorSlot.BODY_CM, 0xff00ff)
        .and()
        .build(FORMS);

    public static final Form MAX = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "max"))
        .menuName("Max")
        .strengthMulti(8.0f).dexMulti(7.0f).willMulti(6.0f)
        .build(FORMS);

    public static final Form GOD = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "god"))
        .menuName("God")
        .strengthMulti(8.0f).dexMulti(7.0f).willMulti(6.0f)
        .build(FORMS);

    // ════════════════════════════════════════════════════════════════
    // Form Tree
    // ════════════════════════════════════════════════════════════════

    public static final FormTree BIO_ANDROID_FORMS = FormTreeBuilder.create(BIO_ANDROID_NS)
        .branch(SEMI_PERFECT).child(PERFECT).child(MAX)
        .branch(GOD)
        .build();

    // ════════════════════════════════════════════════════════════════
    // Display
    // ════════════════════════════════════════════════════════════════

    public static final DisplayComponent BASE_FACE = DisplayComponentBuilder.create(RaceDisplay.COMPONENT_FACE)
        .layer(RaceDisplay.LAYER_EYEBASE)
            .texture("imperfect/face/eye_base.png")
            .color(0xFFFFFF, true)
        .and()
        .layer(RaceDisplay.LAYER_LEFT_EYE)
            .texture("imperfect/face/eye_left.png")
        .and()
        .layer(RaceDisplay.LAYER_RIGHT_EYE)
            .texture("imperfect/face/eye_right.png")
        .and()
        .layer(RaceDisplay.LAYER_NOSE)
            .texture("imperfect/face/nose.png")
            .color(ctx -> new Color(ctx.bodyC2))
        .and()
            .layer(RaceDisplay.LAYER_MOUTH)
            .texture("imperfect/face/mouth.png")
            .color(ctx -> new Color(ctx.bodyC2))
        .and()
        .build();

    public static final DisplayComponent BASE_BODY = DisplayComponentBuilder.create(RaceDisplay.COMPONENT_BODY)
        .layer(RaceDisplay.LAYER_BODY_CM)
            .texture("imperfect/bio_imperfect_0.png")
            .color(0x568D32)
            .and()
        .layer(RaceDisplay.LAYER_BODY_C1, "Body Layer 1")
            .texture("imperfect/bio_imperfect_1.png")
            .color(0xB7C913)
            .and()
        .layer(RaceDisplay.LAYER_BODY_C2, "Body Layer 2")
            .texture("imperfect/bio_imperfect_2.png")
            .color(0xFCB054)
            .and()
        .layer(RaceDisplay.LAYER_BODY_C3, "Body Layer 3")
            .texture("imperfect/bio_imperfect_3.png")
            .color(0x909CC4)
            .and()
        .layer("bodyc4", "Body Layer 4")
            .texture("imperfect/bio_imperfect_4.png")
            .color(0xFFFFFF, true)
            .and()
        .build();

    // ════════════════════════════════════════════════════════════════
    // Race
    // ════════════════════════════════════════════════════════════════

    public static final Race RACE = RaceBuilder.create(DBCRace.BIO_ANDROID, "bio_android", "Bio-Android", BIO_ANDROID_NS)
        .formTree(BIO_ANDROID_FORMS)
        .racialSkill()
            .maxLevel(4)
            .displayName("Evolution")
            .level(1, SEMI_PERFECT, 100, 10)
            .level(2, PERFECT, 200, 20)
            .level(3, MAX, 300, 30)
            .level(4, GOD, 300, 30)
            .and()
        .stats()
            .allClasses()
                .startAttr().str(15).dex(10).con(10).will(15).mnd(5).spi(5).and()
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
        .display()
            .renderer("npcdbc:bio_android")
            .hairType("X")
            .addComponent(BASE_FACE)
            .addComponent(BASE_BODY)
            .and()
        .build(RACES);
}
