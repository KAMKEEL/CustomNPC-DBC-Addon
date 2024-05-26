package kamkeel.npcdbc.constants.enums;

import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;

public enum EnumRegenCapsules {
    // @TODO: Add proper configs
    BaseHP(0, "base_hp", Effects.REGEN_HEALTH, 1, 4, 4),
    SuperHP(1, "super_hp", Effects.REGEN_HEALTH, 2, 4, 4),
    MegaHP(2, "mega_hp", Effects.REGEN_HEALTH, 3, 4, 4),
    BaseKi(3, "base_ki", Effects.REGEN_KI, 1, 4, 4),
    SuperKi(4, "super_ki", Effects.REGEN_KI, 2, 4, 4),
    MegaKi(5, "mega_ki", Effects.REGEN_KI, 3, 4, 4),
    BaseStamina(6, "base_stamina", Effects.REGEN_STAMINA, 1, 4, 4),
    SuperStamina(7, "super_stamina", Effects.REGEN_STAMINA, 2, 4, 4),
    MegaStamina(8, "mega_stamina", Effects.REGEN_STAMINA, 3, 4, 4);

    private final int meta;
    private final String name;
    private final int statusEffectId;
    private final int strength;
    private final int cooldownType;
    private final int cooldown;

    EnumRegenCapsules(int meta, String name, int statusEffectId, int strength, int cooldownType, int cooldown){
        this.meta = meta;
        this.name = name;
        this.statusEffectId = statusEffectId;
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
    public int getStatusEffect(){
        return statusEffectId;
    }

    public static int count(){
        return values().length;
    }
}
