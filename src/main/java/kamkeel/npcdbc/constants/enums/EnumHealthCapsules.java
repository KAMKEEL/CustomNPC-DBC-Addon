package kamkeel.npcdbc.constants.enums;

import static kamkeel.npcdbc.config.ConfigCapsules.*;

public enum EnumHealthCapsules {

    Base("Base", HealthBaseStrength, HealthBaseCooldown),
    Super("Super", HealthSuperStrength,HealthSuperCooldown),
    Mega("Mega", HealthMegaStrength,HealthMegaCooldown),
    Giga("Giga", HealthGigaStrength,HealthGigaCooldown),
    Superior("Superior", HealthSuperiorStrength, HealthSuperiorCooldown),
    Master("Master", HealthMasterStrength,HealthMasterCooldown);

    private final String name;
    private final int strength;
    private final int cooldown;

    EnumHealthCapsules(String name, int strength, int cooldown){;
        this.name = name;
        this.strength = strength;
        this.cooldown = cooldown;
    }

    public int getMeta(){
        return this.ordinal();
    }

    public String getName(){
        return this.name;
    }

    public int getStrength(){
        return this.strength;
    }

    public int getCooldown(){
        return this.cooldown;
    }

    public static int count(){
        return values().length;
    }
}
