package kamkeel.npcdbc.data.race;

public class DBCAddonRaces {
    public static final RaceRegistry RACES = RaceRegistry.create("npcdbc", "DBC Addon");

    public static void register() {
        RACES.register();
    }
}
