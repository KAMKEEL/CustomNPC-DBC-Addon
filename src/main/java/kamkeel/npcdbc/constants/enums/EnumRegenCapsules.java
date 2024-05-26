package kamkeel.npcdbc.constants.enums;

public enum EnumRegenCapsules {
    // @TODO: Add proper configs
    BaseHP(0, "base_hp", 1, 4, 4),
    SuperHP(0, "super_hp", 2, 4, 4),
    MegaHP(0, "mega_hp", 3, 4, 4),
    BaseKi(0, "base_ki", 1, 4, 4),
    SuperKi(0, "super_ki", 2, 4, 4),
    MegaKi(0, "mega_ki", 3, 4, 4),
    BaseStamina(0, "base_stamina", 1, 4, 4),
    SuperStamina(0, "super_stamina", 2, 4, 4),
    MegaStamina(0, "mega_stamina", 3, 4, 4);

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
