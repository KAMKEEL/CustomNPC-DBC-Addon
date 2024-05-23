package kamkeel.npcdbc.constants.enums;

import static kamkeel.npcdbc.config.ConfigCapsules.*;

public enum EnumHealthCapsules {

    Base(0, "Base", HealthBaseStrength, HealthBaseCooldown),
    Super(1, "Super", HealthSuperStrength,HealthSuperCooldown),
    Mega(2, "Mega", HealthMegaStrength,HealthMegaCooldown),
    Giga(3, "Giga", HealthGigaStrength,HealthGigaCooldown),
    Superior(4, "Superior", HealthSuperiorStrength, HealthSuperiorCooldown),
    Master(5, "Master", HealthMasterStrength,HealthMasterCooldown);

    private final int meta;
    private final String name;
    private final int strength;
    private final int cooldown;

    private EnumHealthCapsules(int meta, String name, int strength, int cooldown){
        this.meta = meta;
        this.name = name;
        this.strength = strength;
        this.cooldown = cooldown;
    }

    public int getMeta(){
        return this.meta;
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
