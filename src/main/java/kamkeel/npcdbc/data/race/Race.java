package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.display.RaceDisplay;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.progression.RaceDataHolder;
import kamkeel.npcdbc.data.race.progression.RaceSkill;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;
import kamkeel.npcdbc.data.race.stats.RaceAttributeConfig;
import kamkeel.npcdbc.data.race.stats.RaceStats;
import net.minecraft.nbt.NBTTagCompound;

import java.util.function.Function;
import java.util.function.Supplier;

public class Race implements DataSerializable {
    public int id = -1;
    private String name;
    private String menuName;

    public RaceDisplay display = new RaceDisplay();
    public RaceStats stats = new RaceStats();
    public RaceSkill skill = new RaceSkill(1);
    public FormTree formTree = new FormTree();
    public RaceAttributeConfig attributeConfig = RaceAttributeConfig.defaults();
    public Supplier<RaceDataHolder> dataHolder = null;

    public Race(int id, String name, String menuName, RaceDisplay display, RaceStats stats, RaceSkill skill,
                FormTree formTree, RaceAttributeConfig attributeConfig, Supplier<RaceDataHolder> dataHolder) {
        this.id = id;
        this.name = name;
        this.menuName = menuName;
        this.display = display;
        this.stats = stats;
        this.skill = skill;
        this.formTree = formTree;
        this.attributeConfig = attributeConfig;
        this.dataHolder = dataHolder;
    }

    public Race() {}

    public String getName() { return name; }
    public String getMenuName() { return menuName; }

    /**
     * Serialize this race to NBT for network transport.
     * Includes identity fields (id, name) which are NOT written
     * by the config-oriented {@link #serialize(DataCompound)}.
     */
    public NBTTagCompound writeToNBT() {
        DataCompound data = DataCompound.create();
        data.putInt("id", id);
        data.putString("name", name);
        serialize(data);
        return data.toNbt();
    }

    /**
     * Deserialize this race from NBT received over the network.
     * Reads identity fields (id, name) and delegates to
     * {@link #deserialize(DataCompound)} for config fields.
     */
    public void readFromNBT(NBTTagCompound nbt) {
        DataCompound data = DataCompound.ofNbt(nbt);
        id = data.getInt("id", id);
        name = data.getString("name", name);
        deserialize(data);
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        data.comment("Race config — id and name are read-only identity keys.");
        data.putString("MenuName", menuName);

        data.spacing();
        data.comment("=== Form Tree ===");
        data.put("FormTree", formTree);

        data.spacing();
        data.comment("=== Racial Skill ===");
        data.put("RacialSkill", skill);

        data.spacing();
        data.comment("=== Stats ===");
        data.put("Stats", stats);

        data.spacing();
        data.comment("=== AttributeConfig ===");
        data.put("AttributeConfig", attributeConfig);

        data.spacing();
        data.comment("=== Display ===");
        data.put("Display", display);


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
        data.deserialize("FormTree", formTree);
        data.deserialize("RacialSkill", skill);
        data.deserialize("Stats", stats);
        data.deserialize("AttributeConfig", attributeConfig);
        data.deserialize("Display", display);

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

        dataHolder = RaceController.Instance.getDataHolder(this.name);
    }
}
