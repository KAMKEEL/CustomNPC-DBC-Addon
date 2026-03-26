package kamkeel.npcdbc.data.race.races;

import kamkeel.npcdbc.api.Color;
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
        .textureVariant("imperfect/face/eye_base.png")
        .colorOverride(0xFFFFFF)
        .and()
        .layer(RaceDisplay.LAYER_LEFT_EYE)
        .textureVariant("imperfect/face/eye_left.png")
        .and()
        .layer(RaceDisplay.LAYER_RIGHT_EYE)
        .textureVariant("imperfect/face/eye_right.png")
        .and()
        .layer(RaceDisplay.LAYER_NOSE)
        .textureVariant("imperfect/face/nose.png")
        .colorFunction(ctx -> new Color(ctx.bodyC2))
        .and()
        .layer(RaceDisplay.LAYER_MOUTH)
        .textureVariant("imperfect/face/mouth.png")
        .colorFunction(ctx -> new Color(ctx.bodyC2))
        .and()
        .build();

    // ════════════════════════════════════════════════════════════════
    // Race
    // ════════════════════════════════════════════════════════════════

    public static final Race RACE = RaceBuilder.create(6, "bio_android", "Bio-Android", BIO_ANDROID_NS)
        .formTree(BIO_ANDROID_FORMS)
        .racialSkill()
        .maxLevel(4)
        .displayName("Evolution")
        .level(1, SEMI_PERFECT, 100, 10)
        .level(2, PERFECT, 200, 20)
        .level(3, MAX, 300, 30)
        .level(4, GOD, 300, 30)
        .and()
        .display()
        .renderer("npcdbc:bio_android")
        .hairType("X")
        .addComponent(BASE_FACE)
        .and()
        .build();
}
