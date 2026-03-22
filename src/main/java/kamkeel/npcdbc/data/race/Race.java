package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.data.race.display.RaceDisplay;
import kamkeel.npcdbc.data.skill.RacialSkill;

public class Race {
    public int id;
    private String name;
    private String menuName;
    private double baseMultiplier = 1.0;

    public RaceDisplay display = new RaceDisplay();
    public RacialSkill racialSkill = new RacialSkill();

    public Race(int id, String name) {
        this.id = id;
        this.name = name;
        this.menuName = name;
    }

    public Race(int id, String name, String menuName) {
        this.id = id;
        this.name = name;
        this.menuName = menuName;
    }

    // -------------------------
    // Getters
    // -------------------------

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMenuName() {
        return menuName;
    }

    public double getBaseMultiplier() {
        return baseMultiplier;
    }

    public RaceDisplay getDisplay() {
        return display;
    }

    public RacialSkill getRacialSkill() {
        return racialSkill;
    }

    // -------------------------
    // Setters (fluent)
    // -------------------------

    public Race setBaseMultiplier(double baseMultiplier) {
        this.baseMultiplier = baseMultiplier;
        return this;
    }

    public Race setDisplay(RaceDisplay display) {
        this.display = display;
        return this;
    }

    public Race setRacialSkill(RacialSkill racialSkill) {
        this.racialSkill = racialSkill;
        return this;
    }

    public Race setMenuName(String menuName) {
        this.menuName = menuName;
        return this;
    }

    // -------------------------
    // NBT
    // -------------------------

    public void readFromNBT(net.minecraft.nbt.NBTTagCompound tag) {
        id = tag.getInteger("id");
        name = tag.getString("name");
        menuName = tag.getString("menuName");
        baseMultiplier = tag.getDouble("baseMultiplier");

        if (tag.hasKey("display"))
            display.readFromNBT(tag.getCompoundTag("display"));

        if (tag.hasKey("racialSkill"))
            racialSkill.readFromNBT(tag.getCompoundTag("racialSkill"));
    }

    public net.minecraft.nbt.NBTTagCompound writeToNBT() {
        net.minecraft.nbt.NBTTagCompound tag = new net.minecraft.nbt.NBTTagCompound();
        tag.setInteger("id", id);
        tag.setString("name", name);
        tag.setString("menuName", menuName);
        tag.setDouble("baseMultiplier", baseMultiplier);
        tag.setTag("display", display.writeToNBT());
        tag.setTag("racialSkill", racialSkill.writeToNBT());
        return tag;
    }
}
