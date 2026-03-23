package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.data.race.display.RaceDisplay;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.progression.RaceSkill;
import kamkeel.npcdbc.data.race.stats.RaceStats;

public class Race {
    public final int id;
    private final String name;
    private final String menuName;

    public final RaceRegistry registry;
    public final RaceDisplay display;
    public final RaceStats stats;
    public final RaceSkill skill;
    public final FormTree formTree;

    public Race(int id, String name, String menuName, RaceRegistry registry, RaceDisplay display, RaceStats stats, RaceSkill skill, FormTree formTree) {
        this.id = id;
        this.name = name;
        this.menuName = menuName;
        this.registry = registry;
        this.display = display;
        this.stats = stats;
        this.skill = skill;
        this.formTree = formTree;
    }

    public String getName() { return name; }

    public String getMenuName() { return menuName; }
}
