package kamkeel.npcdbc.constants.enums;

import static kamkeel.npcdbc.config.ConfigCapsules.StaminaBaseCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.StaminaBaseStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.StaminaGigaCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.StaminaGigaStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.StaminaMasterCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.StaminaMasterStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.StaminaMegaCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.StaminaMegaStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.StaminaSuperCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.StaminaSuperStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.StaminaSuperiorCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.StaminaSuperiorStrength;

public enum EnumStaminaCapsules {

    Base("Base", StaminaBaseStrength, StaminaBaseCooldown),
    Super("Super", StaminaSuperStrength, StaminaSuperCooldown),
    Mega("Mega", StaminaMegaStrength, StaminaMegaCooldown),
    Giga("Giga", StaminaGigaStrength, StaminaGigaCooldown),
    Superior("Superior", StaminaSuperiorStrength, StaminaSuperiorCooldown),
    Master("Master", StaminaMasterStrength, StaminaMasterCooldown);

    private final String name;
    private final int strength;
    private final int cooldown;

    EnumStaminaCapsules(String name, int strength, int cooldown) {
        this.name = name;
        this.strength = strength;
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

    public static int count() {
        return values().length;
    }
}
