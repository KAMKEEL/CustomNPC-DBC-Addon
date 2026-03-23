package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.data.race.builder.RaceBuilder;

public class DBCAddonRaces {
    public static final RaceRegistry RACES = RaceRegistry.create("npcdbc", "DBC Addon");

    public static final Race ANDROID = RACES.register(RaceBuilder.create(1, "android", "Android", "npcdbc:android")
            .skill()
                .maxLevel(5)
                .tpCosts(1000, 2000, 3000, 4000, 5000)
                .mindCosts(10, 10, 10, 20, 30)
                .and()
    );

    public static void register() {
        RACES.register();
    }
}
