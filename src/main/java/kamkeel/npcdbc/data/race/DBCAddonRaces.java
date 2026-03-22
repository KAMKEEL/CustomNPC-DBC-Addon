package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.data.skill.RacialSkill;

public class DBCAddonRaces {
    public static final RaceRegistry RACES = RaceRegistry.create("npcdbc", "DBC Addon");

    public static final Race ANDROID = RACES.register("android", () ->
        new Race(1, "Android")
            .setRacialSkill(new RacialSkill(5, new int[]{100, 200, 300, 400, 500}, new int[]{10, 10, 10, 15, 20}))
    );

    public static void register() {
        RACES.register();
    }
}
