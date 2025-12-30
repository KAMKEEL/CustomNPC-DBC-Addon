package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.CustomEffect;

import java.util.function.Consumer;

public class Ability {
    public int id = -1;
    public String name = "";
    public String menuName = "NEW ABILITY";
    public String description = "";
    public int cooldown = -1;
    public int kiCost = -1;

    public String icon;
    public int iconX;
    public int iconY;
    public int width;
    public int height;
    public int scale;

    public AbilityData abilityData;

    public Ability() {
        abilityData = new AbilityData(this);
    }

    public Ability(int id) {
        this();
        this.id = id;
    }

    public Ability(int id, String name) {
        this();;
        this.id = id;
        this.name = name;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getKiCost() {
        return kiCost;
    }

    public void setKiCost(int kiCost) {
        this.kiCost = kiCost;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIconX() {
        return iconX;
    }

    public void setIconX(int iconX) {
        this.iconX = iconX;
    }

    public int getIconY() {
        return iconY;
    }

    public void setIconY(int iconY) {
        this.iconY = iconY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public Ability save() {
        return AbilityController.Instance.saveAbility(this);
    }

    public void onActivate(Consumer<DBCPlayerEvent.AbilityEvent.Activate> function) {
        abilityData.onActivate(function);
    }

    public void onToggle(Consumer< DBCPlayerEvent.AbilityEvent.Toggle> function) {
        abilityData.onToggle(function);
    }

    public void onActivate(EntityPlayer player) {
        abilityData.onActivate(player);
    }

    public void onToggle(EntityPlayer player) {
        abilityData.onToggle(player);
    }

    public Ability cloneAbility() {
        Ability newAbility = new Ability();
        newAbility.readFromNBT(this.writeToNBT(true));
        newAbility.id = -1;
        return newAbility;
    }

    public NBTTagCompound writeToNBT(boolean saveScripts) {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setInteger("ID", id);

        compound.setString("name", name);
        compound.setString("menuName", menuName);
        compound.setString("description", description);

        compound.setInteger("cooldown", cooldown);
        compound.setInteger("kiCost", kiCost);

        compound.setTag("abilityData", abilityData.writeToNBT(saveScripts));

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        id = compound.getInteger("ID");

        name = compound.getString("name");
        menuName = compound.getString("menuName");
        description = compound.getString("description");

        cooldown = compound.getInteger("cooldown");
        kiCost = compound.getInteger("kiCost");

        abilityData.readFromNBT(compound.getCompoundTag("abilityData"));
    }

    public enum Type {
        Active,
        Toggle
    }
}
