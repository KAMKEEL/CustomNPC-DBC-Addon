package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.data.race.builder.RaceBuilder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.AndroidRaceRenderer;

public class DBCAddonRaces {
    public static final RaceRegistry RACES = RaceRegistry.create("npcdbc", "DBC Addon");
    
    // ID 6: first custom race slot after vanilla DBC races (0-5)
    public static final Race ANDROID = RACES.register(RaceBuilder.create(6, "android", "Android", "npcdbc:android")
        .skill()
            .maxLevel(5)
            .tpCosts(100, 200, 300, 400, 500)
            .mindCosts(10, 20, 30, 40, 50)
            .and()
        .display()
            .renderer("npcdbc:android")
            .skinLimits(1, 1, 5, 5, 6, 2)
            .genderCount(2)
            .hairType("H")
            .bodyColorPresetCount(7)
            .defaultEyeColors(1, 4896782, 14617612)
            .defaultBodyColors(new int[][]{
                {16297621, 6498048},
                {10112303, 6498048},
                {7225375, 6498048},
                {3677711, 6498048},
                {16297621, 6498048},
                {10112303, 6498048},
                {7225375, 6498048}
            })
            .allowedPowerTypes("012")
            .customSkinMode(2)
            .fixedHairColor(-1)
            .raceAllow("DBC")
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
