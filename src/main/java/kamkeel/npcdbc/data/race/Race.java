package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.data.race.display.RaceDisplay;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.progression.RaceSkill;
import kamkeel.npcdbc.data.race.stats.RaceAttributeConfig;
import kamkeel.npcdbc.data.race.stats.RaceStats;

public class Race {
    public final int id;
    private final String name;
    private final String menuName;

    public final RaceDisplay display;
    public final RaceStats stats;
    public final RaceSkill skill;
    public final FormTree formTree;
    public final RaceAttributeConfig attributeConfig;

    public Race(int id, String name, String menuName, RaceDisplay display, RaceStats stats, RaceSkill skill, FormTree formTree, RaceAttributeConfig attributeConfig) {
        this.id = id;
        this.name = name;
        this.menuName = menuName;
        this.display = display;
        this.stats = stats;
        this.skill = skill;
        this.formTree = formTree;
        this.attributeConfig = attributeConfig;
    }

    public String getName() { return name; }

    public String getMenuName() { return menuName; }
}
