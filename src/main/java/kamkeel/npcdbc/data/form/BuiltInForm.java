package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.data.race.Race;

public class BuiltInForm {
    public final int id;
    public String menuName = "§aNEW";

    public float strengthMulti = 1.0f;
    public float dexMulti = 1.0f;
    public float willMulti = 1.0f;

    public FormRace race = FormRace.ALL;
    public FormDisplay display = new FormDisplay(null);

    public int childID = -1;
    public int parentID = -1;

    public BuiltInForm(int id) {
        this.id = id;
    }

    public boolean hasChild() {
        return childID != -1;
    }

    public boolean hasParent() {
        return parentID != -1;
    }
}
