package kamkeel.npcdbc.constants.enums;

public enum EnumRegenCapsules {
    // @TODO: Add proper configs
    BaseHP(0, "base_hp", 2, 4, 4);

    ;

    private final int meta;
    private final String name;
    private final int strength;
    private final int cooldownType;
    private final int cooldown;

    EnumRegenCapsules(int meta, String name, int strength, int cooldownType, int cooldown){
        this.meta = meta;
        this.name = name;
        this.strength = strength;
        this.cooldownType = cooldownType;
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

    public int getCooldownType() {
        return cooldownType;
    }

    public int getCooldown(){
        return this.cooldown;
    }

    public static int count(){
        return values().length;
    }
}
