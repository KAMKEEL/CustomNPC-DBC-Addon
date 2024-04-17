package kamkeel.npcdbc.constants;

import static kamkeel.npcdbc.config.ConfigCapsules.*;

public enum EnumKiCapsules {

    Base(0, "Base", KiBaseStrength, KiBaseCooldown, "5%"),
    Super(1, "Super", KiSuperStrength,KiSuperCooldown, "10%"),
    Mega(2, "Mega", KiMegaStrength,KiMegaCooldown, "25"),
    Giga(3, "Giga", KiGigaStrength,KiGigaCooldown, "50%"),
    Supreme(4, "Supreme", KiSuperStrength,KiSupremeCooldown, "75%"),
    Master(5, "Master", KiMasterStrength,KiMasterCooldown, "100%");

    private final int meta;
    private final String name;
    private final int strength;
    private final int cooldown;

    private EnumKiCapsules(int meta, String name, int strength, int cooldown, String lore){
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
