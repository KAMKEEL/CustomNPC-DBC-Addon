package kamkeel.npcdbc.constants.enums;

import static kamkeel.npcdbc.config.ConfigCapsules.KiBaseCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.KiBaseStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.KiGigaCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.KiGigaStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.KiMasterCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.KiMasterStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.KiMegaCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.KiMegaStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.KiSuperCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.KiSuperStrength;
import static kamkeel.npcdbc.config.ConfigCapsules.KiSuperiorCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.KiSuperiorStrength;

public enum EnumKiCapsules {

    Base("Base", KiBaseStrength, KiBaseCooldown),
    Super("Super", KiSuperStrength, KiSuperCooldown),
    Mega("Mega", KiMegaStrength, KiMegaCooldown),
    Giga("Giga", KiGigaStrength, KiGigaCooldown),
    Superior("Superior", KiSuperiorStrength, KiSuperiorCooldown),
    Master("Master", KiMasterStrength, KiMasterCooldown);

    private final String name;
    private final int strength;
    private final int cooldown;

    EnumKiCapsules(String name, int strength, int cooldown) {
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
