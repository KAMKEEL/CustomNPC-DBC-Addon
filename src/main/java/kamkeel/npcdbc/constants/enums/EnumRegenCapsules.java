package kamkeel.npcdbc.constants.enums;

import kamkeel.npcdbc.constants.Effects;

import static kamkeel.npcdbc.config.ConfigCapsules.*;


public enum EnumRegenCapsules {
    BaseHP(0, "base_hp", Effects.REGEN_HEALTH, RegenHPBaseStrength, RegenHPBaseUseTime, RegenHPBaseCooldown),
    SuperHP(1, "super_hp", Effects.REGEN_HEALTH, RegenHPSuperStrength, RegenHPSuperUseTime, RegenHPSuperCooldown),
    MegaHP(2, "mega_hp", Effects.REGEN_HEALTH, RegenHPMegaStrength, RegenHPMegaUseTime, RegenHPMegaCooldown),
    BaseKi(3, "base_ki", Effects.REGEN_KI, RegenKiBaseStrength, RegenKiBaseUseTime, RegenKiBaseCooldown),
    SuperKi(4, "super_ki", Effects.REGEN_KI, RegenKiSuperStrength, RegenKiSuperUseTime, RegenKiSuperCooldown),
    MegaKi(5, "mega_ki", Effects.REGEN_KI, RegenKiMegaStrength, RegenKiMegaUseTime, RegenKiMegaCooldown),
    BaseStamina(6, "base_stamina", Effects.REGEN_STAMINA, RegenStaminaBaseStrength, RegenStaminaBaseUseTime, RegenStaminaBaseCooldown),
    SuperStamina(7, "super_stamina", Effects.REGEN_STAMINA, RegenStaminaSuperStrength, RegenStaminaSuperUseTime, RegenStaminaSuperCooldown),
    MegaStamina(8, "mega_stamina", Effects.REGEN_STAMINA, RegenStaminaMegaStrength, RegenStaminaMegaUseTime, RegenStaminaMegaCooldown);

    private final int meta;
    private final String name;
    private final int statusEffectId;
    private final byte strength;
    private final int useTime;
    private final int cooldown;

    EnumRegenCapsules(int meta, String name, int statusEffectId, byte strength, int useTime, int cooldown){
        this.meta = meta;
        this.name = name;
        this.statusEffectId = statusEffectId;
        this.strength = strength;
        this.useTime = useTime;
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
