package kamkeel.npcdbc.data.race.races;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormKey;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.builder.FormBuilder;
import kamkeel.npcdbc.data.race.builder.FormTreeBuilder;
import kamkeel.npcdbc.data.race.builder.RaceBuilder;
import kamkeel.npcdbc.data.race.display.ColorSlot;
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
               .color(ColorSlot.BODY_CM, 0x00ffff)
               .and()                                
        .build(FORMS);

    public static final Form PERFECT = FormBuilder.create(FormKey.of(BIO_ANDROID_NS, "perfect"))
        .menuName("Perfect")
        .strengthMulti(4.0f).dexMulti(3.5f).willMulti(3.0f)
        .display()
               .color(ColorSlot.BODY_CM, 0xff00ff)
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
            .bodyColorSlots(4)
            .defaultColor(ColorSlot.LEFT_EYE, 0xff00ff)
            .defaultColor(ColorSlot.RIGHT_EYE, 0xff00ff)
            .defaultColor(ColorSlot.BODY_CM, 0x568D32)
            .defaultColor(ColorSlot.BODY_C1, 0xB7C913)
            .defaultColor(ColorSlot.BODY_C2, 0xD59406)
            .defaultColor(ColorSlot.BODY_C3, 0x909CC4)
            .and()
        .build(RACES);
}
