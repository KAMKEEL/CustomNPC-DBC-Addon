package kamkeel.npcdbc.constants;

import static kamkeel.npcdbc.config.ConfigCapsules.*;

public enum EnumStaminaCapsules {

    Base(0, "Base", StaminaBaseStrength, StaminaBaseCooldown),
    Super(1, "Super", StaminaSuperStrength,StaminaSuperCooldown),
    Mega(2, "Mega", StaminaMegaStrength,StaminaMegaCooldown),
    Giga(3, "Giga", StaminaGigaStrength,StaminaGigaCooldown),
    Supreme(4, "Supreme", StaminaSuperStrength,StaminaSupremeCooldown),
    Master(5, "Master", StaminaMasterStrength,StaminaMasterCooldown);

    private final int meta;
    private final String name;
    private final int strength;
    private final int cooldown;

    private EnumStaminaCapsules(int meta, String name, int strength, int cooldown){
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
