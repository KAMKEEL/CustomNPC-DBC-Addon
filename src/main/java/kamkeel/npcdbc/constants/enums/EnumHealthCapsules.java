package kamkeel.npcdbc.constants.enums;

import static kamkeel.npcdbc.config.ConfigCapsules.HealthBaseCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.HealthBaseStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.HealthGigaCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.HealthGigaStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.HealthMasterCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.HealthMasterStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.HealthMegaCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.HealthMegaStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.HealthSuperCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.HealthSuperStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.HealthSuperiorCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.HealthSuperiorStrength;

public enum EnumHealthCapsules {

    Base("Base", HealthBaseStrength, HealthBaseCooldown),
    Super("Super", HealthSuperStrength, HealthSuperCooldown),
    Mega("Mega", HealthMegaStrength, HealthMegaCooldown),
    Giga("Giga", HealthGigaStrength, HealthGigaCooldown),
    Superior("Superior", HealthSuperiorStrength, HealthSuperiorCooldown),
    Master("Master", HealthMasterStrength, HealthMasterCooldown);

    private final String name;
    private final int strength;
    private final int cooldown;

    EnumHealthCapsules(String name, int strength, int cooldown) {
        ;
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
