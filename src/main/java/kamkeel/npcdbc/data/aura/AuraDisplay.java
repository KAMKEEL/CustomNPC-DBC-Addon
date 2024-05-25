package kamkeel.npcdbc.data.aura;

import kamkeel.npcdbc.api.aura.IAuraDisplay;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

public class AuraDisplay implements IAuraDisplay {
    public Aura parent;

    public boolean enable2DAura = true, enable3DAura = true;
    public EnumAuraTypes3D type = EnumAuraTypes3D.Default;
    public EnumAuraTypes2D type2D = EnumAuraTypes2D.Default;

    public String texture1 = "", texture2 = "", texture3 = "";
    public int color1 = -1, color2 = -1, color3 = -1, alpha = -1;
    public float size = 1.0f, speed = -1f;

    public boolean hasLightning = false;
    public int lightningColor = -1, lightningAlpha = -1, lightningSpeed = -1, lightningIntensity = -1;

    public int kaiokenColor = -1, kaiokenAlpha = -1;
    public boolean hasKaiokenAura = true, kaiokenOverrides = true;
    public float kaiokenSize = 1.1f; //kaioken size is always 1.1x bigger than aura size by default

    public boolean overrideDBCAura = false;

    public boolean kettleModeCharging, kettleModeAura;
    public byte kettleModeType = 0;

    public String auraSound = "", kaiokenSound = "";

    public AuraDisplay(Aura parent) {
        this.parent = parent;
    }


    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound rendering = compound.getCompoundTag("rendering");

        enable2DAura = rendering.getBoolean("enable2DAura");
        enable3DAura = rendering.getBoolean("enable3DAura");
        EnumAuraTypes3D auraTypes3D = EnumAuraTypes3D.getEnumFromName(rendering.getString("type"));
        type = auraTypes3D == null ? EnumAuraTypes3D.Default : auraTypes3D;

        EnumAuraTypes2D auraTypes2D = EnumAuraTypes2D.getEnumFromName(rendering.getString("type2D"));
        type2D = auraTypes2D == null ? EnumAuraTypes2D.Default : auraTypes2D;
        
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
        lightningSpeed = rendering.getInteger("lightningSpeed");
        lightningIntensity = rendering.getInteger("lightningIntensity");


        kaiokenColor = rendering.getInteger("kaiokenColor");
        kaiokenAlpha = rendering.getInteger("kaiokenAlpha");
        kaiokenSize = rendering.getFloat("kaiokenSize");
        hasKaiokenAura = rendering.getBoolean("kaiokenOn");
        kaiokenOverrides = rendering.getBoolean("kaiokenOverrides");
        overrideDBCAura = rendering.getBoolean("overrideDBCAura");

        kettleModeCharging = rendering.getBoolean("kettleModeCharging");
        kettleModeAura = rendering.getBoolean("kettleMode");
        kettleModeType = rendering.getByte("kettleModeType");

        auraSound = rendering.getString("auraSound");
        kaiokenSound = rendering.getString("kaiokenSound");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound rendering = new NBTTagCompound();

        rendering.setBoolean("enable2DAura", enable2DAura);
        rendering.setBoolean("enable3DAura", enable3DAura);
        rendering.setString("type", type.getName());
        rendering.setString("type2D", type2D.name);
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
        rendering.setInteger("lightningSpeed", lightningSpeed);
        rendering.setInteger("lightningIntensity", lightningIntensity);

        rendering.setInteger("kaiokenColor", kaiokenColor);
        rendering.setInteger("kaiokenAlpha", kaiokenAlpha);
        rendering.setFloat("kaiokenSize", kaiokenSize);
        rendering.setBoolean("kaiokenOn", hasKaiokenAura);
        rendering.setBoolean("kaiokenOverrides", kaiokenOverrides);
        rendering.setBoolean("overrideDBCAura", overrideDBCAura);

        rendering.setBoolean("kettleModeCharging", kettleModeCharging);
        rendering.setBoolean("kettleMode", kettleModeAura);
        rendering.setByte("kettleModeType", kettleModeType);

        rendering.setString("auraSound", auraSound);
        rendering.setString("kaiokenSound", kaiokenSound);

        compound.setTag("rendering", rendering);
        return compound;
    }

    @Override
    public void enable3DAura(boolean enable) {
        this.enable3DAura = enable;
    }

    @Override
    public boolean is3DAuraEnabled() {
        return this.enable3DAura;
    }

    @Override
    public void enable2DAura(boolean enable) {
        this.enable2DAura = enable;
    }

    @Override
    public boolean is2DAuraEnabled() {
        return this.enable2DAura;
    }
    @Override
    public boolean getKettleModeAura() {
        return this.kettleModeAura;
    }

    @Override
    public void setKettleModeAura(boolean set) {
        this.kettleModeAura = set;
    }

    @Override
    public boolean getKettleModeCharging() {
        return this.kettleModeCharging;
    }

    @Override
    public void setKettleModeCharging(boolean set) {
        this.kettleModeCharging = set;
    }

    @Override
    public byte getKettleModeType() {
        return this.kettleModeType;
    }

    @Override
    public void setKettleModeType(byte type) {
        this.kettleModeType = type;
    }

    @Override
    public boolean getOverrideDBCAura() {
        return overrideDBCAura;
    }

    @Override
    public void setOverrideDBCAura(boolean override) {
        this.overrideDBCAura = override;
    }

    @Override
    public void toggleKaioken(boolean toggle) {
        this.hasKaiokenAura = toggle;
    }

    @Override
    public float getKaiokenSize() {
        return kaiokenSize;
    }

    @Override
    public void setKaiokenSize(float size) {
        this.kaiokenSize = size;
    }

    @Override
    public String getKaiokenSound() {
        return kaiokenSound;
    }

    @Override
    public void setKaiokenSound(String soundDirectory) {
        this.kaiokenSound = soundDirectory;
    }

    @Override
    public boolean hasSound() {
        return auraSound.length() > 3;
    }

    @Override
    public String getAuraSound() {
        return auraSound;
    }

    @Override
    public void setAuraSound(String soundDirectory) {
        this.auraSound = soundDirectory;
    }

    public String getFinalSound() {
        String sound = "jinryuudragonbc:DBC.aura";

        if (type == EnumAuraTypes3D.SaiyanGod)
            sound = "jinryuudragonbc:1610.aurag";
        else if (type == EnumAuraTypes3D.UI)
            sound = "jinryuudragonbc:DBC5.aura_ui";
        else if (type == EnumAuraTypes3D.GoD)
            sound = "jinryuudragonbc:DBC5.aura_destroyer";
        else if (EnumAuraTypes3D.isBlue(type))
            sound = "jinryuudragonbc:1610.aurab";


        if (hasSound())
            sound = auraSound;
        return sound;

    }

    public String getFinalKKSound() {
        if (!hasKaiokenAura)
            return null;

        String sound = null;

        if (kaiokenSound.equalsIgnoreCase("nosound")) {

        } else if (kaiokenSound.isEmpty() && !kaiokenOverrides)
            sound = "jinryuudragonbc:1610.aurabk";
        else if (kaiokenOverrides)
            sound = "jinryuudragonbc:DBC.aura";
        else if (kaiokenSound.length() > 3)
            sound = kaiokenSound;

        return sound;
    }

    @Override
    public boolean isKaiokenToggled() {
        return hasKaiokenAura;
    }


    @Override
    public String getType() {
        return type.getName();
    }

    @Override
    public void setType(String type) {
        EnumAuraTypes3D s = EnumAuraTypes3D.getEnumFromName(type.toLowerCase());
        if (s == null)
            throw new CustomNPCsException("Invalid type! Legal types: %s", String.join(", ", EnumAuraTypes3D.getAllNames()));
        this.type = s;
    }

    @Override
    public String getType2D() {
        return type2D.name;
    }

    @Override
    public void setType2D(String type2D) {
        EnumAuraTypes2D s = EnumAuraTypes2D.getEnumFromName(type2D.toLowerCase());
        if (s == null)
            throw new CustomNPCsException("Invalid type! Legal types: %s", String.join(", ", EnumAuraTypes2D.getAllNames()));
        this.type2D = s;
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
                return color1 > -1;
            case "color2":
                return color2 > -1;
            case "color3":
                return color3 > -1;
            case "lightning":
                return lightningColor > -1;
            case "kaioken":
                return kaiokenColor > -1;

        }
        throw new CustomNPCsException("Invalid type! Legal types: color1, color2, color3, lightningColor, kaioken");
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
            case "kaioken":
                kaiokenColor = color;
                break;
            default:
                throw new CustomNPCsException("Invalid type! Legal types: color1, color2, color3, lightning, kaioken");

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
            case "kaioken":
                return kaiokenColor;
        }
        throw new CustomNPCsException("Invalid type! Legal types: color1, color2, color3, lightningColor, kaioken");
    }

    @Override
    public boolean hasAlpha(String type) {
        switch (type.toLowerCase()) {
            case "aura":
                return alpha > -1;
            case "lightning":
                return lightningAlpha > -1;
            case "kaioken":
                return kaiokenAlpha > -1;

        }
        throw new CustomNPCsException("Invalid type! Legal types:  aura, lightning, kaioken");
    }

    @Override
    public int getAlpha(String type) {
        switch (type.toLowerCase()) {
            case "aura":
                return alpha;
            case "lightning":
                return lightningAlpha;
            case "kaioken":
                return kaiokenAlpha;
        }
        throw new CustomNPCsException("Invalid type! Legal types: aura, lightning, kaioken");
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
            case "kaioken":
                kaiokenAlpha = value;
                break;
            default:
                throw new CustomNPCsException("Invalid type! Legal types: aura, lightning, kaioken");
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
    public int getLightningSpeed() {
        return lightningSpeed;
    }

    @Override
    public void setLightningSpeed(int lightningSpeed) {
        this.lightningSpeed = lightningSpeed;
    }

    @Override
    public int getLightningIntensity() {
        return lightningIntensity;
    }

    @Override
    public void setLightningIntensity(int lightningIntensity) {
        if (lightningIntensity != -1)
            lightningIntensity = ValueUtil.clamp(lightningIntensity, 1, 8);
        this.lightningIntensity = lightningIntensity;
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
        return speed > 0;
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
