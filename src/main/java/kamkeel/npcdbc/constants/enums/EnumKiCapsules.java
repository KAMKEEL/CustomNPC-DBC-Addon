package kamkeel.npcdbc.constants.enums;

import static kamkeel.npcdbc.config.ConfigCapsules.*;

public enum EnumKiCapsules {

    Base(0, "Base", KiBaseStrength, KiBaseCooldown),
    Super(1, "Super", KiSuperStrength,KiSuperCooldown),
    Mega(2, "Mega", KiMegaStrength,KiMegaCooldown),
    Giga(3, "Giga", KiGigaStrength,KiGigaCooldown),
    Superior(4, "Superior", KiSuperiorStrength, KiSuperiorCooldown),
    Master(5, "Master", KiMasterStrength,KiMasterCooldown);

    private final int meta;
    private final String name;
    private final int strength;
    private final int cooldown;

    private EnumKiCapsules(int meta, String name, int strength, int cooldown){
        this.meta = meta;
        this.name = name;
        this.strength = strength;
        this.cooldown = 10;
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
