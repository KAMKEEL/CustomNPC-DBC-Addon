package kamkeel.npcdbc.constants.enums;

import kamkeel.npcdbc.constants.Effects;

import static kamkeel.npcdbc.config.ConfigCapsules.RegenHPBaseCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenHPBaseStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenHPBaseUseTime;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenHPMegaCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenHPMegaStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenHPMegaUseTime;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenHPSuperCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenHPSuperStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenHPSuperUseTime;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenKiBaseCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenKiBaseStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenKiBaseUseTime;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenKiMegaCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenKiMegaStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenKiMegaUseTime;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenKiSuperCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenKiSuperStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenKiSuperUseTime;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenStaminaBaseCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenStaminaBaseStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenStaminaBaseUseTime;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenStaminaMegaCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenStaminaMegaStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenStaminaMegaUseTime;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenStaminaSuperCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenStaminaSuperStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.RegenStaminaSuperUseTime;


public enum EnumRegenCapsules {
    BaseHP("base_hp", Effects.REGEN_HEALTH, RegenHPBaseStrength, RegenHPBaseUseTime, RegenHPBaseCooldown),
    SuperHP("super_hp", Effects.REGEN_HEALTH, RegenHPSuperStrength, RegenHPSuperUseTime, RegenHPSuperCooldown),
    MegaHP("mega_hp", Effects.REGEN_HEALTH, RegenHPMegaStrength, RegenHPMegaUseTime, RegenHPMegaCooldown),
    BaseKi("base_ki", Effects.REGEN_KI, RegenKiBaseStrength, RegenKiBaseUseTime, RegenKiBaseCooldown),
    SuperKi("super_ki", Effects.REGEN_KI, RegenKiSuperStrength, RegenKiSuperUseTime, RegenKiSuperCooldown),
    MegaKi("mega_ki", Effects.REGEN_KI, RegenKiMegaStrength, RegenKiMegaUseTime, RegenKiMegaCooldown),
    BaseStamina("base_stamina", Effects.REGEN_STAMINA, RegenStaminaBaseStrength, RegenStaminaBaseUseTime, RegenStaminaBaseCooldown),
    SuperStamina("super_stamina", Effects.REGEN_STAMINA, RegenStaminaSuperStrength, RegenStaminaSuperUseTime, RegenStaminaSuperCooldown),
    MegaStamina("mega_stamina", Effects.REGEN_STAMINA, RegenStaminaMegaStrength, RegenStaminaMegaUseTime, RegenStaminaMegaCooldown);

    private final String name;
    private final int statusEffectId;
    private final byte strength;
    private final int useTime;
    private final int cooldown;

    EnumRegenCapsules(String name, int statusEffectId, byte strength, int useTime, int cooldown) {
        this.name = name;
        this.statusEffectId = statusEffectId;
        this.strength = strength;
        this.useTime = useTime;
        this.cooldown = cooldown;
    }

    public int getMeta() {
        return this.ordinal();
    }

    public String getName() {
        return this.name;
    }

    public int getStrength() {
        return this.strength;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public int getStatusEffectId() {
        return statusEffectId;
    }

    public static int count() {
        return values().length;
    }

    public int getEffectTime() {
        return this.useTime;
    }
}
