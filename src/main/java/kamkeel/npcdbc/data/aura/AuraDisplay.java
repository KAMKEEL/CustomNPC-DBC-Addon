package kamkeel.npcdbc.data.aura;

import kamkeel.npcdbc.api.aura.IAuraDisplay;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

public class AuraDisplay implements IAuraDisplay {
    public Aura parent;

    public String type = "", texture1 = "", texture2 = "", texture3 = "";
    public int color1 = -1, color2 = -1, color3 = -1, alpha = -1;
    public float size = 1.0f, speed = 1.0f;


    public boolean hasLightning = false;
    public int lightningColor = -1, lightningAlpha = -1;


    public AuraDisplay(Aura parent) {
        this.parent = parent;
    }


    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound rendering = compound.getCompoundTag("rendering");

        type = rendering.getString("type");
        texture1 = rendering.getString("texture1");
        texture2 = rendering.getString("texture2");
        texture3 = rendering.getString("texture3");
        speed = rendering.getFloat("speed");
        size = rendering.getFloat("size");

        color1 = rendering.getInteger("color1");
        color2 = rendering.getInteger("color2");
        color3 = rendering.getInteger("color3");
        alpha = rendering.getInteger("alpha");

        hasLightning = rendering.getBoolean("hasLightning");
        lightningColor = rendering.getInteger("lightningColor");
        lightningAlpha = rendering.getInteger("lightningAlpha");

    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound rendering = new NBTTagCompound();

        rendering.setString("type", type);
        rendering.setString("texture1", texture1);
        rendering.setString("texture2", texture2);
        rendering.setString("texture3", texture3);
        rendering.setFloat("speed", speed);
        rendering.setFloat("size", size);

        rendering.setInteger("color1", color1);
        rendering.setInteger("color2", color2);
        rendering.setInteger("color3", color3);
        rendering.setInteger("alpha", alpha);

        rendering.setBoolean("hasLightning", hasLightning);
        rendering.setInteger("lightningColor", lightningColor);
        rendering.setInteger("lightningAlpha", lightningAlpha);

        compound.setTag("rendering", rendering);
        return compound;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        String s = type.toLowerCase();
        if (s.equals("ssgod") || s.equals("ssb") || s.equals("ssbevo") || s.equals("ssrose") || s.equals("ssroseevo") || s.equals("ui") || s.equals("godofdestruction") || s.equals(""))
            this.type = s;
        else
            throw new CustomNPCsException("Invalid type! Legal types: SSGod, SSB ,SSBEvo , SSRose, SSRoseEvo, UI, GodOfDestruction");

    }

    @Override
    public String getTexture(String textureType) {
        return null;
    }

    @Override
    public void setTexture(String textureType, String textureLocation) {

    }

    @Override
    public boolean hasColor(String colorType) {
        switch (colorType.toLowerCase()) {
            case "color1":
                return color1 != -1;
            case "color2":
                return color2 != -1;
            case "color3":
                return color3 != -1;
            case "lightning":
                return lightningColor != -1;

        }
        throw new CustomNPCsException("Invalid type! Legal types: color1, color2, color3, lightningColor");
    }

    @Override
    public void setColor(String colorType, int color) {
        switch (colorType.toLowerCase()) {
            case "color1":
                color1 = color;
                break;
            case "color2":
                color2 = color;
                break;
            case "color3":
                color3 = color;
                break;
            case "lightning":
                lightningColor = color;
                break;
            default:
                throw new CustomNPCsException("Invalid type! Legal types: color1, color2, color3, lightning");

        }

    }


    @Override
    public int getColor(String colorType) {
        switch (colorType.toLowerCase()) {
            case "color1":
                return color1;
            case "color2":
                return color2;
            case "color3":
                return color3;
            case "lightning":
                return lightningColor;
        }
        throw new CustomNPCsException("Invalid type! Legal types: color1, color2, color3, lightningColor");
    }

    @Override
    public boolean hasAlpha(String type) {
        switch (type.toLowerCase()) {
            case "aura":
                return alpha != -1;
            case "lightning":
                return lightningAlpha != -1;

        }
        throw new CustomNPCsException("Invalid type! Legal types:  aura, lightning");
    }

    @Override
    public int getAlpha() {
        switch (type.toLowerCase()) {
            case "aura":
                return alpha;
            case "lightning":
                return lightningAlpha;
        }
        throw new CustomNPCsException("Invalid type! Legal types: aura, lightning");
    }

    @Override
    public void setAlpha(String type, int value) {
        switch (type.toLowerCase()) {
            case "aura":
                alpha = value;
                break;
            case "lightning":
                lightningAlpha = value;
                break;
            default:
                throw new CustomNPCsException("Invalid type! Legal types: aura, lightning");
        }
    }

    @Override
    public void hasLightning(boolean hasLightning) {
        this.hasLightning = hasLightning;
    }

    @Override
    public boolean getHasLightning() {
        return hasLightning;
    }

    @Override
    public boolean hasSize() {
        return size != 1f;
    }

    @Override
    public float getSize() {
        return size;
    }

    @Override
    public void setSize(float size) {
        this.size = ValueUtil.clamp(size, 0.05f, 10);
    }


    @Override
    public boolean hasSpeed() {
        return speed != 1.0f;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }


    @Override
    public IAuraDisplay save() {
        if (parent != null)
            parent.save();
        return this;
    }


}
