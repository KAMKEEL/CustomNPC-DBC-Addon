package kamkeel.npcdbc.data.race.registry;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.builder.FormBuilder;
import kamkeel.npcdbc.data.race.builder.FormTreeBuilder;
import kamkeel.npcdbc.data.race.builder.RaceBuilder;
import kamkeel.npcdbc.data.race.display.ColorSlot;
import kamkeel.npcdbc.data.race.progression.FormTree;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.BioAndroidRaceRenderer;

public class DBCAddonRaces {
    public static final RaceRegistry RACES = RaceRegistry.create("npcdbc", "DBC Addon");

    private static final String BIO_ANDROID_NS = "npcdbc:bio_android";

    public static final Form SEMI_PERFECT = FormBuilder.create(1)
        .menuName("\u00a7eSemi-Perfect")
        .strengthMulti(2.0f).dexMulti(1.8f).willMulti(1.5f)
        .build();

    public static final Form PERFECT = FormBuilder.create(2)
        .menuName("\u00a76Perfect")
        .strengthMulti(4.0f).dexMulti(3.5f).willMulti(3.0f)
        .build();

    public static final Form MAX = FormBuilder.create(3)
        .menuName("\u00a7cMax")
        .strengthMulti(8.0f).dexMulti(7.0f).willMulti(6.0f)
        .build();

    public static final FormTree BIO_ANDROID_FORMS = FormTreeBuilder.create(BIO_ANDROID_NS)
        .branch(SEMI_PERFECT).child(PERFECT).child(MAX)
        .build();

    public static final Race BIO_ANDROID = RACES.register(RaceBuilder.create(6, "bio_android", "Bio-Android", BIO_ANDROID_NS)
        .formTree(BIO_ANDROID_FORMS)
        .skill()
            .maxLevel(3)
            .displayName("Evolution")
            .level(1, SEMI_PERFECT, 100, 10)
            .level(2, PERFECT, 200, 20)
            .level(3, MAX, 300, 30)
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
    );

    public static void register() {
        RACES.register();
    }

    @SideOnly(Side.CLIENT)
    public static void registerClient(){
        RACES.registerRenderer("npcdbc:bio_android", new BioAndroidRaceRenderer());
    }
}
