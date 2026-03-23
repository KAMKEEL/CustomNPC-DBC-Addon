package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.data.race.builder.RaceBuilder;
import kamkeel.npcdbc.data.race.display.ColorSlot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.AndroidRaceRenderer;

public class DBCAddonRaces {
    public static final RaceRegistry RACES = RaceRegistry.create("npcdbc", "DBC Addon");
    
    // ID 6: first custom race slot after vanilla DBC races (0-5)
    public static final Race ANDROID = RACES.register(RaceBuilder.create(6, "bio_android", "Bio-Android", "npcdbc:bio_android")
        .skill()
            .maxLevel(5)
            .tpCosts(100, 200, 300, 400, 500)
            .mindCosts(10, 20, 30, 40, 50)
            .and()
        .display()
            .renderer("npcdbc:android")
            .hairType("X")
            .bodyColorSlots(3)                                                   
            .defaultColor(ColorSlot.EYES, 0xff00ff)
            .defaultColor(ColorSlot.LEFT_EYE, 0x0)
            .defaultColor(ColorSlot.RIGHT_EYE, 0x0)
            .defaultColor(ColorSlot.BODY_CM, 0x2FED38)
            .defaultColor(ColorSlot.BODY_C1, 0xFFDE50)
            .defaultColor(ColorSlot.BODY_C2, 0xFF9850)
            .and()
    );

    public static void register() {
        RACES.register();
    }
    
    @SideOnly(Side.CLIENT)
    public static void registerClient(){
        RACES.registerRenderer("npcdbc:android", new AndroidRaceRenderer());
    }
}
