package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.skill.RacialSkill;

public class DBCAddonRaces {
    public static final Race ANDROID = new Race(1, "Android")
        .setRacialSkill(new RacialSkill(5, new int[]{100, 200, 300, 400, 500}, new int[]{10, 10, 10, 15, 20}));

    public static void register() {
        if (RaceController.Instance == null) return;

        RaceController.getInstance().register(ANDROID);
    }
}
