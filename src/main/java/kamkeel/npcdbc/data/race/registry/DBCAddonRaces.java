package kamkeel.npcdbc.data.race.registry;

import kamkeel.npcdbc.data.ability.DBCAbilities;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.builder.RaceBuilder;
import kamkeel.npcdbc.data.race.display.ColorSlot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.BioAndroidRaceRenderer;

public class DBCAddonRaces {
    public static final RaceRegistry RACES = RaceRegistry.create("npcdbc", "DBC Addon");

    // ID 6: first custom race slot after vanilla DBC races (0-5)
    public static final Race BIO_ANDROID = RACES.register(RaceBuilder.create(6, "bio_android", "Bio-Android", "npcdbc:bio_android")
        .skill()
            .maxLevel(8)
            .level(1,0,100,10)
            .level(2,0,200,20)
            .level(3,0,300,30)
            .level(4,0,400,40)
            .level(5,0,500,50)
            .level(6,0,600,60)
            .level(7,0,700,70)
            .level(8,0,800,80)
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
