package kamkeel.npcdbc.data;

import kamkeel.npcdbc.api.IAura;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.AnimationController;

public class Aura implements IAura {
    public String name, menuName;
    public int id;
    public String type, texture1, texture2, texture3;
    public int color1, color2, color3, alpha = 255, lightningColor, lightningAlpha = 255, speed;
    public boolean hasLightning, hasSpeed, hasAlpha, hasColor1, hasColor2, hasColor3, hasTexture1, hasTexture2, hasTexture3;
    public float size = 1.0f;

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setType(String auraType) {

    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setString("menuName", menuName);

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("ID"))
            id = compound.getInteger("ID");
        else if (AnimationController.Instance != null)
            id = FormController.Instance.getUnusedId();

        name = compound.getString("name");
        menuName = compound.getString("menuName");

    }

    @Override
    public void setColor(String colorType, int color) {

    }

    @Override
    public int getColor(String colorType) {
        return 0;
    }

    @Override
    public void setHasColor(String colorType, boolean hasType) {

    }

    @Override
    public boolean getHasColor(String colortype) {
        return false;
    }

    @Override
    public void setHasLightning(boolean hasLightning) {

    }

    @Override
    public boolean hasLightning() {
        return false;
    }

    @Override
    public boolean hasSize() {
        return size != 1.0f;
    }

    @Override
    public int getLightningColor() {
        return 0;
    }

    @Override
    public void setLightningColor(int color) {

    }

    @Override
    public int getLightningAlpha() {
        return 0;
    }

    @Override
    public void setLightningAlpha(int alpha) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getMenuName() {
        return menuName;
    }

    @Override
    public void setMenuName(String name) {
        if (name.contains("&"))
            name = name.replace("&", "§");

        this.menuName = name;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int newID) {
        id = newID;
    }

    @Override
    public int getSpeed() {
        return 0;
    }

    @Override
    public void setSpeed(int speed) {

    }

    @Override
    public String getTexture(String textureType) {
        return null;
    }

    @Override
    public void setTexture(String textureType, String textureLocation) {

    }

    @Override
    public float getAlpha() {
        return 0;
    }

    @Override
    public void setAlpha(float alpha) {

    }

    @Override
    public IAura save() {
        return AuraController.Instance.saveAura(this);
    }

    @Override
    public float getSize() {
        return size;
    }
}
