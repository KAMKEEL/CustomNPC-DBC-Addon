package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.display.RaceDisplay;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.progression.RaceSkill;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;
import kamkeel.npcdbc.data.race.stats.RaceAttributeConfig;
import kamkeel.npcdbc.data.race.stats.RaceStats;

public class Race implements DataSerializable {
    public int id;
    private String name;
    private String menuName;

    public RaceDisplay display;
    public RaceStats stats;
    public RaceSkill skill;
    public FormTree formTree;
    public RaceAttributeConfig attributeConfig;

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

    @Override
    public DataCompound serialize(DataCompound data) {
        data.comment("Race config — id and name are read-only identity keys.");
        data.putString("MenuName", menuName);

        data.spacing();
        data.comment("=== Display ===");
        data.put("Display", display);

        data.spacing();
        data.comment("=== Stats ===");
        data.put("Stats", stats);

        data.spacing();
        data.comment("=== RacialSkill ===");
        data.put("RacialSkill", skill);

        data.spacing();
        data.comment("=== AttributeConfig ===");
        data.put("AttributeConfig", attributeConfig);

        if (formTree != null) {
            data.spacing();
            data.comment("=== Forms ===");
            DataCompound formsData = data.child();
            formsData.comment("Builtin form overrides. id/key/parentID/childID/builtIn are runtime-only.");
            for (Form form : formTree.getAllForms()) {
                if (form.key == null)
                    continue;
                DataCompound formData = formsData.child();
                formData.putString("menuName", form.menuName);
                formData.putInt("timer", form.timer);
                formData.putFloat("strengthMulti", form.strengthMulti);
                formData.putFloat("dexMulti", form.dexMulti);
                formData.putFloat("willMulti", form.willMulti);
                formsData.put(form.key.toString(), formData);
            }
            data.put("Forms", formsData);
        }

        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        menuName = data.getString("MenuName", menuName);
        data.deserialize("Display", display);
        data.deserialize("Stats", stats);
        data.deserialize("RacialSkill", skill);
        data.deserialize("AttributeConfig", attributeConfig);

        DataCompound formsData = data.get("Forms");
        for (String key : formsData.getKeys()) {
            DataCompound formData = formsData.get(key);
            Form form = FormController.Instance.getBuiltIn(key);
            if (form == null) continue;
            form.menuName      = formData.getString("menuName",      form.menuName);
            form.timer         = formData.getInt("timer",            form.timer);
            form.strengthMulti = formData.getFloat("strengthMulti",  form.strengthMulti);
            form.dexMulti      = formData.getFloat("dexMulti",       form.dexMulti);
            form.willMulti     = formData.getFloat("willMulti",      form.willMulti);
        }
    }
}
