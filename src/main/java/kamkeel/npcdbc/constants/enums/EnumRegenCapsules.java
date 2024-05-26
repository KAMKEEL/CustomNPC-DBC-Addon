package kamkeel.npcdbc.constants.enums;

import kamkeel.npcdbc.constants.Effects;

public enum EnumRegenCapsules {
    // @TODO: Add proper configs
    BaseHP(0, "base_hp", Effects.REGEN_HEALTH, (byte) 1, 30, 4, 4),
    SuperHP(1, "super_hp", Effects.REGEN_HEALTH, (byte) 2, 20, 4, 4),
    MegaHP(2, "mega_hp", Effects.REGEN_HEALTH, (byte) 3, 10, 4, 4),
    BaseKi(3, "base_ki", Effects.REGEN_KI, (byte) 1, 30, 4, 4),
    SuperKi(4, "super_ki", Effects.REGEN_KI, (byte) 2, 20, 4, 4),
    MegaKi(5, "mega_ki", Effects.REGEN_KI, (byte) 3, 10, 4, 4),
    BaseStamina(6, "base_stamina", Effects.REGEN_STAMINA, (byte) 1, 30, 4, 4),
    SuperStamina(7, "super_stamina", Effects.REGEN_STAMINA, (byte) 2, 20, 4, 4),
    MegaStamina(8, "mega_stamina", Effects.REGEN_STAMINA, (byte) 3, 10, 4, 4);

    private final int meta;
    private final String name;
    private final int statusEffectId;
    private final byte strength;
    private final int useTime;
    private final int cooldownType;
    private final int cooldown;

    EnumRegenCapsules(int meta, String name, int statusEffectId, byte strength, int useTime, int cooldownType, int cooldown){
        this.meta = meta;
        this.name = name;
        this.statusEffectId = statusEffectId;
        this.strength = strength;
        this.useTime = useTime;
        this.cooldownType = cooldownType;
        this.cooldown = cooldown;
    }
    public int getMeta(){
        return this.meta;
    }

    public String getName(){
        return this.name;
    }

    public byte getStrength(){
        return (byte) this.strength;
    }

    public int getCooldownType() {
        return cooldownType;
    }

    public int getCooldown(){
        return this.cooldown;
    }
    public int getStatusEffectId(){
        return statusEffectId;
    }

    public static int count(){
        return values().length;
    }

    public int getUseTime() {
        return this.useTime;
    }
}
